package com.sms.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sms.dao.ComplaintDAO;
import com.sms.util.CsrfUtil;

@WebServlet("/AdminComplaints")
public class AdminComplaintsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Set<String> STATUSES = new HashSet<String>(
			Arrays.asList("OPEN", "IN_REVIEW", "RESOLVED", "REJECTED"));

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("complaints", new ComplaintDAO().getAll());
		request.setAttribute("csrfToken", CsrfUtil.token(request.getSession()));
		request.getRequestDispatcher("admin-complaints.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!CsrfUtil.isValid(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		try {
			long id = Long.parseLong(request.getParameter("complaintId"));
			String status = request.getParameter("status");
			String reply = request.getParameter("adminResponse");
			if (id <= 0 || !STATUSES.contains(status) || reply == null || reply.trim().length() > 2000) {
				throw new IllegalArgumentException("Choose a valid status and provide a response of at most 2,000 characters.");
			}
			new ComplaintDAO().update(id, status, reply.trim());
			request.getSession().setAttribute("complaintSuccess", "Complaint updated.");
		} catch (IllegalArgumentException e) {
			request.getSession().setAttribute("complaintError", "Invalid complaint details.");
		} catch (IllegalStateException e) {
			getServletContext().log("Could not update complaint.", e);
			request.getSession().setAttribute("complaintError", "Could not update the complaint.");
		}
		response.sendRedirect("AdminComplaints");
	}
}
