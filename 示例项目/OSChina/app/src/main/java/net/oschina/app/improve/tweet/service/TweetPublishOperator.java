package net.oschina.app.improve.tweet.service;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.upyun.library.common.UploadEngine;
import com.upyun.library.listener.UpCompleteListener;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.resource.ImageResource;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.utils.MD5;
import net.oschina.app.improve.utils.PicturesCompressor;
import net.oschina.common.utils.BitmapUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by JuQiu
 * on 16/7/21.
 * 动弹发布执行者
 */
@SuppressWarnings("unused")
class TweetPublishOperator implements Runnable, Contract.IOperator {
    private final int serviceStartId;
    private final int notificationId;
    private Contract.IService service;
    private TweetPublishModel model;

    interface UploadImageCallback {
        void onUploadImageDone();

        void onUploadImage(int index, String token);
    }

    TweetPublishOperator(TweetPublishModel model, Contract.IService service, int startId) {
        this.model = model;
        this.notificationId = model.getId().hashCode();
        this.serviceStartId = startId;
        this.service = service;
    }

    /**
     * 执行动弹发布操作
     */
    @Override
    public void run() {
        // call to service
        this.service.start(model.getId(), this);
        // notify
        notifyMsg(R.string.tweet_publishing);
        // doing
        final TweetPublishModel model = this.model;
        if (model.getSrcImages() == null && model.getCacheImages() == null) {
            // 当没有图片的时候,直接进行发布动弹
            publish();
        } else {
            if (model.getCacheImages() == null) {
                notifyMsg(R.string.tweet_image_wait);
                final String cacheDir = service.getCachePath(model.getId());
                // change the model
                model.setCacheImages(saveImageToCache(cacheDir, model.getSrcImages()));
                // update to cache file
                service.updateModelCache(model.getId(), model);

                if (model.getCacheImages() == null) {
                    notifyMsg(R.string.tweet_image_wait_failed);
                    //图片转存失败，注册失败广播
                    AppContext.getInstance().sendBroadcast(new Intent(TweetPublishService.ACTION_FAILED));
                    publish();
                    return;
                }
            }
            // 开始上传图片,并回调进度
            uploadImages(model.getCacheImagesIndex(), model.getCacheImagesToken(), model.getCacheImages(),
                    new UploadImageCallback() {
                        @Override
                        public void onUploadImageDone() {
                            publish();
                        }

                        @Override
                        public void onUploadImage(int index, String token) {
                            model.setCacheImagesInfo(index, token);
                            // update to cache file
                            service.updateModelCache(model.getId(), model);
                        }
                    });
//            getTokenAndUpload(model.getCacheImagesIndex(), model.getCacheImages(), new
//                    UploadImageCallback() {
//                        @Override
//                        public void onUploadImageDone() {
//                            publish();
//                        }
//
//                        @Override
//                        public void onUploadImage(int index, String token) {
//                            model.setCacheImagesInfo(index, token);
//                            service.updateModelCache(model.getId(), model);
//                        }
//                    });

        }
    }


