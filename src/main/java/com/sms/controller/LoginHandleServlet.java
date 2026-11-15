package com.sms.controller;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sms.dao.AdminDAO;
import com.sms.model.AdminBean;

/**
 * Servlet implementation class LoginHandleServlet
 */
@WebServlet("/login")
public class LoginHandleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//access the form data
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		AdminDAO adminDAO = new AdminDAO();
		AdminBean adminBean = adminDAO.validateAdmin(email, password);
		
		if(adminBean != null) {
			HttpSession hs = request.getSession();
			hs.invalidate();
			hs = request.getSession(true);
			hs.setAttribute("admin", adminBean);
			hs.setAttribute("role", "admin");
			hs.setAttribute("csrfToken", UUID.randomUUID().toString());
			response.sendRedirect("admin-panel.jsp");
		}else {
			request.setAttribute("errorMsg", "admin details are incorrect");
			request.getRequestDispatcher("login.jsp").forward(request, response);
		}
	}

}
