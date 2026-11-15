package com.sms.util;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.sms.dao.ParentCallDAO;
import com.sms.model.ParentCallBean;
import com.sms.util.TwilioVoiceService.TwilioRejectedException;

@WebListener
public class ScheduledParentCallWorker implements ServletContextListener {
	private static final long POLL_INTERVAL_SECONDS = 15L;
	private static final int BATCH_SIZE = 25;
	private static final Pattern E164 = Pattern.compile("^\\+[1-9][0-9]{7,14}$");

	private ScheduledExecutorService executor;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		final ServletContext context = event.getServletContext();
		ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable task) {
				Thread thread = new Thread(task, "scheduled-parent-call-worker");
				thread.setDaemon(true);
				return thread;
			}
		};
		executor = Executors.newSingleThreadScheduledExecutor(threadFactory);
		executor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				processDueCalls(context);
			}
		}, POLL_INTERVAL_SECONDS, POLL_INTERVAL_SECONDS, TimeUnit.SECONDS);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if (executor != null) {
			executor.shutdownNow();
		}
	}

	private void processDueCalls(ServletContext context) {
		List<Long> markIds;
		try {
			markIds = new ParentCallDAO().getDueScheduledCallIds(System.currentTimeMillis(), BATCH_SIZE);
		} catch (RuntimeException e) {
			context.log("Could not load due scheduled parent calls.", e);
			closeConnection(context);
			return;
		}
		closeConnection(context);

		for (Long markId : markIds) {
			try {
				processCall(context, markId.longValue());
			} catch (RuntimeException e) {
				context.log("Could not process scheduled parent call for mark " + markId + ".", e);
			} finally {
				closeConnection(context);
			}
		}
	}

	private void processCall(ServletContext context, long markId) {
		ParentCallDAO dao = new ParentCallDAO();
		if (!dao.claimScheduled(markId, System.currentTimeMillis())) {
			return;
		}

		ParentCallBean call;
		try {
			call = dao.getFailedMark(markId);
		} catch (IllegalArgumentException e) {
			dao.finishScheduled(markId, "FAILED");
			context.log("Scheduled call was not placed because the mark is no longer failed: " + markId + ".");
			return;
		}
		if (call.getParentMobile() == null || !E164.matcher(call.getParentMobile()).matches()) {
			dao.finishScheduled(markId, "FAILED");
			context.log("Scheduled call was not placed because the parent's number is missing or invalid for mark "
					+ markId + ".");
			return;
		}

		TwilioVoiceService twilio = new TwilioVoiceService();
		if (!twilio.isConfigured()) {
			dao.finishScheduled(markId, "FAILED");
			context.log("Scheduled call was not placed because Twilio is not configured for mark " + markId + ".");
			return;
		}
		if (!dao.claim(call)) {
			dao.finishScheduled(markId, "CANCELLED");
			return;
		}
		closeConnection(context);

		try {
			String callSid = twilio.placeCall(call.getParentMobile());
			dao.finish(markId, "QUEUED", callSid);
			dao.finishScheduled(markId, "COMPLETED");
		} catch (TwilioRejectedException e) {
			dao.finish(markId, "FAILED", null);
			dao.finishScheduled(markId, "FAILED");
			context.log("Twilio rejected the scheduled parent-call request for mark " + markId + ".", e);
		} catch (IOException e) {
			dao.finish(markId, "UNKNOWN", null);
			dao.finishScheduled(markId, "UNKNOWN");
			context.log("Scheduled call outcome is unknown for mark " + markId
					+ "; automatic retry is disabled to prevent duplicate calls.", e);
		}
	}

	private void closeConnection(ServletContext context) {
		try {
			DBConnection.closeRequestConnection();
		} catch (IllegalStateException e) {
			context.log("Could not close the scheduled-call worker database connection.", e);
		}
	}
}
