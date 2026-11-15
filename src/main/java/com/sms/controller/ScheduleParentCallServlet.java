package com.sms.controller;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sms.dao.ParentCallDAO;
import com.sms.model.ParentCallBean;
import com.sms.util.CsrfUtil;
import com.sms.util.TwilioVoiceService;

@WebServlet("/ScheduleParentCall")
public class ScheduleParentCallServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final ZoneId INDIA_ZONE = ZoneId.of("Asia/Kolkata");
	private static final DateTimeFormatter LOCAL_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	private static final Pattern E164 = Pattern.compile("^\\+[1-9][0-9]{7,14}$");
	private static final long MINIMUM_LEAD_MILLIS = 60_000L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!CsrfUtil.isValid(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		try {
			long markId = Long.parseLong(request.getParameter("markId"));
			if (markId <= 0) {
				throw new IllegalArgumentException("Invalid failed-result reference.");
			}
			ParentCallDAO dao = new ParentCallDAO();
			String action = request.getParameter("action");
			if ("cancel".equals(action)) {
				if (!dao.cancelScheduled(markId)) {
					throw new IllegalArgumentException("There is no pending scheduled call to cancel.");
				}
				request.getSession().setAttribute("academicSuccess", "Scheduled parent call cancelled.");
			} else if ("schedule".equals(action)) {
				scheduleCall(request, dao, markId);
			} else {
				throw new IllegalArgumentException("Unknown scheduled-call action.");
			}
		} catch (NumberFormatException e) {
			request.getSession().setAttribute("academicError", "Select a valid failed result.");
		} catch (DateTimeParseException e) {
			request.getSession().setAttribute("academicError", "Choose a valid date and time in Indian Standard Time.");
		} catch (IllegalArgumentException e) {
			request.getSession().setAttribute("academicError", e.getMessage());
		} catch (IllegalStateException e) {
			getServletContext().log("Scheduled parent-call operation failed.", e);
			request.getSession().setAttribute("academicError", "Could not update the scheduled call. Check the database and Twilio configuration.");
		}
		response.sendRedirect("AcademicRecords");
	}

	private void scheduleCall(HttpServletRequest request, ParentCallDAO dao, long markId) {
		TwilioVoiceService twilio = new TwilioVoiceService();
		if (!twilio.isConfigured()) {
			throw new IllegalArgumentException("Twilio is not configured. No call was scheduled.");
		}

		String requestedTime = request.getParameter("scheduledAt");
		if (requestedTime == null || requestedTime.trim().isEmpty()) {
			throw new IllegalArgumentException("Choose a date and time in Indian Standard Time.");
		}
		LocalDateTime localDateTime = LocalDateTime.parse(requestedTime, LOCAL_DATE_TIME);
		Instant scheduledInstant = localDateTime.atZone(INDIA_ZONE).toInstant();
		long scheduledAtEpochMillis = scheduledInstant.toEpochMilli();
		if (scheduledAtEpochMillis < System.currentTimeMillis() + MINIMUM_LEAD_MILLIS) {
			throw new IllegalArgumentException("Choose a time at least one minute in the future (IST).");
		}

		ParentCallBean call = dao.getFailedMark(markId);
		if (call.getParentMobile() == null || !E164.matcher(call.getParentMobile()).matches()) {
			throw new IllegalArgumentException("The student must add a valid parent mobile number in international format before a call can be scheduled.");
		}
		dao.schedule(call, scheduledAtEpochMillis);
		request.getSession().setAttribute("academicSuccess", "Parent call scheduled for the selected Indian Standard Time.");
	}
}
