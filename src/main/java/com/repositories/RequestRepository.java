package com.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.entities.Request;

public interface RequestRepository extends CrudRepository<Request, Long> {

	List<Request> findByUserId(Long id);
	List<Request> findAllByCompleted(Boolean completed);
}
