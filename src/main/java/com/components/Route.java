package com.components;


import java.util.ArrayList;
import java.util.List;

import com.entities.Waypoint;

import lombok.Getter;
import lombok.Setter;

public class Route {

	@Getter @Setter
	public List<Waypoint> waypoints;
	
	public void SetInitWaypoint(Waypoint waypoint) {
		waypoints = new ArrayList<Waypoint>();
		waypoints.add(waypoint);
	}
}
