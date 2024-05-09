package com.example.bookmybusinventoryservice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BusInventoryRepository extends JpaRepository<BusInventory, String> {

}
