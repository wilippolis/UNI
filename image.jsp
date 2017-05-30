<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.json.simple.*" %>
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

    <title>Java (Tomcat) MySQL Hackathon Example - Image Analysis</title>

    <!-- Bootstrap core CSS -->
    <link href="./css/bootstrap.min.css" rel="stylesheet">

  </head>
<body>

<div class="container">
<button type="button" class="btn btn-link" onclick="window.location = 'index.html';">Home</button>
<h1>Example Image Recognition Page</h1>

</div>

<div class="container">
      <div class="panel panel-default">
        <div class="panel-heading"><strong>Upload File</strong> <small>Select an image file (.jpg or .png) to upload and analyze.</small></div>
        <div class="panel-body">

          <h4>Select files from your computer</h4>
          <form action="eval" method="post" enctype="multipart/form-data" id="js-upload-form">
            <div class="form-inline">
              <div class="form-group">
                <input type="file" name="image_file" id="js-upload-files">
              </div>
              <button type="submit" class="btn btn-sm btn-primary" id="js-upload-submit">Analyze image</button>
            </div>
          </form>

        </div>
      </div>
    </div> <!-- /container -->
    
    
    <div class="container">
    <div class="row">
 
    
    <% JSONObject results = (JSONObject) request.getAttribute("results");
    	if( results != null ) {
			JSONArray classifiers = (JSONArray) results.get("classifiers");
    		
    %>		
    <!--  classifiers -->
    <%
    		if( classifiers != null && classifiers.size()>0 ){
		    	for( int c=0; c< classifiers.size(); c++ ) {
		    		JSONObject classifier = (JSONObject) classifiers.get(c);
		    		String classifierId = (String) classifier.get("classifier_id");
		    		JSONArray values = (JSONArray) classifier.get("classes");
    %>
					<div class="col-md-4">
					<div class="panel panel-primary">
					<div class="panel-heading">
						<h3 class="panel-title">Classifier: <strong><%=classifierId %></strong></h3>
					</div>
				  	<div class="panel-body">
				    		 <table class="table">
						    <thead>
						      <tr>
						        <th class="col-xs-2">Class</th>
						        <th class="col-xs-2">Score</th>
						      </tr>
						    </thead>
						    <tbody>
				    		<%	for( int i=0;i<values.size(); i++ ) {
				    			JSONObject value = (JSONObject) values.get(i);
				    		%>
				    			<tr>
					    			<td><%= value.get("class") %></td>
					    			<td><%= value.get("score") %></td>
				    			</tr>
				    		<%  } %>
				    		</tbody>
				    		</table>
				        </div>
					</div>
					</div>
    	
			<% } } // classifiers %>
			
		<!-- faces -->
		<% // still within the results if
		JSONArray faces = (JSONArray) results.get("faces");
		if( faces != null && faces.size()>0 ){
    	for( int f=0; f<faces.size(); f++ ) {
    		JSONObject face = (JSONObject) faces.get(f);
		    JSONObject loc = (JSONObject) face.get("face_location"); 
		    if( loc != null ) {
		    	long top = (long) loc.get("top");
		    	long left = (long) loc.get("left");
		    	long width = (long) loc.get("width");
		    	long height = (long) loc.get("height");
		    	String location = "(" + left + ", " + top + ", " + width + ", " + height + ")";
	    %>
	   		<div class="col-md-4">
			<div class="panel panel-info">
				<div class="panel-heading">
							<h3 class="panel-title">Face <%=f %> <%= location %></h3>
				</div>
			  	<div class="panel-body">
		    		
		    		<table class="table">
				    <thead>
				      <tr>
				        <th class="col-xs-2">Property</th>
				        <th class="col-xs-2">Value</th>
				        <th class="col-xs-2">Score</th>
				      </tr>
				    </thead>
				    <tbody>
		    		
		    		<% 
				    JSONObject obj = (JSONObject) face.get("gender"); 
		    		if( obj != null ) {
					    String value = (String) obj.get("gender");
					    double score = (double) obj.get("score");
				    %>
				    	<tr>
				    		<td>Gender</td>
				    		<td><%= value %></td>
				    		<td><%= score %></td>
				    	</tr>
				    <% } // gender %>
		    		
		    		<% 
				    obj = (JSONObject) face.get("age"); 
		    		if( obj != null ) {
		    			long min = (long) obj.get("min");
		    			long max = (long) obj.get("max");
					    String age = min + " - " + max;
					    double score = (double) obj.get("score");
				    %>
				    	<tr>
				    		<td>Age</td>
				    		<td><%= age %></td>
				    		<td><%= score %></td>
				    	</tr>
				    <% } // age %>
		    		
		 			<% 
				    obj = (JSONObject) face.get("identity"); 
		    		if( obj != null ) {
					    String name = (String) obj.get("name");
					    double score = (double) obj.get("score");
				    %>
				    	<tr>
				    		<td>Identity</td>
				    		<td><%= name %></td>
				    		<td><%= score %></td>
				    	</tr>
				    <% } // identity %>
		    		</tbody>
		    		</table>
		        </div>
			</div>
			</div>
    	
    
    	<% } } } // faces	%>
		
		
    <% } // results %>
    
    </div>
    </div>
	<!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script>window.jQuery || document.write('<script src="./js/vendor/jquery.min.js"><\/script>')</script>
    <script src="./js/bootstrap.min.js"></script>
</body>
</html>