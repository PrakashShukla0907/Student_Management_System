package com.sms.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sms.dao.AcademicDAO;
import com.sms.dao.ComplaintDAO;
import com.sms.dao.StudentAuthDAO;
import com.sms.model.StudentBean;
import com.sms.util.PasswordUtil;
import java.util.regex.Pattern;
import com.sms.util.CsrfUtil;

@WebServlet(urlPatterns = { "/StudentReport", "/StudentComplaints", "/ChangeStudentPassword" })
public class StudentPortalServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Pattern E164 = Pattern.compile("^\\+[1-9][0-9]{7,14}$");

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long studentId = getStudentId(request);
		String path = request.getServletPath();
		request.setAttribute("csrfToken", CsrfUtil.token(request.getSession(false)));
		if ("/ChangeStudentPassword".equals(path)) {
			request.getRequestDispatcher("student-change-password.jsp").forward(request, response);
			return;
		}

		StudentAuthDAO studentDAO = new StudentAuthDAO();
		StudentBean student = studentDAO.getStudent(studentId);
		if (student == null) {
			request.getSession(false).invalidate();
			response.sendRedirect("login.jsp");
			return;
		}
		request.setAttribute("student", student);
		if ("/StudentReport".equals(path)) {
			AcademicDAO academicDAO = new AcademicDAO();
			request.setAttribute("records", academicDAO.getStudentRecords(studentId));
			request.setAttribute("leaderboard", academicDAO.getLeaderboard());
			request.getRequestDispatcher("student-report.jsp").forward(request, response);
		} else {
			AcademicDAO academicDAO = new AcademicDAO();
			request.setAttribute("subjects", academicDAO.getStudentSubjects(studentId));
			request.setAttribute("complaints", new ComplaintDAO().getForStudent(studentId));
			request.getRequestDispatcher("student-complaints.jsp").forward(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long studentId = getStudentId(request);
		String path = request.getServletPath();
		if (!"/StudentComplaints".equals(path) && !"/ChangeStudentPassword".equals(path)) {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}
		if (!CsrfUtil.isValid(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		try {
			if ("/ChangeStudentPassword".equals(path)) {
				String password = request.getParameter("password");
				String confirm = request.getParameter("confirmPassword");
				if (password == null || !password.equals(confirm)) {
					throw new IllegalArgumentException("The password and confirmation must match.");
				}
				new StudentAuthDAO().changePassword(studentId, PasswordUtil.hash(password));
				request.getSession(false).setAttribute("mustChangePassword", Boolean.FALSE);
				request.getSession(false).setAttribute("studentSuccess", "Password updated.");
				response.sendRedirect("StudentReport");
				return;
			}
			if ("saveParentMobile".equals(request.getParameter("action"))) {
				String parentMobile = request.getParameter("parentMobile");
				if (parentMobile == null || !E164.matcher(parentMobile.trim()).matches()) {
					throw new IllegalArgumentException("Enter a valid parent mobile number in international format, such as +14155552671.");
				}
				new StudentAuthDAO().updateParentMobile(studentId, parentMobile.trim());
				request.getSession(false).setAttribute("studentSuccess", "Parent mobile number saved.");
				response.sendRedirect("StudentComplaints");
				return;
			}
			String subjectValue = request.getParameter("subjectId");
			String message = request.getParameter("message");
			if (subjectValue == null || subjectValue.trim().isEmpty()
					|| message == null || message.trim().length() < 10 || message.trim().length() > 2000) {
				throw new IllegalArgumentException("Choose a subject and describe the discrepancy in 10 to 2,000 characters.");
			}
			long subjectId = Long.parseLong(subjectValue);
			new ComplaintDAO().create(studentId, Long.valueOf(subjectId), message.trim());
			request.getSession(false).setAttribute("studentSuccess", "Your complaint was sent to the school administrator.");
		} catch (NumberFormatException e) {
			request.getSession(false).setAttribute("studentError", "Choose a valid subject.");
		} catch (IllegalArgumentException e) {
			request.getSession(false).setAttribute("studentError", e.getMessage());
		} catch (IllegalStateException e) {
			getServletContext().log("Student portal update failed.", e);
			request.getSession(false).setAttribute("studentError", "Could not save your request. Please try again later.");
		}
		response.sendRedirect("/StudentComplaints".equals(path) ? "StudentComplaints" : "ChangeStudentPassword");
	}

	private long getStudentId(HttpServletRequest request) {
		Object value = request.getSession(false).getAttribute("studentId");
		if (!(value instanceof Long)) {
			throw new IllegalStateException("Student session is missing its account identifier.");
		}
		return ((Long) value).longValue();
	}
}
