package com.sms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sms.model.AdminBean;
import com.sms.util.DBConnection;

public class AdminDAO {
	//method returns adminbean based on email and password
	public AdminBean validateAdmin(String email,String password) {
		AdminBean adminBean = null;
		//create con
		Connection con = DBConnection.getConnection();
		//query to get admin record
		String query = "select id, user_name, email, password from admin_details where email=? and password=?";
		//user preaparedstatement to get admin record
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, email);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				adminBean = new AdminBean();
				adminBean.setId(rs.getLong(1));
				adminBean.setUserName(rs.getString(2));
				adminBean.setEmail(rs.getString(3));
				adminBean.setPassword(rs.getString(4));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return adminBean;
	}//end of method

}
