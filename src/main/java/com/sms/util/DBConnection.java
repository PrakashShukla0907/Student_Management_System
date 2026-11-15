package com.sms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	private static final String URL = System.getenv().getOrDefault(
			"SMS_DB_URL",
			"jdbc:mysql://127.0.0.1:3306/SalesForce?useSSL=false&serverTimezone=UTC");
	private static final String USERNAME = System.getenv().getOrDefault("SMS_DB_USERNAME", "root");
	private static final String PASSWORD = System.getenv("SMS_DB_PASSWORD");

	private DBConnection() {}

	private static final ThreadLocal<Connection> REQUEST_CONNECTION = new ThreadLocal<Connection>();

	public static Connection getConnection() {
		if(PASSWORD == null || PASSWORD.isEmpty()) {
			throw new IllegalStateException("Set the SMS_DB_PASSWORD environment variable before starting the application.");
		}

		try {
			Connection con = REQUEST_CONNECTION.get();
			if(con == null || con.isClosed() || !con.isValid(2)) {
				Class.forName("com.mysql.cj.jdbc.Driver");
				con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
				REQUEST_CONNECTION.set(con);
			}
			return con;
		} catch (ClassNotFoundException | SQLException e) {
			throw new IllegalStateException("Could not connect to the configured MySQL database.", e);
		}
	}

	public static void closeRequestConnection() {
		Connection con = REQUEST_CONNECTION.get();
		REQUEST_CONNECTION.remove();
		if(con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				throw new IllegalStateException("Could not close the request database connection.", e);
			}
		}
	}
}