    /**
     * 步骤1，获取又拍凭证并上传文件
     *
     * @param index    index
     * @param paths    paths
     * @param callback callback
     */
    private void getTokenAndUpload(final int index, final String[] paths, final UploadImageCallback callback) {
        OSChinaApi.getYPToken(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                String error = "";
                String response = "上传失败";
                TweetPublishService.log(String.format("Upload image onFailure, statusCode:[%s] responseString:%s throwable:%s",
                        "error", response, error));
                setError(R.string.tweet_image_publish_failed, String.valueOf(index + 1), String.valueOf(paths.length));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<YoupaiToken>>() {
                    }.getType();
                    ResultBean<YoupaiToken> resultBean = new Gson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        upload(index, resultBean.getResult(), paths, callback);
                    } else {
                        saveError("Upload", resultBean.getMessage());
                        onFailure(statusCode, headers, responseString, null);
                    }
                } catch (Exception e) {
                    saveError("Upload", "response parse error「" + responseString + "」");
                    onFailure(statusCode, headers, responseString, null);
                }
            }
        });
    }


    private List<YouPaiResult> mImages = new ArrayList<>();

    private void upload(final int index, final YoupaiToken token, final String[] paths, final UploadImageCallback runnable) {
        // checkShare done
        if (index < 0 || index >= paths.length) {
            uploadTokenUrl(model.getCacheImagesToken(), 0, runnable);
            return;
        }

        final String path = paths[index];

        File file = new File(path);
        String saveKey = String.format("app_%s.%s", MD5.get32MD5Str(AccountHelper.getUserId() + file.getName() + System.currentTimeMillis()), PicturesCompressor.getFileDiff(file));

        HashMap<String, Object> map = new HashMap<>();
        map.put("bucket", "oscnet");
        map.put("save-key", saveKey);
        map.put("expiration", System.currentTimeMillis());

        UploadEngine.getInstance()
                .formUpload(file, map, token.getOperator(), token.getSecret(),
                        new UpCompleteListener() {
                            @Override
                            public void onComplete(boolean isSuccess, String result) {
                                Log.e("result", "result" + result + "  --  " + isSuccess);
                                try {
                                    YouPaiResult bean = new Gson().fromJson(result, YouPaiResult.class);
                                    if (bean != null) {
                                        if (bean.getCode() == 200) {
                                            mImages.add(bean);
                                            upload(index + 1, token, paths, runnable);
                                        } else {
                                            String error = "";
                                            String response = bean.getMessage();
                                            TweetPublishService.log(String.format("Upload image onFailure, statusCode:[%s] responseString:%s throwable:%s",
                                                    bean.getCode(), response, error));
                                            setError(R.string.tweet_image_publish_failed, String.valueOf(index + 1), String.valueOf(paths.length));
                                        }
                                    } else {
                                        String error = "";
                                        String response = "上传失败";
                                        TweetPublishService.log(String.format("Upload image onFailure, statusCode:[%s] responseString:%s throwable:%s",
                                                "error", response, error));
                                        setError(R.string.tweet_image_publish_failed, String.valueOf(index + 1), String.valueOf(paths.length));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, null);
    }


    /**
     * 将图片资源告诉后台
     */
    private void uploadTokenUrl(String token, final int index, final UploadImageCallback runnable) {
        runnable.onUploadImage(index, token);
        if (index >= mImages.size()) {
            runnable.onUploadImageDone();
            return;
        }
        OSChinaApi.uploadImageForYouPai(token, mImages.get(index),
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        String error = "";
                        String response = responseString == null ? "" : responseString;
                        if (throwable != null) {
                            throwable.printStackTrace();
                            error = throwable.getMessage();
                            if (error.contains("UnknownHostException")
                                    || error.contains("Read error: ssl")
                                    || error.contains("Connection timed out")) {
                                saveError("Upload", "network error");
                            } else {
                                saveError("Upload", response + " " + error);
                            }
                        }
                        TweetPublishService.log(String.format("Upload image onFailure, statusCode:[%s] responseString:%s throwable:%s",
                                statusCode, response, error));
                        setError(R.string.tweet_image_publish_failed, String.valueOf(index + 1), String.valueOf(mImages.size()));
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Type type = new TypeToken<ResultBean<ImageResource>>() {
                            }.getType();
                            ResultBean<ImageResource> resultBean = new Gson().fromJson(responseString, type);
                            if (resultBean.isSuccess()) {
                                String token = resultBean.getResult().getToken();
                                uploadTokenUrl(token, index + 1, runnable);
                            } else {
                                saveError("Upload", resultBean.getMessage());
                                onFailure(statusCode, headers, responseString, null);
                            }
                        } catch (Exception e) {
                            saveError("Upload", "response parse error「" + responseString + "」");
                            onFailure(statusCode, headers, responseString, null);
                        }
                    }
                });
    }


    /**
     * 上传图片
     *
     * @param index    上次图片的坐标
     * @param token    上传Token
     * @param paths    上传的路径数组
     * @param runnable 完全上传完成时回调
     */
    private void uploadImages(final int index, final String token, final String[] paths, final UploadImageCallback runnable) {
        // call progress
        runnable.onUploadImage(index, token);

        // checkShare done
        if (index < 0 || index >= paths.length) {
            runnable.onUploadImageDone();
            return;
        }

        final String path = paths[index];

        OSChinaApi.uploadImage(token, path, new LopperResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                notifyMsg(R.string.tweet_image_publishing, String.valueOf(paths.length - index));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                String error = "";
                String response = responseString == null ? "" : responseString;
                if (throwable != null) {
                    throwable.printStackTrace();
                    error = throwable.getMessage();
                    if (error.contains("UnknownHostException")
                            || error.contains("Read error: ssl")
                            || error.contains("Connection timed out")) {
                        saveError("Upload", "network error");
                    } else {
                        saveError("Upload", response + " " + error);
                    }
                }
                TweetPublishService.log(String.format("Upload image onFailure, statusCode:[%s] responseString:%s throwable:%s",
                        statusCode, response, error));
                setError(R.string.tweet_image_publish_failed, String.valueOf(index + 1), String.valueOf(paths.length));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                try {
                    Type type = new TypeToken<ResultBean<ImageResource>>() {
                    }.getType();
                    ResultBean<ImageResource> resultBean = new Gson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        String token = resultBean.getResult().getToken();
                        uploadImages(index + 1, token, paths, runnable);
                    } else {
                        File file = new File(path);
                        TweetPublishService.log(String.format("Upload name:[%s] size:[%s] error:%s",
                                file.getAbsolutePath(), file.length(), resultBean.getMessage()));
                        saveError("Upload", resultBean.getMessage());
                        onFailure(statusCode, headers, responseString, null);
                    }
                } catch (Exception e) {
                    saveError("Upload", "response parse error「" + responseString + "」");
                    onFailure(statusCode, headers, responseString, null);
                }
            }
        });
    }

    @Override
    public void stop() {
        final Contract.IService service = this.service;
        if (service != null) {
            this.service = null;
            service.stop(model.getId(), serviceStartId);
        }
    }

    /**
     * 发布动弹
     */
    private void publish() {
        OSChinaApi.pubTweet(model.getContent(), model.getCacheImagesToken(), null, model.getAboutShare(), new LopperResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                String error = "";
                String response = responseString == null ? "" : responseString;
                if (throwable != null) {
                    throwable.printStackTrace();
                    error = throwable.getMessage();
                    if (error.contains("UnknownHostException")
                            || error.contains("Read error: ssl")
                            || error.contains("Connection timed out")) {
                        saveError("Publish", "network error");
                    } else {
                        saveError("Publish", response + " " + error);
                    }
                }

                TweetPublishService.log(String.format("Publish tweet onFailure, statusCode:[%s] responseString:%s throwable:%s",
                        statusCode, response, error));
                setError(R.string.tweet_publish_failed);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean>() {
                    }.getType();
                    ResultBean resultBean = new Gson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        setSuccess();
                    } else {
                        saveError("Publish", resultBean.getMessage());
                        onFailure(statusCode, headers, responseString, null);
                    }
                } catch (Exception e) {
                    saveError("Publish", "response parse error「" + responseString + "」");
                    onFailure(statusCode, headers, responseString, null);
                }
            }
        });
    }

    private void notifyMsg(int resId, Object... values) {
        notifyMsg(false, resId, values);
    }

    private void notifyMsg(boolean done, int resId, Object... values) {
        Contract.IService service = this.service;
        if (service != null) {
            service.notifyMsg(notificationId, model.getId(), done, done, resId, values);
        }
    }

    private void setSuccess() {
        AppContext.getInstance().sendBroadcast(new Intent(TweetPublishService.ACTION_SUCCESS));
        notifyMsg(R.string.tweet_publish_success);
        try {
            Thread.sleep(1600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Contract.IService service = this.service;
        if (service != null) {
            // clear the cache
            service.updateModelCache(model.getId(), null);
            // hide the notify
            service.notifyCancel(notificationId);
        }

        // Check the about commit id
        if (!checkToCommit())
            stop();
    }

    private void setError(int resId, Object... values) {
        AppContext.getInstance().sendBroadcast(new Intent(TweetPublishService.ACTION_FAILED));
        notifyMsg(true, resId, values);
        stop();
    }

    private boolean checkToCommit() {
        // 如果相关节点中定义了评论参数，那么将执行评论
        About.Share share = model.getAboutShare();
        if (About.check(share) && share.commitTweetId > 0) {
            OSChinaApi.pubTweetComment(share.commitTweetId, model.getContent(), 0, new LopperResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    stop();
                }
            });
            return true;
        }
        return false;
    }


    // Max upload 860KB/3M
    private static final long MAX_UPLOAD_LENGTH = 3072 * 1024;

    /**
     * 保存文件到缓存中
     *
     * @param cacheDir 缓存文件夹
     * @param paths    原始路径
     * @return 转存后的路径
     */
    private static String[] saveImageToCache(String cacheDir, String[] paths) {
        List<String> ret = new ArrayList<>();
        //byte[] buffer = new byte[BitmapUtil.DEFAULT_BUFFER_SIZE];
        BitmapFactory.Options options = BitmapUtil.createOptions();
        for (final String path : paths) {
            String ext = null;
            try {
                int lastDotIndex = path.lastIndexOf(".");
                if (lastDotIndex != -1)
                    ext = path.substring(lastDotIndex + 1).toLowerCase();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (TextUtils.isEmpty(ext)) {
                ext = "jpg";
            }

            try {
                String tempFile = String.format("%s/IMG_%s.%s", cacheDir, SystemClock.currentThreadTimeMillis(), ext);
                if (PicturesCompressor.compressImage(path, tempFile, MAX_UPLOAD_LENGTH,
                        80, 1280, 1280 * 16, null, options, true)) {
                    TweetPublishService.log("OPERATOR doImage:" + tempFile + " " + new File(tempFile).length());
                    // verify the picture ext.
                    tempFile = PicturesCompressor.verifyPictureExt(tempFile);
                    ret.add(tempFile);
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            TweetPublishService.log("OPERATOR compressImage error:" + path);
        }
        if (ret.size() > 0) {
            String[] images = new String[ret.size()];
            ret.toArray(images);
            return images;
        }
        return null;
    }

    private void saveError(String cmd, String log) {
        AppContext.getInstance().sendBroadcast(new Intent(TweetPublishService.ACTION_FAILED));
        model.setErrorString(String.format("%s | %s", cmd, log));
        // update to cache file save error log
        service.updateModelCache(model.getId(), model);
    }
}
