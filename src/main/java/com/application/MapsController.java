package com.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;

import com.components.Coordinates;

@Controller
public class MapsController  {

	@RequestMapping("/maps")
	public String maps(Model model) {
		Coordinates[] coordsArray = {
				new Coordinates(53.337992, -6.258764),
				new Coordinates(53.336294, -6.257348),
				new Coordinates(53.337594, -6.262573),
				new Coordinates(53.337485, -6.262058),
				
				new Coordinates(53.351379, -6.263546),
				new Coordinates(53.351347, -6.263975),
				new Coordinates(53.352039, -6.262591),
				new Coordinates(53.351501, -6.264243)};

		double[] coordList_long = new double[coordsArray.length];
		double[] coordList_lat = new double[coordsArray.length];
		
		for(int i = 0; i<coordsArray.length; i++) {
			coordList_long[i] = coordsArray[i].longitude;
			coordList_lat[i] = coordsArray[i].latitude;
		}

		model.addAttribute("coordinates_long", coordList_long);
		model.addAttribute("coordinates_lat", coordList_lat);
		return "maps";
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/makeRequest_depr", method = RequestMethod.POST)
	@ResponseBody
	public String makeRequest(HttpEntity<String> httpEntity, HttpServletRequest request, HttpServletResponse response, @RequestParam("place") String address, Model model) {
		
		String jsonString = httpEntity.getBody();
		
		System.out.println(jsonString);
		return "makeRequest";
	}
	
	public static void MapsStuff() {
		// TODO Auto-generated method stub
		RestTemplate restTemplate = new RestTemplate();
		Map<String, String> vars = new HashMap<String, String>();
		 
		vars.put("address", "1600 Amphitheatre Parkway, Mountain View, CA");
		vars.put("sensor", "false");
		String result = restTemplate
		.getForObject(
		"http://maps.googleapis.com/maps/api/geocode/xml?address={address}&sensor={sensor}",
		 
		String.class, vars);
		 
		System.out.println(result);
	}


}
