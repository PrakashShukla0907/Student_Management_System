package com.sms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sms.model.ComplaintBean;
import com.sms.util.DBConnection;

public class ComplaintDAO {
	public List<ComplaintBean> getForStudent(long studentId) {
		return query("WHERE c.student_id = ? ORDER BY c.created_at DESC", studentId);
	}

	public List<ComplaintBean> getAll() {
		return query("ORDER BY c.created_at DESC", null);
	}

	private List<ComplaintBean> query(String filter, Long studentId) {
		List<ComplaintBean> complaints = new ArrayList<ComplaintBean>();
		String sql = "SELECT c.id, c.student_id, c.subject_id, st.first_name, st.last_name, "
				+ "su.name AS subject_name, c.message, c.status, c.admin_response, c.created_at, c.updated_at "
				+ "FROM complaints c JOIN student_details st ON st.id = c.student_id "
				+ "LEFT JOIN subjects su ON su.id = c.subject_id " + filter;
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			if (studentId != null) {
				ps.setLong(1, studentId);
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					ComplaintBean complaint = new ComplaintBean();
					complaint.setId(rs.getLong("id"));
					complaint.setStudentId(rs.getLong("student_id"));
					long subjectId = rs.getLong("subject_id");
					complaint.setSubjectId(rs.wasNull() ? null : subjectId);
					complaint.setFirstName(rs.getString("first_name"));
					complaint.setLastName(rs.getString("last_name"));
					complaint.setSubjectName(rs.getString("subject_name"));
					complaint.setMessage(rs.getString("message"));
					complaint.setStatus(rs.getString("status"));
					complaint.setAdminResponse(rs.getString("admin_response"));
					complaint.setCreatedAt(rs.getTimestamp("created_at"));
					complaint.setUpdatedAt(rs.getTimestamp("updated_at"));
					complaints.add(complaint);
				}
			}
			return complaints;
		} catch (SQLException e) {
			throw new IllegalStateException("Could not load complaints.", e);
		}
	}

	public void create(long studentId, Long subjectId, String message) {
		String sql;
		if (subjectId == null) {
			sql = "INSERT INTO complaints (student_id, message) VALUES (?, ?)";
		} else {
			sql = "INSERT INTO complaints (student_id, subject_id, message) "
					+ "SELECT ?, su.id, ? FROM student_marks sm JOIN subjects su ON su.id = sm.subject_id "
					+ "WHERE sm.student_id = ? AND su.id = ?";
		}
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setLong(1, studentId);
			if (subjectId == null) {
				ps.setString(2, message);
			} else {
				ps.setString(2, message);
				ps.setLong(3, studentId);
				ps.setLong(4, subjectId.longValue());
			}
			if (ps.executeUpdate() != 1) {
				throw new IllegalArgumentException("Select a subject from your own report before submitting the complaint.");
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Could not submit the complaint.", e);
		}
	}

	public void update(long complaintId, String status, String response) {
		String sql = "UPDATE complaints SET status = ?, admin_response = ? WHERE id = ?";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, status);
			ps.setString(2, response);
			ps.setLong(3, complaintId);
			if (ps.executeUpdate() != 1) {
				throw new IllegalArgumentException("Complaint was not found.");
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Could not update complaint status.", e);
		}
	}
}
