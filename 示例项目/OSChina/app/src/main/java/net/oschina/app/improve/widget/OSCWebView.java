package net.oschina.app.improve.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.oschina.app.improve.main.synthesize.web.Rule;

/**
 * 浏览器,视频、图片都支持
 * Created by huanghaibin on 2017/10/27.
 */
@SuppressWarnings("unused")
public class OSCWebView extends WebView {

    private OnFinishListener mOnFinishFinish;
    private OnLoadedHtmlListener mHTMLListener;
    private OnImageClickListener mImageClickListener;
    private OnVideoClickListener mVideoClickListener;
    private boolean isFinish;
    private Rule mRule;
    private boolean isRemove;

    public OSCWebView(Context context) {
        this(context, null);
    }

    public OSCWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //hideAD();
                if (mOnFinishFinish != null) {
                    mOnFinishFinish.onReceivedTitle(title);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (!isRemove) {
                    isRemove = true;
                    startLoadRule();
                }
                if (mOnFinishFinish != null) {
                    mOnFinishFinish.onProgressChange(newProgress);
                }
            }

        });

        setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                exeExpandRule();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isFinish = true;
                    }
                }, 2000);
                if (mOnFinishFinish != null) {
                    mOnFinishFinish.onFinish();
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (mOnFinishFinish != null) {
                    mOnFinishFinish.onError();
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }
        });

        getSettings().setDomStorageEnabled(true);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getSettings().setJavaScriptEnabled(true);
        addJavascriptInterface(new JavascriptInterface(), "mark");

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                OSCWebView webView = (OSCWebView) v;
                HitTestResult result = webView.getHitTestResult();
                if (null == result)
                    return false;

                int type = result.getType();
                if (type == WebView.HitTestResult.UNKNOWN_TYPE)
                    return false;

                if (type == WebView.HitTestResult.EDIT_TEXT_TYPE) {
                    // let TextViewhandles context menu
                    return false;
                }


                // Setup custom handlingdepending on the type
                switch (type) {
                    case WebView.HitTestResult.PHONE_TYPE:
                        // 处理拨号
                        break;
                    case WebView.HitTestResult.EMAIL_TYPE:
                        // 处理Email
                        break;
                    case WebView.HitTestResult.GEO_TYPE:
                        // TODO
                        break;
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE:
                        // 超链接
                        break;
                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                    case WebView.HitTestResult.IMAGE_TYPE:
                        // 处理长按图片的菜单项
                        break;
                    default:
                        break;
                }

                return false;
            }
        });
    }

    @SuppressWarnings("all")
    public void setUserAgent(String ua) {
        getSettings().setUserAgentString(ua);
    }

    /**
     * 去除广告规则
     */
    private void exeRemoveRule() {
        if (mRule == null)
            return;
        String[] rules = mRule.getRemoveRules();
        if (rules == null || rules.length == 0)
            return;
        for (String rule : rules) {
            evaluateJavascript(rule, null);
        }
    }

    /**
     * 展开全文规则
     */
    private void exeExpandRule() {
        if (mRule == null)
            return;
        String[] rules = mRule.getExpandRules();
        if (rules == null || rules.length == 0)
            return;
        for (String rule : rules) {
            evaluateJavascript(rule, null);
        }
    }

    private int delay = 1;

    public void setRule(Rule mRule) {
        this.mRule = mRule;
    }

    private void startLoadRule() {
        if (Build.VERSION.SDK_INT <= 22) {
            delay = 50;
        } else {
            delay = 20;
        }

        if (mRule == null ||
                mRule.getRemoveRules() == null ||
                mRule.getRemoveRules().length == 0)
            return;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinish)
                    return;
                exeRemoveRule();
                if (!isFinish) {
                    postDelayed(this, delay);
                }
            }
        }, 0);
    }


    @SuppressWarnings("deprecation")
    public void onDestroy() {
        isFinish = true;
        ViewParent parent = getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(this);
        }
        stopLoading();
        getSettings().setJavaScriptEnabled(false);
        clearHistory();
        clearView();
        removeAllViews();
        mOnFinishFinish = null;
        mImageClickListener = null;
        mVideoClickListener = null;
        destroy();
    }

    public boolean hasRule() {
        return mRule != null;
    }

    public void getHtml(OnLoadedHtmlListener listener) {
        this.mHTMLListener = listener;
        loadUrl("javascript:window.mark.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
    }

    private void addJavaScript() {
        loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.mark.openImage(this.src);  " +
                "    }  " +
                "}" +
                "})()");

        loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"video\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.mark.openVideo(this.src);  " +
                "    }  " +
                "}" +
                "})()");
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.mImageClickListener = listener;
    }

    public void setOnVideoClickListener(OnVideoClickListener listener) {
        this.mVideoClickListener = listener;
    }

    public void setOnFinishFinish(OnFinishListener listener) {
        this.mOnFinishFinish = listener;
    }

    private class JavascriptInterface {

        @android.webkit.JavascriptInterface
        public void openImage(String img) {
            if (mImageClickListener != null)
                mImageClickListener.onClick(img);
        }

        @android.webkit.JavascriptInterface
        public void openVideo(String img) {
            if (mVideoClickListener != null)
                mVideoClickListener.onClick(img);
        }

        @android.webkit.JavascriptInterface
        public void showHtml(String html) {
            if (mHTMLListener != null)
                mHTMLListener.showHtml(html);
        }
    }

    public interface OnLoadedHtmlListener {
        void showHtml(String html);
    }


    public interface OnImageClickListener {
        void onClick(String url);
    }

    public interface OnVideoClickListener {
        void onClick(String url);
    }

    public interface OnFinishListener {
        void onReceivedTitle(String title);

        void onProgressChange(int progress);

        void onError();

        void onFinish();
    }
}
