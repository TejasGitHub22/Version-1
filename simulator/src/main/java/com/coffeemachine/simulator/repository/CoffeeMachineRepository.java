package com.coffeemachine.simulator.repository;

import com.coffeemachine.simulator.model.CoffeeMachine;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CoffeeMachineRepository extends JpaRepository<CoffeeMachine, Integer> {

	@Query(value = "select max(id) from coffee_machine", nativeQuery = true)
	Integer findMaxId();

	Optional<CoffeeMachine> findById(Integer id);
}
