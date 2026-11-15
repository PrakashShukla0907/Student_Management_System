package com.sms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sms.model.StudentBean;
import com.sms.util.DBConnection;
import com.sms.util.PasswordUtil;

public class StudentAuthDAO {
	public StudentBean authenticate(String email, String password) {
		String query = "SELECT id, first_name, last_name, email, password_hash, must_change_password "
				+ "FROM student_details WHERE email = ?";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(query)) {
			ps.setString(1, email);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next() || !PasswordUtil.verify(password, rs.getString("password_hash"))) {
					return null;
				}
				StudentBean student = new StudentBean();
				student.setId(rs.getLong("id"));
				student.setFirstName(rs.getString("first_name"));
				student.setLastName(rs.getString("last_name"));
				student.setEmail(rs.getString("email"));
				student.setMustChangePassword(rs.getBoolean("must_change_password"));
				return student;
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Could not authenticate the student account.", e);
		}
	}

	public boolean mustChangePassword(long studentId) {
		String query = "SELECT must_change_password FROM student_details WHERE id = ?";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(query)) {
			ps.setLong(1, studentId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() && rs.getBoolean(1);
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Could not read the student account status.", e);
		}
	}

	public void changePassword(long studentId, String passwordHash) {
		String query = "UPDATE student_details SET password_hash = ?, must_change_password = FALSE WHERE id = ?";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(query)) {
			ps.setString(1, passwordHash);
			ps.setLong(2, studentId);
			if (ps.executeUpdate() != 1) {
				throw new IllegalStateException("Student account was not found.");
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Could not update the student password.", e);
		}
	}

	public StudentBean getStudent(long studentId) {
		String query = "SELECT id, first_name, last_name, email, phone, department, city, parent_mobile "
				+ "FROM student_details WHERE id = ?";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(query)) {
			ps.setLong(1, studentId);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				StudentBean student = new StudentBean();
				student.setId(rs.getLong("id"));
				student.setFirstName(rs.getString("first_name"));
				student.setLastName(rs.getString("last_name"));
				student.setEmail(rs.getString("email"));
				student.setPhone(rs.getString("phone"));
				student.setDepartment(rs.getString("department"));
				student.setCity(rs.getString("city"));
				student.setParentMobile(rs.getString("parent_mobile"));
				return student;
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Could not load the student profile.", e);
		}
	}

	public void updateParentMobile(long studentId, String parentMobile) {
		String query = "UPDATE student_details SET parent_mobile = ? WHERE id = ?";
		Connection con = DBConnection.getConnection();
		try (PreparedStatement ps = con.prepareStatement(query)) {
			ps.setString(1, parentMobile);
			ps.setLong(2, studentId);
			if (ps.executeUpdate() != 1) {
				throw new IllegalStateException("Student account was not found.");
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Could not update the parent's mobile number.", e);
		}
	}
}
