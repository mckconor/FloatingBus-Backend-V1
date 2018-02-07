package com.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "waypoints")
public class Waypoint {

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Getter
	private long id;
	
	@Getter @Setter
	private long vehicleId;

	//Requests this waypoint fulfills
	@Transient
	@Getter @Setter
	private List<Request> requests;
	
	//Average of all requests
	@Getter @Setter
	private double latitude, longitude;
	
	@Getter
	private double radius =  0.007229; //~ distance Trinity to St. Stephens Green
	
	@Getter @Setter
	private double distance;
	
	@Getter @Setter
	private String locationName;

	@Transient
	@Getter @Setter
	private Waypoint next;

	@Transient
	@Getter @Setter
	private List<User> pickUps, dropOffs;
	
	@Getter @Setter
	private int capacityAt;		//What's the capacity it leaves at? How much space for next pickups
	
	public Waypoint() {}
	
	public static void AddRequestToWaypoint(Waypoint waypoint, Request req) {
		waypoint.requests.add(req);
		//Update
		double tempLat = 0, tempLong = 0;
		for(Request r : waypoint.requests) {
			tempLat += r.getDestLatitude();
		}
		
		waypoint.setLatitude(tempLat);
		waypoint.setLongitude(tempLong);
		
	}
}
