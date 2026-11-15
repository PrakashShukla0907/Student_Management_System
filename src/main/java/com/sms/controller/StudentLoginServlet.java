package com.sms.controller;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sms.dao.StudentAuthDAO;
import com.sms.model.StudentBean;

@WebServlet("/student-login")
public class StudentLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		StudentBean student;
		try {
			student = new StudentAuthDAO().authenticate(request.getParameter("email"), request.getParameter("password"));
		} catch (IllegalStateException e) {
			getServletContext().log("Student login failed because the database is unavailable.", e);
			request.setAttribute("studentErrorMsg", "Student sign-in is temporarily unavailable.");
			request.getRequestDispatcher("login.jsp").forward(request, response);
			return;
		}
		if (student == null) {
			request.setAttribute("studentErrorMsg", "Student email or password is incorrect.");
			request.getRequestDispatcher("login.jsp").forward(request, response);
			return;
		}

		HttpSession oldSession = request.getSession(false);
		if (oldSession != null) {
			oldSession.invalidate();
		}
		HttpSession session = request.getSession(true);
		session.setAttribute("role", "student");
		session.setAttribute("studentId", student.getId());
		session.setAttribute("studentName", student.getFirstName() + " " + student.getLastName());
		session.setAttribute("mustChangePassword", student.isMustChangePassword());
		session.setAttribute("csrfToken", UUID.randomUUID().toString());
		if (student.isMustChangePassword()) {
			response.sendRedirect("ChangeStudentPassword");
		} else {
			response.sendRedirect("StudentReport");
		}
	}
}
