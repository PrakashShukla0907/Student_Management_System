package com.sms.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sms.dao.StudentDAO;
import com.sms.model.StudentBean;
import com.sms.util.CsrfUtil;

/**
 * Servlet implementation class GetAllStudents
 */
@WebServlet("/GetAllStudents")
public class GetAllStudentsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ArrayList<StudentBean> studentList = new ArrayList<StudentBean>();
		StudentDAO sd = new StudentDAO();
		studentList = sd.getAllStudents();
		request.setAttribute("studentList", studentList);
		request.setAttribute("csrfToken", CsrfUtil.token(request.getSession()));
		RequestDispatcher rd = request.getRequestDispatcher("display-student.jsp");
		rd.forward(request, response);
		
	}

}










