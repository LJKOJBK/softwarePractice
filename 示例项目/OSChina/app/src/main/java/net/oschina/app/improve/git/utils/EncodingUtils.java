package net.oschina.app.improve.git.utils;


import java.io.UnsupportedEncodingException;

/**
 * Encoding utilities
 */
public abstract class EncodingUtils {

	/**
	 * Decode base64 encoded string
	 *
	 * @param content
	 * @return byte array
	 */
	public static final byte[] fromBase64(final String content) {
		return Base64Util.decode(content);
	}

	/**
	 * Base64 encode given byte array
	 *
	 * @param content
	 * @return byte array
	 */
	public static final String toBase64(final byte[] content) {
		return Base64Util.encodeBytes(content);
	}

	/**
	 * Base64 encode given byte array
	 *
	 * @param content
	 * @return byte array
	 */
	public static final String toBase64(final String content) {
		byte[] bytes;
		try {
			bytes = content.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			bytes = content.getBytes();
		}
		return toBase64(bytes);
	}
}