/**
* Copyright 2017 IBM Corp. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the “License”);
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an “AS IS” BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.ibm.bluemix.hack.todo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.ibm.bluemix.hack.util.VcapServicesHelper;

/**
 * Servlet implementation class InitDb
 */
@WebServlet("/initdb")
public class InitDb extends HttpServlet {
       
	private static final long serialVersionUID = 1013327886370413456L;
	private static final String _dropTable = "DROP TABLE IF EXISTS todos";
	private static final String _createTable = 
			"CREATE TABLE todos ( " +
			"  id bigint(20) NOT NULL AUTO_INCREMENT, " +
			"  time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
			"  todo varchar(255) DEFAULT NULL, " +
			"  PRIMARY KEY (id) " +
			") DEFAULT CHARSET=utf8";
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public InitDb() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.removeAttribute("dbErr");
		request.removeAttribute("dbMsg");
		
		JSONObject creds = VcapServicesHelper.getCredentials("compose-for-mysql", null);
		String connectionString = "jdbc:" + creds.get("uri").toString();
		
		try {

			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection( connectionString );  
			
			Statement stmt=con.createStatement();  
			stmt.executeUpdate(_dropTable);  
			
			stmt.executeUpdate(_createTable);  
			
			con.close();  
			
			request.setAttribute("dbMsg", "Database successfuly created.");

		} catch (ClassNotFoundException | SQLException e) {
			
			request.setAttribute("dbErr", e.getMessage());
			e.printStackTrace();
		}  
		
		request.removeAttribute("dbErr");
		
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/todos.jsp");
		dispatcher.forward(request,response);
	}



}
