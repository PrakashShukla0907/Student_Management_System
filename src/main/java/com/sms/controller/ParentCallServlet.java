package com.sms.controller;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sms.dao.ParentCallDAO;
import com.sms.model.ParentCallBean;
import com.sms.util.TwilioVoiceService;
import com.sms.util.TwilioVoiceService.TwilioRejectedException;
import com.sms.util.CsrfUtil;

@WebServlet("/CallParent")
public class ParentCallServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Pattern E164 = Pattern.compile("^\\+[1-9][0-9]{7,14}$");

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!CsrfUtil.isValid(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		ParentCallDAO dao = new ParentCallDAO();
		try {
			long markId = Long.parseLong(request.getParameter("markId"));
			ParentCallBean call = dao.getFailedMark(markId);
			if (call.getParentMobile() == null || !E164.matcher(call.getParentMobile()).matches()) {
				throw new IllegalArgumentException("The student must add a valid parent mobile number in international format before a call can be placed.");
			}
			TwilioVoiceService twilio = new TwilioVoiceService();
			if (!twilio.isConfigured()) {
				throw new IllegalStateException("Twilio is not configured. No call was placed.");
			}
			if (!dao.claim(call)) {
				throw new IllegalArgumentException("A call was already placed or is still in progress for this failed result.");
			}
			try {
				String callSid = twilio.placeCall(call.getParentMobile());
				dao.finish(markId, "QUEUED", callSid);
				request.getSession().setAttribute("academicSuccess",
						"Twilio accepted the call request for " + call.getFirstName() + " " + call.getLastName() + ".");
			} catch (TwilioRejectedException e) {
				dao.finish(markId, "FAILED", null);
				getServletContext().log("Twilio rejected the parent-call request.", e);
				request.getSession().setAttribute("academicError",
						"Twilio rejected the call request. Check the Twilio configuration before retrying.");
			} catch (IOException e) {
				dao.finish(markId, "UNKNOWN", null);
				getServletContext().log("Twilio call outcome is unknown; automatic retry is disabled to prevent duplicate calls.", e);
				request.getSession().setAttribute("academicError",
						"Twilio could not confirm the call outcome. Check the Twilio console before trying again.");
			}
		} catch (IllegalArgumentException e) {
			request.getSession().setAttribute("academicError", e.getMessage());
		} catch (IllegalStateException e) {
			request.getSession().setAttribute("academicError", e.getMessage());
		}
		response.sendRedirect("AcademicRecords");
	}
}
