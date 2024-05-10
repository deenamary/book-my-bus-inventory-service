package com.example.bookmybusinventoryservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface BusInventoryRepository extends JpaRepository<BusInventory, String> {

    @Transactional
    @Modifying
    @Query("update BusInventory b set b.availableSeats = ?1 where b.busId = ?2")
    int updateAvailableSeatsByBusId(int availableSeats, String busId);

}
