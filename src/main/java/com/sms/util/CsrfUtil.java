package com.sms.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class CsrfUtil {
	private CsrfUtil() {}

	public static String token(HttpSession session) {
		Object token = session.getAttribute("csrfToken");
		if (token instanceof String) {
			return (String) token;
		}
		throw new IllegalStateException("The session security token is missing.");
	}

	public static boolean isValid(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return false;
		}
		Object expected = session.getAttribute("csrfToken");
		String submitted = request.getParameter("csrfToken");
		return expected instanceof String && submitted != null
				&& MessageDigest.isEqual(((String) expected).getBytes(StandardCharsets.UTF_8),
						submitted.getBytes(StandardCharsets.UTF_8));
	}
}
