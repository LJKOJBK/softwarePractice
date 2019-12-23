package net.oschina.app.improve.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import net.oschina.app.AppContext;
import net.oschina.app.util.TLog;

import java.io.File;
import java.io.IOException;

import static net.oschina.common.utils.StreamUtil.copyFile;


/**
 * Created by JuQiu
 * on 16/7/21.
 */
@SuppressWarnings("WeakerAccess,all")
public final class PicturesCompressor {
    private PicturesCompressor() {

    }

    public static boolean compressImage(final String srcPath,
                                        final String savePath,
                                        final long targetSize) {
        return compressImage(srcPath, savePath, targetSize, 75, 1280, 1280 * 6, null, null, true);
    }

    public static File loadWithGlideCache(String path) {
        File tmp;
        try {
            tmp = Glide.with(AppContext.getInstance())
                    .load(path)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
            String absPath = tmp.getAbsolutePath();
            TLog.d("PicturesCompressor", "loadWithGlideCache:" + absPath);
            return tmp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 压缩图片
     *
     * @param srcPath     原图地址
     * @param savePath    存储地址
     * @param maxSize     最大文件地址byte
     * @param minQuality  最小质量
     * @param maxWidth    最大宽度
     * @param maxHeight   最大高度
     * @param byteStorage 用于批量压缩时的buffer，不必要为null，
     *                    需要时，推荐 {{@link BitmapUtil#DEFAULT_BUFFER_SIZE}}
     * @param options     批量压缩时复用参数，可调用 {{@link BitmapUtil#createOptions()}} 得到
     * @param exactDecode 是否精确解码， TRUE： 在4.4及其以上机器中能更节约内存
     * @return 是否压缩成功
     */
    public static boolean compressImage(final String srcPath,
                                        final String savePath,
                                        final long maxSize,
                                        final int minQuality,
                                        final int maxWidth,
                                        final int maxHeight,
                                        byte[] byteStorage,
                                        BitmapFactory.Options options,
                                        boolean exactDecode) {
        boolean loadWithGlide = false;
        // build source file
        File inTmp = new File(srcPath);
        final File sourceFile;
        if (inTmp.exists()) {
            sourceFile = inTmp;
        } else {
            File tmp = loadWithGlideCache(srcPath);
            if (tmp == null)
                return false;
            sourceFile = tmp;
            loadWithGlide = true;
        }

        // build save file
        final File saveFile = new File(savePath);
        File saveDir = saveFile.getParentFile();
        if (!saveDir.exists()) {
            if (!saveDir.mkdirs())
                return false;
        }

        // End clear the out file data
        if (saveFile.exists()) {
            if (!saveFile.delete())
                return false;
        }

        // if the in file size <= maxSize, we can copy to savePath
        if (sourceFile.length() <= maxSize && confirmImage(sourceFile, options) && readPictureDegree(sourceFile.getPath()) == 0) {
            return copyFile(sourceFile, saveFile);
        }

        File realCacheFile;
        if (loadWithGlide) {
            realCacheFile = sourceFile;
        } else {
            realCacheFile = loadWithGlideCache(sourceFile.getAbsolutePath());
            if (realCacheFile == null)
                return false;
        }

        // Doing
        File tempFile = BitmapUtil.Compressor.compressImage(realCacheFile, maxSize, minQuality, maxWidth,
                maxHeight, byteStorage, options, exactDecode);

        // Rename to out file
        return tempFile != null && copyFile(tempFile, saveFile) && tempFile.delete();
    }

    public static boolean confirmImage(File file, BitmapFactory.Options opts) {
        if (opts == null) opts = BitmapUtil.createOptions();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
        String mimeType = opts.outMimeType.toLowerCase();
        return mimeType.contains("jpeg") || mimeType.contains("png") || mimeType.contains("gif");
    }

    public static String getFileDiff(File file) {
        BitmapFactory.Options opts = BitmapUtil.createOptions();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
        String mimeType = opts.outMimeType.toLowerCase();
        if(mimeType.contains("jpeg")){
            return "jpeg";
        }
        if(mimeType.contains("png") ){
            return "png";
        }
        if(mimeType.contains("gif")){
            return "gif";
        }
        return "jpg";
    }

    public static String verifyPictureExt(String filePath) {
        int lastDotIndex = filePath.lastIndexOf(".");
        String ext = "jpg";
        String filePathWithoutDot = filePath;
        if (lastDotIndex != -1) {
            try {
                ext = filePath.substring(lastDotIndex + 1).toLowerCase();
                filePathWithoutDot = filePath.substring(lastDotIndex).toLowerCase();
            } catch (Exception e) {
                ext = "jpg";
                filePathWithoutDot = filePath;
            }
        }

        BitmapFactory.Options option = BitmapUtil.createOptions();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, option);
        String mimeType = option.outMimeType.toLowerCase();
        // "x-ico" "webp" "vnd.wap.wbmp"
        if (mimeType.contains("jpeg")) {
            ext = "jpg";
        } else if (mimeType.contains("png")) {
            ext = "png";
        }

        String newFilePath = String.format("%s.%s", filePathWithoutDot, ext);

        if (!filePath.equals(newFilePath)) {
            if (new File(filePath).renameTo(new File(newFilePath)))
                return newFilePath;
        }
        return filePath;
    }


    /**
     * 获取图片的旋转角度
     *
     * @param path path
     * @return 图片的旋转角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    private static Bitmap rotaingBitmap(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        ;
        matrix.postRotate(angle);
        Log.e("angle2", "  -- " + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return resizedBitmap;
    }
}
