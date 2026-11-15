package com.sms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sms.model.ParentCallBean;
import com.sms.util.DBConnection;

public class ParentCallDAO {
	public ParentCallBean getFailedMark(long markId) {
		String query = "SELECT sm.id AS mark_id, st.id AS student_id, su.id AS subject_id, "
				+ "st.first_name, st.last_name, su.name AS subject_name, st.parent_mobile, sm.marks, "
				+ "pc.status AS call_status, pc.call_sid "
				+ "FROM student_marks sm JOIN student_details st ON st.id = sm.student_id "
				+ "JOIN subjects su ON su.id = sm.subject_id "
				+ "LEFT JOIN parent_calls pc ON pc.mark_id = sm.id "
				+ "WHERE sm.id = ? AND sm.marks < 33";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(query)) {
			ps.setLong(1, markId);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					throw new IllegalArgumentException("Parent calls are only available for currently failed subjects.");
				}
				ParentCallBean call = new ParentCallBean();
				call.setMarkId(rs.getLong("mark_id"));
				call.setStudentId(rs.getLong("student_id"));
				call.setSubjectId(rs.getLong("subject_id"));
				call.setFirstName(rs.getString("first_name"));
				call.setLastName(rs.getString("last_name"));
				call.setSubjectName(rs.getString("subject_name"));
				call.setParentMobile(rs.getString("parent_mobile"));
				call.setMarks(rs.getBigDecimal("marks"));
				call.setCallStatus(rs.getString("call_status"));
				call.setCallSid(rs.getString("call_sid"));
				return call;
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Could not verify the failed result for parent notification.", e);
		}
	}

	public boolean claim(ParentCallBean call) {
		Connection con = DBConnection.getConnection();
		try {
			con.setAutoCommit(false);
			boolean claimed = false;
			try (PreparedStatement insert = con.prepareStatement(
					"INSERT IGNORE INTO parent_calls (mark_id, status, updated_at) VALUES (?, 'REQUESTING', CURRENT_TIMESTAMP)")) {
				insert.setLong(1, call.getMarkId());
				claimed = insert.executeUpdate() == 1;
			}
			if (!claimed) {
				try (PreparedStatement retry = con.prepareStatement(
						"UPDATE parent_calls SET status='REQUESTING', call_sid=NULL, updated_at=CURRENT_TIMESTAMP "
								+ "WHERE mark_id=? AND status='FAILED'")) {
					retry.setLong(1, call.getMarkId());
					claimed = retry.executeUpdate() == 1;
				}
			}
			if (claimed) {
				try (PreparedStatement cancel = con.prepareStatement(
						"UPDATE scheduled_parent_calls SET status='CANCELLED', updated_at=CURRENT_TIMESTAMP "
								+ "WHERE mark_id=? AND status='SCHEDULED'")) {
					cancel.setLong(1, call.getMarkId());
					cancel.executeUpdate();
				}
			}
			con.commit();
			return claimed;
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException rollbackError) {
				e.addSuppressed(rollbackError);
			}
			throw new IllegalStateException("Could not claim the parent-call request.", e);
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				throw new IllegalStateException("Could not restore the database connection state.", e);
			}
		}
	}

	public void schedule(ParentCallBean call, long scheduledAtEpochMillis) {
		Connection con = DBConnection.getConnection();
		try {
			con.setAutoCommit(false);
			String callStatus = null;
			try (PreparedStatement ps = con.prepareStatement(
					"SELECT status FROM parent_calls WHERE mark_id=? FOR UPDATE")) {
				ps.setLong(1, call.getMarkId());
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						callStatus = rs.getString(1);
					}
				}
			}
			if (callStatus != null && !"FAILED".equals(callStatus)) {
				throw new IllegalArgumentException("A call has already been placed or is still in progress for this failed result.");
			}

			String scheduleStatus = null;
			try (PreparedStatement ps = con.prepareStatement(
					"SELECT status FROM scheduled_parent_calls WHERE mark_id=? FOR UPDATE")) {
				ps.setLong(1, call.getMarkId());
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						scheduleStatus = rs.getString(1);
					}
				}
			}

			if (scheduleStatus == null) {
				try (PreparedStatement ps = con.prepareStatement(
						"INSERT INTO scheduled_parent_calls (mark_id, scheduled_at_epoch_ms, status) "
								+ "VALUES (?, ?, 'SCHEDULED')")) {
					ps.setLong(1, call.getMarkId());
					ps.setLong(2, scheduledAtEpochMillis);
					ps.executeUpdate();
				}
			} else if ("SCHEDULED".equals(scheduleStatus)
					|| "CANCELLED".equals(scheduleStatus) || "FAILED".equals(scheduleStatus)) {
				try (PreparedStatement ps = con.prepareStatement(
						"UPDATE scheduled_parent_calls SET scheduled_at_epoch_ms=?, status='SCHEDULED', "
								+ "updated_at=CURRENT_TIMESTAMP WHERE mark_id=?")) {
					ps.setLong(1, scheduledAtEpochMillis);
					ps.setLong(2, call.getMarkId());
					ps.executeUpdate();
				}
			} else {
				throw new IllegalArgumentException("This failed result already has a call in progress or a completed call.");
			}
			con.commit();
		} catch (IllegalArgumentException e) {
			rollback(con, e);
			throw e;
		} catch (SQLException e) {
			rollback(con, e);
			throw new IllegalStateException("Could not schedule the parent call.", e);
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				throw new IllegalStateException("Could not restore the database connection state.", e);
			}
		}
	}

	public boolean cancelScheduled(long markId) {
		String query = "UPDATE scheduled_parent_calls SET status='CANCELLED', updated_at=CURRENT_TIMESTAMP "
				+ "WHERE mark_id=? AND status='SCHEDULED'";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(query)) {
			ps.setLong(1, markId);
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			throw new IllegalStateException("Could not cancel the scheduled parent call.", e);
		}
	}

	public List<Long> getDueScheduledCallIds(long nowEpochMillis, int limit) {
		if (limit < 1 || limit > 100) {
			throw new IllegalArgumentException("The scheduled-call batch size must be from 1 to 100.");
		}
		List<Long> markIds = new ArrayList<Long>();
		String query = "SELECT mark_id FROM scheduled_parent_calls "
				+ "WHERE status='SCHEDULED' AND scheduled_at_epoch_ms<=? "
				+ "ORDER BY scheduled_at_epoch_ms LIMIT ?";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(query)) {
			ps.setLong(1, nowEpochMillis);
			ps.setInt(2, limit);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					markIds.add(Long.valueOf(rs.getLong(1)));
				}
			}
			return markIds;
		} catch (SQLException e) {
			throw new IllegalStateException("Could not load due parent calls.", e);
		}
	}

	public boolean claimScheduled(long markId, long nowEpochMillis) {
		String query = "UPDATE scheduled_parent_calls SET status='PROCESSING', updated_at=CURRENT_TIMESTAMP "
				+ "WHERE mark_id=? AND status='SCHEDULED' AND scheduled_at_epoch_ms<=?";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(query)) {
			ps.setLong(1, markId);
			ps.setLong(2, nowEpochMillis);
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			throw new IllegalStateException("Could not claim the scheduled parent call.", e);
		}
	}

	public void finishScheduled(long markId, String status) {
		if (!"COMPLETED".equals(status) && !"FAILED".equals(status)
				&& !"UNKNOWN".equals(status) && !"CANCELLED".equals(status)) {
			throw new IllegalArgumentException("Invalid scheduled parent-call status.");
		}
		String query = "UPDATE scheduled_parent_calls SET status=?, updated_at=CURRENT_TIMESTAMP "
				+ "WHERE mark_id=? AND status='PROCESSING'";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(query)) {
			ps.setString(1, status);
			ps.setLong(2, markId);
			if (ps.executeUpdate() != 1) {
				throw new IllegalStateException("Scheduled parent call is no longer in progress.");
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Could not record the scheduled parent-call result.", e);
		}
	}

	public void finish(long markId, String status, String callSid) {
		String query = "UPDATE parent_calls SET status = ?, call_sid = ?, updated_at = CURRENT_TIMESTAMP "
				+ "WHERE mark_id = ? AND status = 'REQUESTING'";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(query)) {
			ps.setString(1, status);
			ps.setString(2, callSid);
			ps.setLong(3, markId);
			if (ps.executeUpdate() != 1) {
				throw new IllegalStateException("Parent-call request is no longer in progress.");
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Could not record the parent-call result.", e);
		}
	}

	private static void rollback(Connection con, Exception cause) {
		try {
			con.rollback();
		} catch (SQLException rollbackError) {
			cause.addSuppressed(rollbackError);
		}
	}
}
