package com.wisp.game.core.utils;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 字符串工具
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class StringUtil {

    /**
     *
     * @param str
     * @return true 字符串为空
     */
    public static boolean isNullOrEmpty(String str) {
        return null == str || str.trim().isEmpty();
    }
    
	// 判断整数（int）

	public static boolean isInteger(String str) {
		if (null == str || "".equals(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	// 判断浮点数（double和float ）
	public static boolean isDouble(String str) {
		if (null == str || "".equals(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
		return pattern.matcher(str).matches();
	}

	/**
	 * 首字母大写
	 * 
	 * @param Str
	 * @return
	 */
	public static String upFirstChar(String Str) {
		char[] cs = Str.toCharArray();
		if (Character.isLowerCase(cs[0])) {
			cs[0] -= 32;
			return String.valueOf(cs);
		}
		return Str;
	}

	/**
	 * 首字母转小写
	 */
	public static String lowerFirstChar(String s) {
		if (Character.isLowerCase(s.charAt(0))) {
			return s;
		}

		return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
	}
	
	public static boolean isEmpty(Object value) {
		return (value == null || "".equals(value));
	}

	public static boolean isNotEmpty(Object value) {
		return (!isEmpty(value));
	}

	public static String getUUID() {
		String uuid = UUID.randomUUID().toString();
		return uuid.toUpperCase().replace("-", "");
	}

	public static String getMD5(String source) {
		if (source == null || source == "") {
			return null;
		}
		String str = null;
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(source.getBytes());
			byte tmp[] = md.digest();
			char chstr[] = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++) {
				byte byte0 = tmp[i];
				chstr[k++] = hexDigits[byte0 >>> 4 & 0xf];
				chstr[k++] = hexDigits[byte0 & 0xf];
			}
			str = new String(chstr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return str;
	}
}
