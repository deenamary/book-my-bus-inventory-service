package com.example.bookmybusinventoryservice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import javax.xml.crypto.Data;
import java.util.Date;

@Entity
@Table(name = "businventory")
public class BusInventory {

    @Id
    @Column(name = "busid", length = 500)
    private String busId;

    @Column(name = "available_seats", length = 20)
    private Integer availableSeats;

    @Column(name = "last_updated_date", length = 20)
    private Date lastUpdatedDate;


    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
}
