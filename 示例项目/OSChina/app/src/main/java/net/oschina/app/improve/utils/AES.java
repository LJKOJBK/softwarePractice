package net.oschina.app.improve.utils;

import android.text.TextUtils;
import android.util.Base64;

import net.oschina.app.BuildConfig;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 数据加密类
 * Created by haibin on 2017/5/8.
 */
@SuppressWarnings("unused")
public final class AES {
    private static String AES_KEY = BuildConfig.AES_KEY;
    private static String AES_IV = BuildConfig.AES_IV;

    private static final String AES_MODE = "AES/CBC/PKCS7Padding";

    public static String encryptByBase64(String content) {
        try {
            SecretKeySpec keysSpec = new SecretKeySpec(AES_KEY.getBytes(), "AES");
            final Cipher cipher = Cipher.getInstance(AES_MODE);
            IvParameterSpec ivSpec = new IvParameterSpec(AES.AES_IV.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keysSpec, ivSpec);
            byte[] cipherText = cipher.doFinal(content.getBytes());
            return Base64.encodeToString(cipherText, Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }


    public static String encryptByBase64(String content,String key,String iv) {
        try {
            SecretKeySpec keysSpec = new SecretKeySpec(key.getBytes(), "AES");
            final Cipher cipher = Cipher.getInstance(AES_MODE);
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keysSpec, ivSpec);
            byte[] cipherText = cipher.doFinal(content.getBytes());
            return Base64.encodeToString(cipherText, Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }


    /*
    * 解密
    */
    public static String decryptByBase64( String content) {
        if (TextUtils.isEmpty(content)) {
            return content;
        }
        try {
            byte[] enc = Base64.decode(content,Base64.DEFAULT);
            byte[] result = decrypt(enc);
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 解密
     */
    private static byte[] decrypt(byte[] encrypted) throws Exception {
        byte[] raw = AES_KEY.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(AES.AES_IV.getBytes()));
        return cipher.doFinal(encrypted);
    }

}
