package com.coffeemachine.simulator.repository;

import com.coffeemachine.simulator.model.MachineData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MachineDataRepository extends JpaRepository<MachineData, Integer> {

    List<MachineData> findByMachineIdOrderByTimestampDesc(Integer machineId);

    List<MachineData> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime startDate, LocalDateTime endDate);

    List<MachineData> findByTimestampAfterOrderByTimestampDesc(LocalDateTime timestamp);
}