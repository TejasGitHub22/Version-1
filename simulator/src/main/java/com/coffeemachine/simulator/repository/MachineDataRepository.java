package com.coffeemachine.simulator.repository;

import com.coffeemachine.simulator.model.MachineData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineDataRepository extends JpaRepository<MachineData, Integer> {

}