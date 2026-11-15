package com.sms.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sms.dao.StudentDAO;
import com.sms.model.StudentBean;
import com.sms.util.PasswordUtil;
import com.sms.util.CsrfUtil;

/**
 * Servlet implementation class AddStudentServlet
 */
@WebServlet("/AddStudent")
public class AddStudentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!CsrfUtil.isValid(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		String password = request.getParameter("temporaryPassword");
		if (password == null || password.length() < 12 || password.length() > 256) {
			request.getSession().setAttribute("studentFormError", "Set a temporary student password of at least 12 characters.");
			response.sendRedirect("add-student.jsp");
			return;
		}
		//store all form details
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		String phone = request.getParameter("phone");
		String department = request.getParameter("department");
		String city = request.getParameter("city");
		
		//create a student bean and store the values
		StudentBean sb = new StudentBean();
		sb.setFirstName(firstName);
		sb.setLastName(lastName);
		sb.setEmail(email);
		sb.setPhone(phone);
		sb.setDepartment(department);
		sb.setCity(city);
		
		//create obj for student dao
		StudentDAO sdao = new StudentDAO();
		int result;
		try {
			result = sdao.addStudent(sb, PasswordUtil.hash(password));
		} catch (IllegalStateException e) {
			getServletContext().log("Could not create student account.", e);
			request.getSession().setAttribute("studentFormError", "Could not add the student. Check that the email is unique and the database is available.");
			response.sendRedirect("add-student.jsp");
			return;
		}
		
		if(result>0) {
			HttpSession hs = request.getSession(true);
			hs.setAttribute("insertMsg", sb.getFirstName()+" details inserted successfully.");
			response.sendRedirect("GetAllStudents");
		}else {
			HttpSession hs = request.getSession(true);
			hs.setAttribute("insertMsg", "insertion is failed");
			response.sendRedirect("add-student.jsp");
		}
		
	}

}














