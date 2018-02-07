package com.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.components.Coordinates;
import com.components.Driver;
import com.components.Passenger;
import com.components.Route;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vehicles")
public class Vehicle {

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Getter
	private int id;
	
	@Getter @Setter
	private boolean active;
	
	@Getter @Setter
	private String registration;

	@Getter @Setter
	private int capacity;
	
	@Getter @Setter
	private int maxCapacity;
	
	@Transient
	@Getter @Setter
	private List<Passenger> passengers;
	
	@Getter @Setter
	private int driverId;
	
	@Getter @Setter
	private double currentLatitude, currentLongitude;
	
	@Getter @Setter
	private double destinationLatitude, destinationLongitude;
	
	@Getter @Setter
	private String locationName;
	
	@Transient
	@Getter @Setter
	private Coordinates currentPosition, destination;
	
	@Transient
	@Getter @Setter
	public Route givenRoute;
	
	@Transient
	@Getter @Setter
	private List<Waypoint> waypoints;
}
