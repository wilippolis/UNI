<%@ page import="java.util.List" %>
<%@ page import="com.ibm.bluemix.hack.todo.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
 <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="Jim Conallen">
    <link rel="icon" href="./images/bluemix_icon.png">

    <title>Java (Tomcat) MySQL Hackathon Example - TODOs</title>

    <!-- Bootstrap core CSS -->
    <link href="./css/bootstrap.min.css" rel="stylesheet">

  </head>
<body>

<div class="container">
<button type="button" class="btn btn-link" onclick="window.location = 'index.html';">Home</button>
<button type="button" class="btn btn-link" onclick="window.location = 'initdb';">Reset DB</button>
<h1>Example Database Page</h1>

<% if( request.getAttribute("dbErr") != null ) { %>
	<div class="alert alert-danger" role="alert">
		<strong>DB ERROR:</strong> <%= request.getAttribute("dbErr") %>
	</div>
<% } %>

<% if( request.getAttribute("dbMsg") != null ) { %>
	<div class="alert alert-success" role="alert">
		<strong>DB:</strong> <%= request.getAttribute("dbMsg") %>
	</div>
<% } %>
</div>

<div class="container">
<form action="todo" class="form-inline" method="post">
  <label class="sr-only" for="todo">TODO</label>
  <input type="text" class="form-control mb-4 mr-sm-4 mb-sm-0" id="todo" name="todo" placeholder="TODO...">
  <button type="submit" class="btn btn-primary">Add</button>
</form>
</div>

<div class="container">
  <h2>TODOs</h2>
  <table class="table">
    <thead>
      <tr>
        <th class="col-xs-1">ID</th>
        <th class="col-xs-2">Timestamp</th>
        <th class="col-xs-9">TODO</th>
      </tr>
    </thead>
    <tbody>
    
<% 
	TodoDao dao = new TodoDao();
	List<Todo> todos = dao.getTodos();
		  
	for (Todo todo : todos) {
%>
	<tr>
		<td><%= todo.getId() %></td>
		<td><%= todo.getCreated() %></td>
		<td><%= todo.getTodo() %></td>
	</tr>
	
<%		
	}
%>


    </tbody>
  </table>
</div>

<%
// clear up any messages - they are just a one time thing.
	request.removeAttribute("dbErr");
	request.removeAttribute("dbMsg");
%>

	<!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script>window.jQuery || document.write('<script src="./js/vendor/jquery.min.js"><\/script>')</script>
    <script src="./js/bootstrap.min.js"></script>
</body>
</html>