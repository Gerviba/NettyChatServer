package hu.gerviba.chatserver.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;

import hu.gerviba.chatserver.datastores.User;

public final class Util {

	public static int parseInt(String value, int defaultValue) {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static String pack(ArrayList<User> users) {
		StringBuilder sb = new StringBuilder(256);
		for (User u : users) {
			sb.append(", ");
			sb.append(u.getNickname());
		}
		return sb.substring(2);
	}

	public static String encryptPassword(String password) {
		String sha1 = "";
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(password.getBytes("UTF-8"));
			sha1 = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sha1;
	}

	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	public static String escape(String str, int to) {
		for(int i = str.length();i < to;++i) {
			str += " ";
		}
		return str;
	}
	
	private static final ArrayList<Character> HEX = new ArrayList<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F'));
	
	public static boolean validHash(String pass) {
		if(pass.length() != 40)
			return false;
		for(int i = 0;i < 40;++i) {
			if(!HEX.contains(pass.charAt(i)))
				return false;
		}
		return true;
	}

}
