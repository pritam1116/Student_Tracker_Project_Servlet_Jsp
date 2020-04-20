package com.luv2code.web.jdbc;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class StudentControllerServlet
 */
@WebServlet("/StudentControllerServlet")
public class StudentControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private StudentDbUtil studentDbUtil;
	
	@Resource(name="jdbc/web_student_tracker")
	private DataSource dataSource;
	
	

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		
		//create our student db util ... and pass in the conn pool / datasource
		
		try {
			studentDbUtil = new StudentDbUtil(dataSource);
		}
		catch(Exception e) {
			throw new ServletException(e);
		}
	}




	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		try {
			
			//read the "command" parameter 
			String theCommand =  request.getParameter("command");
			
			//if the command is missing, then default to listing students
			if(theCommand==null) {
				theCommand = "LIST";	
			}
			//route the appropriate method 
			switch (theCommand) {
			case "LIST":
				//list the students ... in MVC fashion
				listStudents(request,response);
				break;
			case "ADD":
				addStudent(request,response);
				break;
			case "LOAD":
				loadStudent(request,response);
				break;
			case "UPDATE":
				updateStudent(request,response);
				break;
			case "DELETE":
				deleteStudent(request,response);
				break;

			default:
				listStudents(request,response);
			}
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new ServletException(e);
		}
	}




	private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//read the student id from data
		String theStudentId = request.getParameter("studentId");
		
		//delete data from the database 
		studentDbUtil.deleteStudent(theStudentId);
		
		//send back to the "list students" page 
		listStudents(request, response);
	}




	private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//read student info from form data
		int id = Integer.parseInt(request.getParameter("studentId"));
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		
		//create a new student object
		Student theStudent = new Student(id, firstName, lastName, email);
		
		//perform updte on databse
		studentDbUtil.updateStudent(theStudent);
		
		//send them back to the "list students" page
		listStudents(request, response);
	}




	private void loadStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//read the student id from data
		String theStudentId = request.getParameter("studentId");
		
		//get student from database (db util)
		Student theStudent = studentDbUtil.getStudents(theStudentId);
		
		//place student in the request attribute
		request.setAttribute("THE_STUDENT", theStudent);
		
		//send to jsp page: update-student-form.jsp
		RequestDispatcher dispatcher = request.getRequestDispatcher("/update-student-form.jsp");
		dispatcher.forward(request, response);
		
	}




	private void addStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//read the info from the data
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		//create a new student object
		Student theStudent = new Student(firstName, lastName, email);
		
		//add the student to the database
		studentDbUtil.addStudent(theStudent);
		
		//send back to main page (the student list)
		listStudents(request, response);
	}




	private void listStudents(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//get students from db util
		List<Student> students = studentDbUtil.getStudents();
		
		//add student to the request
		request.setAttribute("STUDENT_LIST", students);
		
		//send to the JSP page (view )
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list-students.jsp");
		dispatcher.forward(request, response);
		
	}

}
