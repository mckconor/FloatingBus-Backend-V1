package com.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.entities.Vehicle;

public interface VehicleRepository extends CrudRepository<Vehicle, Long>{
	public List<Vehicle> findAllByActive(Boolean active);
	public Vehicle findByActive(Boolean active);
}
