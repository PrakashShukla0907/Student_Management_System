package com.sms.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sms.dao.StudentDAO;
import com.sms.util.CsrfUtil;

/**
 * Servlet implementation class DeleteStudentServlet
 */
@WebServlet("/DeleteStudent")
public class DeleteStudentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteStudentServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!CsrfUtil.isValid(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		//store student id
		Long sid = Long.parseLong(request.getParameter("id"));
		
		//create obj for studentdao and call deletebyid method
		StudentDAO sdao = new StudentDAO();
		int result = sdao.deleteStudentById(sid);
		
		if(result==1) {
			HttpSession hs = request.getSession(true);
			hs.setAttribute("deleteMsg", sid+" student record is deleted.");
		}else {
			HttpSession hs = request.getSession(true);
			hs.setAttribute("deleteMsg", sid+" student record is not deleted.");
		}
		
		response.sendRedirect("GetAllStudents");
	}

	
}












