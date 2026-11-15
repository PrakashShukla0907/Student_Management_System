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
 * Servlet implementation class UpdateStudentServlet
 */
@WebServlet("/UpdateStudent")
public class UpdateStudentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!CsrfUtil.isValid(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		//read updated student values
		Long id = Long.parseLong(request.getParameter("id"));
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		String phone = request.getParameter("phone");
		String department = request.getParameter("department");
		String city = request.getParameter("city");
		
		//store the above values into a student bean
		StudentBean sb = new StudentBean();
		sb.setId(id);
		sb.setFirstName(firstName);
		sb.setLastName(lastName);
		sb.setEmail(email);
		sb.setPhone(phone);
		sb.setDepartment(department);
		sb.setCity(city);
		
		//create obj for sdao and class updateStudent method
		StudentDAO sdao = new StudentDAO();
		String temporaryPassword = request.getParameter("temporaryPassword");
		String passwordHash = null;
		if (temporaryPassword != null && !temporaryPassword.isEmpty()) {
			try {
				passwordHash = PasswordUtil.hash(temporaryPassword);
			} catch (IllegalArgumentException e) {
				request.getSession().setAttribute("studentFormError", e.getMessage());
				response.sendRedirect("EditStudent?id=" + id);
				return;
			}
		}

		int result;
		try {
			result = sdao.updateStudent(sb, passwordHash);
		} catch (IllegalStateException e) {
			getServletContext().log("Could not update student account.", e);
			request.getSession().setAttribute("studentFormError", "Could not update the student. Check that the email is unique and the database is available.");
			response.sendRedirect("EditStudent?id=" + id);
			return;
		}
		if(result>0) {
			HttpSession hs = request.getSession(true);
			hs.setAttribute("updateMsg", sb.getFirstName()+" student details updated successfully.");
		}else {
			HttpSession hs = request.getSession(true);
			hs.setAttribute("updateMsg", sb.getFirstName()+" student details updation failed.");
		}
		
		response.sendRedirect("GetAllStudents");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
