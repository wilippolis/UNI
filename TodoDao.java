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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.json.simple.JSONObject;

import com.ibm.bluemix.hack.util.VcapServicesHelper;


public class TodoDao {
	
	static private DataSource _datasource = null;
	static private final String _selectSql = "SELECT * FROM todos ORDER BY id DESC limit 100";
	
	static public synchronized DataSource getDataSource(){
		
		if( _datasource == null ) {
			JSONObject creds = VcapServicesHelper.getCredentials("compose-for-mysql", null);
			String connectionString = "jdbc:" + creds.get("uri").toString();
	        
	        PoolProperties p = new PoolProperties();
	        p.setUrl(connectionString);
	        p.setDriverClassName( "com.mysql.cj.jdbc.Driver" );

	        _datasource = new org.apache.tomcat.jdbc.pool.DataSource( p );
	        _datasource.setPoolProperties(p);
		} 
        
        return _datasource;
	}
	
	public List<Todo> getTodos(){
		
		List<Todo> todos = new ArrayList<Todo>();
		DataSource datasource = getDataSource();
		Connection conn = null;

		    try {
				conn = datasource.getConnection();
				Statement statement = conn.createStatement();
				ResultSet results = statement.executeQuery(_selectSql);
				while(results.next()){

			         int id  = results.getInt("id");
			         Timestamp created = results.getTimestamp("time");
			         String todoVal = results.getString("todo");
			         
			         Todo todo = new Todo();
			         todo.setId(id);
			         todo.setCreated(created);
			         todo.setTodo(todoVal);
			         
			         todos.add(todo);

			      }
				    
			} catch (SQLException e) {
				
				e.printStackTrace();
			} finally {
				try {
					if( conn != null ) conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		return todos;
	}

}
