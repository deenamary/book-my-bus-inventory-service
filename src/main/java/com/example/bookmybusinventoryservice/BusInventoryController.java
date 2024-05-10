package com.example.bookmybusinventoryservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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

    @PutMapping("update/businventory")
    public ResponseEntity<BusInventory> updateBusInventory(@RequestBody BusInventory busInventory)
    {
        if((busInventoryRepository.findById(busInventory.getBusId()).isPresent())){
            busInventory.setLastUpdatedDate(new Date());
            busInventoryRepository.save(busInventory);
            return ResponseEntity.ok(busInventory);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("delete/businventory/{busid}")
    public ResponseEntity<String> deleteBusInventory(@PathVariable String busid)
    {
        busInventoryRepository.deleteById(busid);
        return ResponseEntity.ok().body("Deleted successfully");
    }
}
