package com.helpers;

import com.entities.Request;
import org.json.*;

public class RequestParser {

	public static Request parseRequest(String body) {
		
		Request x = new Request();
		
		try {
			JSONObject jsonObj = new JSONObject(body);
			
			x.setAmount(Integer.parseInt(jsonObj.getString("amount")));

			x.setDestLatitude(jsonObj.getDouble("destLat"));
			x.setDestLongitude(jsonObj.getDouble("destLong"));

			x.setSourceLatitude(jsonObj.getDouble("sourceLat"));
			x.setSourceLongitude(jsonObj.getDouble("sourceLong"));
			
			x.setLocationName(jsonObj.getString("address"));
			
			x.setUserEmail(jsonObj.getString("email"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return x;
	}
	
}
