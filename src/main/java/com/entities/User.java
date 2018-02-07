package com.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
public class User {
	
	public static final String DRIVER = "DRIVER", PASSENGER = "PASSENGER";

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Getter
	private Long id;

	@Getter @Setter
	private String email;
	
	@Getter @Setter
	private String userRole;
	
	@Getter @Setter
	private double currentLatitude, currentLongitude;
}
