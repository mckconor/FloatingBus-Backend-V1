package components;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Vehicle {
	
	@Getter @Setter
	private int id;
	
	@Getter @Setter
	private String registration;

	@Getter @Setter
	private int capacity;
	
	@Getter @Setter
	private int maxCapacity;
	
	@Getter @Setter
	private List<Passenger> passengers;
	
	@Getter @Setter
	private Driver driver;
	
	@Getter @Setter
	private double currentLatitude, currentLongitude;
	
	@Getter @Setter
	private double destinationLatitude, destinationLongitude;
	
	@Getter @Setter
	private Coordinates currentPosition, destination;
}
