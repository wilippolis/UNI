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
package com.ibm.bluemix.hack.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class VcapServicesHelper {
	
	static private JSONObject _vcapServices = null;
	
	static public JSONObject getCredentials(String serviceName, String instanceName) {
		
		if( _vcapServices == null || _vcapServices.size()==0 ) {
			String vcapServicesStr = System.getenv("VCAP_SERVICES");
			JSONParser parser = new JSONParser();
			try {
				_vcapServices = (JSONObject) parser.parse(vcapServicesStr);
			} catch (ParseException e) {
				e.printStackTrace();
				System.err.println("Unable to parse VCAP_SERVICES " + e.getMessage());
				return null;
			}
		}

		JSONArray services = (JSONArray) _vcapServices.get(serviceName);
		if( services == null ) {
			System.err.println("Service " + serviceName + " not bound to this application.");
			return null;
		}
		
		if( instanceName == null ) {
			// grab the first one in the array
			JSONObject service = (JSONObject) services.get(0);
			JSONObject creds = (JSONObject) service.get("credentials");
			return creds;
		} else {
			for(int i=0;i<services.size(); i++) {
				JSONObject service = (JSONObject) services.get(0);
				String name = (String) service.get("name");
				if(instanceName.equals(name)) {
					JSONObject creds = (JSONObject) service.get("credentials");
					return creds;
				}
			}
		}

		System.err.println("Service " + serviceName + " with instance name " + instanceName + " not bound to this application.");
		return null;
	}

}
