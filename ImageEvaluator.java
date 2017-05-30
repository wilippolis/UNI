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
package com.ibm.bluemix.hack.image;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ibm.bluemix.hack.util.VcapServicesHelper;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


@WebServlet("/eval")
public class ImageEvaluator extends HttpServlet {

	private static final long serialVersionUID = 1186968436292476469L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImageEvaluator() {
		super();
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	public JSONObject analyzeImage(byte[] buf) throws IOException {
		
		JSONObject imageProcessingResults = new JSONObject();
		
		JSONObject creds = VcapServicesHelper.getCredentials("watson_vision_combined", null);

		String baseUrl = creds.get("url").toString();
		String apiKey = creds.get("api_key").toString();
		String detectFacesUrl = baseUrl + "/v3/detect_faces?api_key=" + apiKey + "&version=2016-05-20";
		String classifyUrl = baseUrl + "/v3/classify?api_key=" + apiKey + "&version=2016-05-20";

		OkHttpClient client = new OkHttpClient();

		RequestBody requestBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("images_file", "sample.jpg", RequestBody.create(MediaType.parse("image/jpg"), buf))
				.build();

		Request request = new Request.Builder()
				.url(detectFacesUrl)
				.post(requestBody).build();

		Response response = client.newCall(request).execute();
		String result = response.body().string();
		
		JSONParser jsonParser = new JSONParser();
		
		try {
			JSONObject results = (JSONObject) jsonParser.parse(result);
			// since we only process one image at a time, let's simplfy the json 
			// we send to the JSP.
			JSONArray images = (JSONArray) results.get("images");
			if( images != null && images.size()>0 ) {
				JSONObject firstImage = (JSONObject) images.get(0);
				JSONArray faces = (JSONArray) firstImage.get("faces");
				imageProcessingResults.put("faces", faces);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// now request classification
		request = new Request.Builder()
				.url(classifyUrl)
				.post(requestBody).build();

		response = client.newCall(request).execute();
		result = response.body().string();
		try {
			JSONObject results = (JSONObject) jsonParser.parse(result);
			// since we only process one image at a time, let's simplfy the json 
			// we send to the JSP.
			JSONArray images = (JSONArray) results.get("images");
			if( images != null && images.size()>0 ) {
				JSONObject firstImage = (JSONObject) images.get(0);
				JSONArray classifiers = (JSONArray) firstImage.get("classifiers");
				imageProcessingResults.put("classifiers", classifiers);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		

		return imageProcessingResults;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		
		response.sendRedirect("image.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Part file = request.getPart("image_file");
		InputStream is = file.getInputStream();
		byte[] buf = IOUtils.toByteArray(is);
		JSONObject results = analyzeImage(buf);
		
		request.setAttribute("results", results);
		
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/image.jsp");
		dispatcher.forward(request,response);

	}

}
