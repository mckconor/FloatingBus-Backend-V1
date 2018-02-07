package com.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.entities.Waypoint;

public interface WaypointRepository extends CrudRepository<Waypoint, Long>{
	public List<Waypoint> findAllByVehicleId(long id);
	public List<Waypoint> findAll();
}
