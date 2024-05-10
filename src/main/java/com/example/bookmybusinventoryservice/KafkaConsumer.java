package com.example.bookmybusinventoryservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
public class KafkaConsumer {

    private  BusInventoryRepository busInventoryRepository;
    private  KafkaProducer kafkaProducer;

    KafkaConsumer(BusInventoryRepository busInventoryRepository,
                  KafkaProducer kafkaProducer) {
        this.busInventoryRepository = busInventoryRepository;
        this.kafkaProducer=kafkaProducer;
    }

    Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "book-my-bus-payment-topic", groupId = "console-consumer-68654")
    public void consumeMessage(String message) throws JsonProcessingException {

        logger.info("Received message: " + message);

        ObjectMapper mapper = new ObjectMapper();
        PaymentMessage paymentMessage = mapper.readValue(message,PaymentMessage.class);

        //update available seats for this bus

        Optional<BusInventory> busInventory = busInventoryRepository.findById(paymentMessage.getBusId());
        busInventory.ifPresent(inventory -> {
            logger.info("Available seats for bus {}: {}",paymentMessage.getBusId(),inventory.getAvailableSeats());
            logger.info("Seats requested : {}",paymentMessage.getNoOfSeats());
            int remainingSeats = inventory.getAvailableSeats() - paymentMessage.getNoOfSeats();
            busInventoryRepository.updateAvailableSeatsByBusId(
                remainingSeats,
                paymentMessage.getBusId());
            logger.info("Updated available seats to {}",remainingSeats);
        });


        //send message to booking confirmation topic
        kafkaProducer.sendMessage(paymentMessage.getBookingId());
        logger.info("Successfully sent the booking confirmation message to booking confirmation kafka topic");

    }
}
