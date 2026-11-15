package com.sms.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sms.model.AcademicRecordBean;
import com.sms.model.LeaderboardEntryBean;
import com.sms.model.StudentBean;
import com.sms.model.SubjectBean;
import com.sms.util.DBConnection;

public class AcademicDAO {
	private static final BigDecimal PASS_MARK = new BigDecimal("33");

	public List<StudentBean> getStudents() {
		List<StudentBean> students = new ArrayList<StudentBean>();
		String query = "SELECT id, first_name, last_name FROM student_details ORDER BY first_name, last_name";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(query);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				StudentBean student = new StudentBean();
				student.setId(rs.getLong("id"));
				student.setFirstName(rs.getString("first_name"));
				student.setLastName(rs.getString("last_name"));
				students.add(student);
			}
			return students;
		} catch (SQLException e) {
			throw new IllegalStateException("Could not load students for academic records.", e);
		}
	}

	public List<SubjectBean> getSubjects() {
		List<SubjectBean> subjects = new ArrayList<SubjectBean>();
		String query = "SELECT id, name FROM subjects ORDER BY name";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(query);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				SubjectBean subject = new SubjectBean();
				subject.setId(rs.getLong("id"));
				subject.setName(rs.getString("name"));
				subjects.add(subject);
			}
			return subjects;
		} catch (SQLException e) {
			throw new IllegalStateException("Could not load subjects.", e);
		}
	}

	public List<AcademicRecordBean> getRecords() {
		List<AcademicRecordBean> records = new ArrayList<AcademicRecordBean>();
		String query = "SELECT st.id, st.first_name, st.last_name, su.name, sm.marks, sm.id, "
				+ "st.parent_mobile, pc.status, spc.scheduled_at_epoch_ms, spc.status "
				+ "FROM student_marks sm "
				+ "JOIN student_details st ON st.id = sm.student_id "
				+ "JOIN subjects su ON su.id = sm.subject_id "
				+ "LEFT JOIN parent_calls pc ON pc.mark_id = sm.id "
				+ "LEFT JOIN scheduled_parent_calls spc ON spc.mark_id = sm.id "
				+ "ORDER BY st.first_name, st.last_name, su.name";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(query);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				AcademicRecordBean record = new AcademicRecordBean();
				record.setStudentId(rs.getLong(1));
				record.setFirstName(rs.getString(2));
				record.setLastName(rs.getString(3));
				record.setSubjectName(rs.getString(4));
				record.setMarks(rs.getBigDecimal(5));
				record.setPassed(record.getMarks().compareTo(PASS_MARK) >= 0);
				record.setMarkId(rs.getLong(6));
				record.setParentMobile(rs.getString(7));
				record.setParentCallStatus(rs.getString(8));
				long scheduledAt = rs.getLong(9);
				if (!rs.wasNull()) {
					record.setScheduledCallAtEpochMillis(Long.valueOf(scheduledAt));
				}
				record.setScheduledCallStatus(rs.getString(10));
				records.add(record);
			}
			return records;
		} catch (SQLException e) {
			throw new IllegalStateException("Could not load student marks.", e);
		}
	}

	public List<AcademicRecordBean> getStudentRecords(long studentId) {
		List<AcademicRecordBean> records = new ArrayList<AcademicRecordBean>();
		String query = "SELECT st.id, st.first_name, st.last_name, su.name, sm.marks "
				+ "FROM student_marks sm "
				+ "JOIN student_details st ON st.id = sm.student_id "
				+ "JOIN subjects su ON su.id = sm.subject_id "
				+ "WHERE st.id = ? ORDER BY su.name";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(query)) {
			ps.setLong(1, studentId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					AcademicRecordBean record = new AcademicRecordBean();
					record.setStudentId(rs.getLong(1));
					record.setFirstName(rs.getString(2));
					record.setLastName(rs.getString(3));
					record.setSubjectName(rs.getString(4));
					record.setMarks(rs.getBigDecimal(5));
					record.setPassed(record.getMarks().compareTo(PASS_MARK) >= 0);
					records.add(record);
				}
			}
			return records;
		} catch (SQLException e) {
			throw new IllegalStateException("Could not load the student's report.", e);
		}
	}

	public List<SubjectBean> getStudentSubjects(long studentId) {
		List<SubjectBean> subjects = new ArrayList<SubjectBean>();
		String query = "SELECT su.id, su.name FROM subjects su "
				+ "JOIN student_marks sm ON sm.subject_id = su.id "
				+ "WHERE sm.student_id = ? ORDER BY su.name";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(query)) {
			ps.setLong(1, studentId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					SubjectBean subject = new SubjectBean();
					subject.setId(rs.getLong("id"));
					subject.setName(rs.getString("name"));
					subjects.add(subject);
				}
			}
			return subjects;
		} catch (SQLException e) {
			throw new IllegalStateException("Could not load subjects for the student's report.", e);
		}
	}

	public List<LeaderboardEntryBean> getLeaderboard() {
		List<LeaderboardEntryBean> entries = new ArrayList<LeaderboardEntryBean>();
		String query = "SELECT st.id, st.first_name, st.last_name, AVG(sm.marks) AS average_marks, "
				+ "COUNT(sm.subject_id) AS subject_count "
				+ "FROM student_details st "
				+ "JOIN student_marks sm ON sm.student_id = st.id "
				+ "GROUP BY st.id, st.first_name, st.last_name "
				+ "ORDER BY average_marks DESC, st.first_name, st.last_name";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(query);
				ResultSet rs = ps.executeQuery()) {
			BigDecimal lastAverage = null;
			int rank = 0;
			int position = 0;
			while (rs.next()) {
				position++;
				BigDecimal average = rs.getBigDecimal("average_marks");
				if (lastAverage == null || average.compareTo(lastAverage) != 0) {
					rank = position;
					lastAverage = average;
				}
				LeaderboardEntryBean entry = new LeaderboardEntryBean();
				entry.setRank(rank);
				entry.setStudentId(rs.getLong("id"));
				entry.setFirstName(rs.getString("first_name"));
				entry.setLastName(rs.getString("last_name"));
				entry.setAverageMarks(average);
				entry.setSubjectCount(rs.getInt("subject_count"));
				entries.add(entry);
			}
			return entries;
		} catch (SQLException e) {
			throw new IllegalStateException("Could not calculate the student leaderboard.", e);
		}
	}

	public void addSubject(String name) {
		String query = "INSERT INTO subjects (name) VALUES (?)";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(query)) {
			ps.setString(1, name);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new IllegalStateException("Could not add subject. Check whether its name already exists.", e);
		}
	}

	public void saveMark(long studentId, long subjectId, BigDecimal marks) {
		String query = "INSERT INTO student_marks (student_id, subject_id, marks) VALUES (?, ?, ?) "
				+ "ON DUPLICATE KEY UPDATE marks = VALUES(marks)";
		Connection con = DBConnection.getConnection();
		try {
			con.setAutoCommit(false);
			try (PreparedStatement ps = con.prepareStatement(query)) {
				ps.setLong(1, studentId);
				ps.setLong(2, subjectId);
				ps.setBigDecimal(3, marks);
				ps.executeUpdate();
			}
			if (marks.compareTo(PASS_MARK) >= 0) {
				try (PreparedStatement ps = con.prepareStatement(
						"DELETE pc FROM parent_calls pc JOIN student_marks sm ON sm.id = pc.mark_id "
								+ "WHERE sm.student_id = ? AND sm.subject_id = ?")) {
					ps.setLong(1, studentId);
					ps.setLong(2, subjectId);
					ps.executeUpdate();
				}
			}
			con.commit();
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException rollbackError) {
				e.addSuppressed(rollbackError);
			}
			throw new IllegalStateException("Could not save marks for the selected student and subject.", e);
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				throw new IllegalStateException("Could not restore the database connection state.", e);
			}
		}
	}
}
