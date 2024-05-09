package com.example.bookmybusinventoryservice;

import jakarta.ws.rs.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1")
public class BusInventoryController {

    private  BusInventoryRepository busInventoryRepository;

    BusInventoryController(BusInventoryRepository busInventoryRepository) {
        this.busInventoryRepository = busInventoryRepository;
    }

    @PostMapping("add/businventory")
    public ResponseEntity<BusInventory> addBusInventory(@RequestBody BusInventory busInventory)
    {
        busInventory.setLastUpdatedDate(new Date());
        busInventoryRepository.save(busInventory);
        return ResponseEntity.ok(busInventory);
    }

    @GetMapping("get/businventory/{busid}")
    public ResponseEntity<BusInventory> getBusInventory(@PathVariable String busid)
    {
        Optional<BusInventory> busInventory = busInventoryRepository.findById(busid);
        return busInventory.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
