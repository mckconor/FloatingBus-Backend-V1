package com.application;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.optaplanner.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.components.Coordinates;
import com.components.Driver;
import com.components.Passenger;
import com.components.Route;
import com.entities.Request;
import com.entities.Vehicle;
import com.entities.Waypoint;
import com.repositories.RequestRepository;
import com.helpers.*;

//@Controller
public class RouteControllerV1 {

	@Autowired
	private RequestRepository requestDao;
	
	private List<Request> requests;
	private List<Vehicle> vehicles;
	
	Coordinates depot = new Coordinates(53.343761, -6.255194);		//Trinity College Dublin

	public void plotRoute () {
		
	}
	
	public void AddLocation (String locationName, double latitude, double longitude) {
		Request newRequest = new Request();
		newRequest.setLocationName(locationName);
		newRequest.setDestLatitude(latitude);
		newRequest.setDestLongitude(longitude);
		
		if(requests == null) {
			requests = new ArrayList<Request>();
		}
		
		requests.add(newRequest);
		
		requestDao.save(newRequest);
	}

	@RequestMapping("/addVehicle")
	public void AddVehicle (HttpEntity<String> httpEntity, HttpServletRequest request, HttpServletResponse response, @RequestParam("maxCapacity") int maxCapacity) {
		if(vehicles == null) {
			vehicles = new ArrayList<Vehicle>();
		}
		
		Driver newDriver = new Driver();
		Vehicle newVehicle = new Vehicle();
//		newVehicle.setDriver(newDriver);
		newVehicle.setMaxCapacity(maxCapacity);
		newVehicle.setCapacity(0);
		newVehicle.setCurrentPosition(depot);
		newVehicle.setCurrentLatitude(depot.latitude);
		newVehicle.setCurrentLongitude(depot.longitude);
		newVehicle.setDestination(depot);
		
		vehicles.add(newVehicle);
	}		

	@RequestMapping("/updateVehicle")
	public void UpdateVehicle (HttpEntity<String> httpEntity, HttpServletRequest request, HttpServletResponse response, @RequestParam("id") int vehicleId, @RequestParam("capacity") int capacity, @RequestParam("latitude") double latitude, @RequestParam("longitude") double longitude) {
		Vehicle selected = null;
		for(Vehicle v : vehicles) {
			if(v.getId() == vehicleId) {
				selected = v;
			}
		}
		if(selected == null) {
			return;
		}
		
		selected.setCurrentPosition(new Coordinates(latitude, longitude));
		selected.setCurrentLatitude(latitude);
		selected.setCurrentLongitude(longitude);
		selected.setCapacity(capacity);
		

		for(Vehicle v : vehicles) {
			if(v.getId() == vehicleId) {
				v = selected;
			}
		}
	}
	

	@RequestMapping("/makeRequest")
	public void MakeRequest (HttpEntity<String> httpEntity, HttpServletRequest request, HttpServletResponse response, @RequestParam("email") String email, @RequestParam("capacity") int capacity, @RequestParam("latitude") double latitude, @RequestParam("longitude") double longitude) {
		//Find suitable vehicle
		Vehicle bestFit = FindVehicleGoingNearDestination(1, new Coordinates(latitude, longitude));
		
		Request x = RequestParser.parseRequest(httpEntity.getBody().toString());
		
		//Get passenger details (create for now)
		Passenger newPassenger = new Passenger();
		newPassenger.setEmail(email);
		newPassenger.setRequestDestination(new Coordinates(latitude, longitude));
		
		//Assign passenger to it
		if(bestFit.getPassengers() == null) {
			bestFit.setPassengers(new ArrayList<Passenger>());
		}
		if(!bestFit.getPassengers().contains(newPassenger)) {
			bestFit.getPassengers().add(newPassenger);
		}
		
		//Update vehicle destination
		UpdateVehicleDestination(bestFit);

		//Add to waypoint
		WaypointCalc(bestFit, x);
		
		AddLocation(x.getLocationName(), x.getDestLatitude(), x.getDestLongitude());
		saveRequest (x);
	}
	
	private void WaypointCalc (Vehicle vehicle, Request req) {
		if(vehicle.getGivenRoute() == null) {
			//Create/Assign route
			Route x = new Route();
			Waypoint initWaypoint = new Waypoint();
			x.SetInitWaypoint(initWaypoint);
			vehicle.setGivenRoute(x);
		} else {
			List<Waypoint> tempList = vehicle.getGivenRoute().getWaypoints();
			//Is request within radius of any waypoint?
			double closestDist = 0;
			Waypoint closestWaypoint = null;
			for(Waypoint w : tempList) {
				double tempDist = Math.sqrt(Math.pow((req.getDestLatitude() - w.getLatitude()), 2) + Math.pow((req.getDestLongitude() - w.getLongitude()), 2));
				if(closestDist <= 0 || tempDist < closestDist) {
					closestDist = tempDist;
					closestWaypoint = w;
				}
			}
			//But is it in the radius?!
			if(closestDist < closestWaypoint.getRadius()) {
				Waypoint.AddRequestToWaypoint(closestWaypoint, req);
			} else {
				//New waypoint
				Waypoint newWaypoint = new Waypoint();
				Waypoint.AddRequestToWaypoint(newWaypoint, req);
				tempList.add(newWaypoint);
			}
		}
	}
	
	private void saveRequest (Request x) {
		requestDao.save(x);
	}
	
	private void UpdateVehicleDestination (Vehicle vehicle) {
		double latitude = 0, longitude = 0;
		for(Passenger p : vehicle.getPassengers()) {
			latitude += p.getRequestDestination().latitude;
			longitude += p.getRequestDestination().longitude;
		}
		latitude /= vehicle.getPassengers().size();
		longitude /= vehicle.getPassengers().size();

		vehicle.setDestination(new Coordinates(latitude, longitude));
		vehicle.setDestinationLatitude(latitude);
		vehicle.setDestinationLongitude(longitude);;
		
		for(Vehicle v : vehicles) {
			if(v.getId() == vehicle.getId()) {
				v = vehicle;
			}
		}
	}
	
	private Vehicle FindVehicleGoingNearDestination (float radius, Coordinates requestDestination) {
		Vehicle bestFit = null;
		if(vehicles == null) { return null; }
		else {
			for(Vehicle v : vehicles) {
				if(v.getDestination() != null) {
					if(GetDistance(v.getDestination(), requestDestination) < radius) {
						bestFit = v;
					}
				}
			}
			
			if(bestFit == null) {
				bestFit = vehicles.get(0);
			}
		}
		
		return bestFit;
	}
	
	private double GetDistance (Coordinates a, Coordinates b) {
		double x1 = a.latitude;
		double x2 = b.latitude;

		double y1 = a.longitude;
		double y2 = b.longitude;
		
		double ans = Math.sqrt(Math.pow((x2-x1), 2) + Math.pow(y2-y1, 2));
		
		return ans;
	}

	@RequestMapping("/heatMap")
	public String HeatMapView (Model model) {	
		model.addAttribute("req", requests);	
		model.addAttribute("vehicles", vehicles);
		return "heatmap";
	}
}