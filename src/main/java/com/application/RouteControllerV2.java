package com.application;

import static org.mockito.Matchers.doubleThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.components.Coordinates;
import com.components.Route;
import com.drew.lang.GeoLocation;
import com.entities.Request;
import com.entities.User;
import com.entities.Vehicle;
import com.entities.Waypoint;
import com.helpers.RequestParser;
import com.helpers.RouteHelper;
import com.repositories.RequestRepository;
import com.repositories.UserRepository;
import com.repositories.VehicleRepository;
import com.repositories.WaypointRepository;

import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;


@Controller
public class RouteControllerV2 {

	@Autowired private RequestRepository requestDao;
	@Autowired private UserRepository userDao;
	@Autowired private VehicleRepository vehicleDao;
	@Autowired private WaypointRepository waypointDao;
	
	private List<Waypoint> currentWaypoints;
	
	@RequestMapping("/addVehicle")
	public void AddVehicle(HttpEntity<String> httpEntity, HttpServletRequest request, HttpServletResponse response) {
		Vehicle newVehicle = new Vehicle();
		
		newVehicle.setActive(true);
		newVehicle.setCapacity(0);
		newVehicle.setMaxCapacity(20);
		newVehicle.setCurrentLatitude(53.3437935);
		newVehicle.setCurrentLongitude(-6.254571599999963);
		
		vehicleDao.save(newVehicle);
	}
	
	@RequestMapping("/addRequest")
	public void AddRequest(HttpEntity<String> httpEntity, HttpServletRequest request, HttpServletResponse response) {
		Request newReq = RequestParser.parseRequest(httpEntity.getBody());
		try {
			newReq.setUserId(userDao.findByEmail(newReq.getUserEmail()).getId());
			requestDao.save(newReq);
		} catch (Exception e) {
			//User doesn't exist
			System.err.println("ERROR: User '" + newReq.getUserEmail() +"' does not exist.");
			return;
		}
		
		List<Waypoint> waypoints = CalculateWaypoints();
		currentWaypoints = waypoints;
		UpdateVehicleRoute();
	}
	
	@RequestMapping("/liveMap")
	public String LiveMap (HttpEntity<String> httpEntity, HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("vehicles", vehicleDao.findAll());
		model.addAttribute("waypoints", waypointDao.findAll());
//		model.addAttribute("waypoints", currentWaypoints);
		model.addAttribute("requests", requestDao.findAllByCompleted(false));
		return "liveMap";
	}
	
