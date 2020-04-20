package com.luv2code.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDbUtil {

	private DataSource dataSource;
	
	public StudentDbUtil(DataSource theDataSource) {
		
		dataSource = theDataSource;
	}
	
	public List<Student> getStudents() throws Exception {
		
		List<Student> students = new ArrayList<Student>();
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
		//get a connection
		conn = dataSource.getConnection();
		
		//create SQL statement
		String sql = "select * from student order by last_name";
		stmt = conn.createStatement();
		
		//execute query
		rs = stmt.executeQuery(sql);
		
		//process result set
		while(rs.next()) {
			
			//retrieve data from result set row 
			int id = rs.getInt("id");
			String firstName = rs.getString("first_name");
			String lastName = rs.getString("last_name");
			String email = rs.getString("email");
			
			//create new student object
			Student tempStudent = new Student(id, firstName, lastName, email);
			
			//add it to the list of students
			students.add(tempStudent);
					

		}
		
			return students;	
		}
		finally {
			
			//close JDBC objects
			close(conn,stmt,rs);
		}

		
	}

	private void close(Connection conn, Statement stmt, ResultSet rs) {

		try {
			if(rs != null) {
				rs.close();
			}
			if(stmt != null) {
				stmt.close();
			}
			if(conn != null) {
				conn.close();
			}
			
		}
		catch(Exception e) {
			
			e.printStackTrace();
		}
		
	}

	public void addStudent(Student theStudent) throws Exception {
		
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			
			conn=dataSource.getConnection();
			
			//create sql for insert
			String sql = "insert into student "
						+"(first_Name,last_Name,email) "
						+ "values (?,?,?)";
			stmt = conn.prepareStatement(sql);
			
			//set the param values for the student 
			stmt.setString(1, theStudent.getFirstName());
			stmt.setString(2, theStudent.getLastName());
			stmt.setString(3, theStudent.getEmail());
			
			//execute sql  insert
			stmt.executeQuery();
			
		}
		finally {
		//clean up JDBC objects
		close(conn, stmt, rs);
			
		}
	}

	public Student getStudents(String theStudentId) throws Exception {
		
		Student theStudent = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int studentId;
		
		try {
			//convert student id to int 
			studentId = Integer.parseInt(theStudentId);
			
			//get connection to database
			conn = dataSource.getConnection();
			
			//create sql to get selected student 
			String sql = "select * from student where id=?";
			
			//create prepared statement 
			stmt = conn.prepareStatement(sql);
			
			//set the param
			stmt.setInt(1,studentId);
			
			//execute statement 
			rs = stmt.executeQuery();
			
			//retrieve data from result set row
			if(rs.next()) {
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String email = rs.getString("email");
				
				//use the studentId during construction
				theStudent = new Student(studentId, firstName, lastName, email);
			}
			
			else {
				throw new Exception("Could not find student id: " + studentId);
			}
		
		return theStudent;
		}
		finally {
			//clean up JDBC object
			close(conn, stmt, rs);
		}
	}

	public void updateStudent(Student theStudent) throws Exception {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			
			//get DB connection
			conn=dataSource.getConnection();
			
			//create SQL for update
			String sql = "update student "
						+"set first_name=?, last_name=?, email=? "
						+ "where id=?";
			
			stmt = conn.prepareStatement(sql);
			
			//set the param values for the student 
			stmt.setString(1, theStudent.getFirstName());
			stmt.setString(2, theStudent.getLastName());
			stmt.setString(3, theStudent.getEmail());
			stmt.setInt(4,theStudent.getId());
			
			//execute SQL  statements
			stmt.execute();
			
		} 	 	
		finally {
		//clean up JDBC objects
		close(conn, stmt, rs);
			
		}
		
	}

	public void deleteStudent(String theStudentId) throws Exception {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs =null;
		
		try {
		
			//convert student id to int 
			int studentId = Integer.parseInt(theStudentId);
			
			//get connection to database 
			conn = dataSource.getConnection();
			
			//create statement to delete student
			String sql = "delete from student where id=?";
			
			//prepared statement
			stmt=conn.prepareStatement(sql);
			
			//set params
			stmt.setInt(1, studentId);
			
			//execute SQL statement 
			stmt.execute();
		}
		finally {
			close(conn, stmt, rs);
		}
		
	}
}