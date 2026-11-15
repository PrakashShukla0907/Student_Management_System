package com.sms.util;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {
	private static final int ITERATIONS = 120000;
	private static final int SALT_BYTES = 16;
	private static final int KEY_BITS = 256;
	private static final SecureRandom RANDOM = new SecureRandom();

	private PasswordUtil() {}

	public static String hash(String password) {
		if (password == null || password.length() < 12 || password.length() > 256) {
			throw new IllegalArgumentException("Student passwords must be between 12 and 256 characters.");
		}
		byte[] salt = new byte[SALT_BYTES];
		RANDOM.nextBytes(salt);
		byte[] hash = derive(password.toCharArray(), salt, ITERATIONS);
		return "pbkdf2$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt)
				+ "$" + Base64.getEncoder().encodeToString(hash);
	}

	public static boolean verify(String password, String encoded) {
		if (password == null || encoded == null) {
			return false;
		}
		try {
			String[] parts = encoded.split("\\$", -1);
			if (parts.length != 4 || !"pbkdf2".equals(parts[0])) {
				return false;
			}
			int iterations = Integer.parseInt(parts[1]);
			if (iterations < 10000 || iterations > 1000000) {
				return false;
			}
			byte[] salt = Base64.getDecoder().decode(parts[2]);
			byte[] expected = Base64.getDecoder().decode(parts[3]);
			byte[] actual = derive(password.toCharArray(), salt, iterations);
			if (actual.length != expected.length) {
				return false;
			}
			int difference = 0;
			for (int i = 0; i < actual.length; i++) {
				difference |= actual[i] ^ expected[i];
			}
			return difference == 0;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private static byte[] derive(char[] password, byte[] salt, int iterations) {
		PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_BITS);
		try {
			return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Password hashing is unavailable.", e);
		} finally {
			spec.clearPassword();
		}
	}
}