	public List<Waypoint> CalculateWaypoints() {
		//Collect uncompleted requests in same area
		//Choose initial request at random, loop until all requests allocated to a waypoint
		List<Waypoint> waypoints = new ArrayList<Waypoint>();
		for(Request req : requestDao.findAllByCompleted(false)){
			//Drop off wp
			Waypoint wp1 = new Waypoint();

			wp1.setLatitude(req.getDestLatitude());
			wp1.setLongitude(req.getDestLongitude());
			
			wp1.setDropOffs(new ArrayList<User>());
			wp1.getDropOffs().add(userDao.findById(req.getUserId()));
			
			wp1.setRequests(new ArrayList<Request>());
			wp1.getRequests().add(req);
			
			waypoints.add(wp1);

			//Pick up wp
			Waypoint wp2 = new Waypoint();
			
			wp2.setLatitude(req.getSourceLatitude());
			wp2.setLongitude(req.getSourceLongitude());

			wp2.setPickUps(new ArrayList<User>());
			wp2.getPickUps().add(userDao.findById(req.getUserId()));
			
			wp2.setRequests(new ArrayList<Request>());
			wp2.getRequests().add(req);
			
			waypoints.add(wp2);
		}
		
		//Combine neighbouring waypoints that are close enough
		List<Waypoint> combinedWaypoints = new ArrayList<Waypoint>();
		for(int i = 0; i<waypoints.size(); i++) {
			for(int j = i+1; j<waypoints.size(); j++) {
				double distance = RouteHelper.GetDistance(new Coordinates(waypoints.get(i).getLatitude(), waypoints.get(i).getLongitude()), 
						new Coordinates(waypoints.get(j).getLatitude(), waypoints.get(j).getLongitude()));
				System.out.println(distance);
				
				if(distance < waypoints.get(i).getRadius()) {
					//They can be combined
					Waypoint newWp = waypoints.get(i);

					double avgLat = (waypoints.get(i).getLatitude() +  waypoints.get(j).getLatitude()) / 2;
					double avgLong = (waypoints.get(i).getLongitude() +  waypoints.get(j).getLongitude()) / 2;
					newWp.setLatitude(avgLat);
					newWp.setLongitude(avgLong);

					newWp.setDropOffs(new ArrayList<User>());
					newWp.setPickUps(new ArrayList<User>());

					try {
						newWp.getDropOffs().addAll(waypoints.get(i).getDropOffs());
						newWp.getDropOffs().addAll(waypoints.get(j).getDropOffs());
						newWp.getPickUps().addAll(waypoints.get(i).getPickUps());
						newWp.getPickUps().addAll(waypoints.get(j).getPickUps());
					} catch (Exception e) {
						
					}
					
					newWp.setRequests(new ArrayList<Request>());
					newWp.getRequests().addAll(waypoints.get(i).getRequests());
					newWp.getRequests().addAll(waypoints.get(j).getRequests());
					
					//Remove @ j, but then decrement j to allow
					waypoints.remove(j);
					
					combinedWaypoints.add(newWp);
				}
			}
		}
		waypoints = combinedWaypoints;
		
		
		//Now update waypoint centers
		for(Waypoint wp : waypoints) {
			double totalLat = 0, totalLong = 0;
			for(Request req : wp.getRequests()){
				//Which coords affect this waypoint? The source or the dest?
				if(RouteHelper.GetDistance(new Coordinates(wp.getLatitude(), wp.getLongitude()), new Coordinates(req.getDestLatitude(), req.getDestLongitude())) < wp.getRadius()){
					totalLat += req.getDestLatitude();
					totalLong += req.getDestLongitude();
				} else {
					totalLat += req.getSourceLatitude();
					totalLong += req.getSourceLongitude();
				}
			}
			double avgLat = totalLat/wp.getRequests().size();
			double avgLong = totalLong/wp.getRequests().size();
			
			wp.setLatitude(avgLat);
			wp.setLongitude(avgLong);
			
			waypointDao.save(wp);
		}
		return waypoints;
	}
	
	public String getAddressFromCoords(double lat, double longitude) {
		try {
			URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?latlng="
	                + lat + "," + longitude + "&sensor=true");
	        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	        String formattedAddress = "";
	        
	        try {
	            InputStream in = url.openStream();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	            String result, line = reader.readLine();
	            result = line;
	            while ((line = reader.readLine()) != null) {
	                result += line;
	            }
	 
	            System.out.println(result.toString());
	            
	            JSONObject x = new JSONObject(result.toString());
	            return(x.getString("formatted_address"));
	            
	        } finally {
	            urlConnection.disconnect();
	            return formattedAddress;
	        }
		} catch (Exception e) { 
			
		}
		return "";
	}
	
	public void UpdateVehicleRoute() {
		//Find nearest vehicle, that's active now
		Vehicle v = null;
		for(Vehicle tempVehicle : vehicleDao.findAllByActive(true)) {
			if(tempVehicle.getGivenRoute() == null) {
				v = tempVehicle;
			}
		}
		if(v == null) {
			System.err.println("ERROR: No available vehicles");
			return;
		}
		
		List<Waypoint> waypoints = waypointDao.findAll();
		
		//Active, good! Update or assign route
		//Start route from nearest waypoint
		Waypoint nearest = waypoints.get(0);
		for(Waypoint wp : waypoints) {
			if(RouteHelper.GetDistance(new Coordinates(v.getCurrentLatitude(), v.getCurrentLongitude()), new Coordinates(wp.getLatitude(), wp.getLongitude()))
					< RouteHelper.GetDistance(new Coordinates(v.getCurrentLatitude(), v.getCurrentLongitude()), new Coordinates(nearest.getLatitude(), nearest.getLongitude()))) {
				nearest = wp;
			}
			
			//Vehicle routing part here
			wp.setVehicleId(v.getId());
		}
		
		Route route = new Route();
		route.SetInitWaypoint(nearest);
		route.setWaypoints(waypoints);
		
		v.setGivenRoute(route);
		
		v.setWaypoints(waypoints);
	}
}
