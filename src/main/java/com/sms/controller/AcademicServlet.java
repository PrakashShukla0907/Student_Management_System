package com.sms.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sms.dao.AcademicDAO;
import com.sms.model.LeaderboardEntryBean;
import com.sms.util.CsrfUtil;

@WebServlet("/AcademicRecords")
public class AcademicServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final BigDecimal MAX_MARK = new BigDecimal("100");
	private static final ZoneId INDIA_ZONE = ZoneId.of("Asia/Kolkata");
	private static final DateTimeFormatter DATE_TIME_LOCAL = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getSession(false) == null || request.getSession(false).getAttribute("admin") == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		AcademicDAO dao = new AcademicDAO();
		request.setAttribute("students", dao.getStudents());
		request.setAttribute("subjects", dao.getSubjects());
		request.setAttribute("records", dao.getRecords());
		List<LeaderboardEntryBean> leaderboard = dao.getLeaderboard();
		request.setAttribute("leaderboard", leaderboard);
		request.setAttribute("minimumScheduledCallTime", LocalDateTime.now(INDIA_ZONE).plusMinutes(2)
				.withSecond(0).withNano(0).format(DATE_TIME_LOCAL));
		request.setAttribute("csrfToken", CsrfUtil.token(request.getSession()));
		request.getRequestDispatcher("academic-records.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getSession(false) == null || request.getSession(false).getAttribute("admin") == null) {
			response.sendRedirect("login.jsp");
			return;
		}
		if (!CsrfUtil.isValid(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		String action = request.getParameter("action");
		try {
			AcademicDAO dao = new AcademicDAO();
			if ("addSubject".equals(action)) {
				String name = request.getParameter("subjectName");
				if (name == null || name.trim().isEmpty() || name.trim().length() > 255) {
					throw new IllegalArgumentException("Enter a subject name (up to 255 characters).");
				}
				dao.addSubject(name.trim());
				setNotice(request, "academicSuccess", "Subject added.");
			} else if ("saveMark".equals(action)) {
				long studentId = Long.parseLong(request.getParameter("studentId"));
				long subjectId = Long.parseLong(request.getParameter("subjectId"));
				BigDecimal marks = new BigDecimal(request.getParameter("marks"));
				if (studentId <= 0 || subjectId <= 0 || marks.compareTo(BigDecimal.ZERO) < 0
						|| marks.compareTo(MAX_MARK) > 0 || marks.scale() > 2) {
					throw new IllegalArgumentException("Select a student and subject, then enter marks from 0 to 100 (up to 2 decimal places).");
				}
				dao.saveMark(studentId, subjectId, marks);
				setNotice(request, "academicSuccess", "Marks saved.");
			} else {
				throw new IllegalArgumentException("Unknown academic-record action.");
			}
		} catch (NumberFormatException e) {
			setNotice(request, "academicError", "Enter valid numeric student, subject, and mark values.");
		} catch (IllegalArgumentException e) {
			setNotice(request, "academicError", e.getMessage());
		} catch (IllegalStateException e) {
			getServletContext().log("Academic record update failed.", e);
			setNotice(request, "academicError", "Could not save the change. Check the database and that the subject name is unique.");
		}
		response.sendRedirect("AcademicRecords");
	}

	private void setNotice(HttpServletRequest request, String key, String message) {
		HttpSession session = request.getSession();
		session.setAttribute(key, message);
	}
}
