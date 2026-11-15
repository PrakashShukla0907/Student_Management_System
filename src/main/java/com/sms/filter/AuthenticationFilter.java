package com.sms.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sms.util.DBConnection;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {
	@Override
	public void init(FilterConfig filterConfig) {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
		try {
			if ("/".equals(path) || "/login.jsp".equals(path) || "/login".equals(path)
					|| "/student-login".equals(path) || "/student-login.jsp".equals(path)) {
				chain.doFilter(request, response);
				return;
			}

			HttpSession session = httpRequest.getSession(false);
			String role = session == null ? null : (String) session.getAttribute("role");
			if (role == null) {
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
				return;
			}

			if ("student".equals(role)) {
				if (!path.startsWith("/StudentReport") && !path.startsWith("/StudentComplaints")
						&& !path.startsWith("/ChangeStudentPassword") && !"/Logout".equals(path)) {
					httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
				if (Boolean.TRUE.equals(session.getAttribute("mustChangePassword"))
						&& !path.startsWith("/ChangeStudentPassword") && !"/Logout".equals(path)) {
					httpResponse.sendRedirect(httpRequest.getContextPath() + "/ChangeStudentPassword");
					return;
				}
			} else if (!"admin".equals(role)) {
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
				return;
			} else if (path.startsWith("/StudentReport") || path.startsWith("/StudentComplaints")
					|| path.startsWith("/ChangeStudentPassword") || path.startsWith("/student-")) {
				httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			chain.doFilter(request, response);
		} finally {
			try {
				DBConnection.closeRequestConnection();
			} catch (IllegalStateException e) {
				httpRequest.getServletContext().log("Could not close request database connection.", e);
			}
		}
	}

	@Override
	public void destroy() {}
}
