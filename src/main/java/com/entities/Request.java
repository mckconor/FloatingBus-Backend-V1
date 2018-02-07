package com.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.junit.Ignore;

import com.components.Coordinates;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "requests")
public class Request {

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Getter
	private Long id;
	
	@Getter @Setter
	private String userEmail;
	
	@Getter @Setter
	private long userId;
	
	@Getter @Setter
	private String locationName;
	
	@Transient
	@Getter @Setter
	private Coordinates locationCoords;

	@Getter @Setter
	private double destLatitude, destLongitude;

	@Getter @Setter
	private double sourceLatitude, sourceLongitude;
	
	@Getter @Setter
	private int amount;
	
	@Getter @Setter
	private boolean completed;
	
	public Request() {}
}
