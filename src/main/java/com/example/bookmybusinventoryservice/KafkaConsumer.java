package com.example.bookmybusinventoryservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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
    private final String bookingConfirmationTopic;
    private final String inventoryUpdateFailureTopic;

    KafkaConsumer(BusInventoryRepository busInventoryRepository,
                  KafkaProducer kafkaProducer,
                  String booking_confirmation_topic_name,
                  String inventory_update_failure_topic_name) {
        this.busInventoryRepository = busInventoryRepository;
        this.kafkaProducer=kafkaProducer;
        this.bookingConfirmationTopic = booking_confirmation_topic_name;
        this.inventoryUpdateFailureTopic = inventory_update_failure_topic_name;
    }

    Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Transactional
    @KafkaListener(topics = "book-my-bus-payment-topic", groupId = "console-consumer-68654")
    public void consumeMessage(String message) throws JsonProcessingException {

        logger.info("Received message: " + message);

        ObjectMapper mapper = new ObjectMapper();
        PaymentMessage paymentMessage = mapper.readValue(message,PaymentMessage.class);
        try {
            //update available seats for this bus

            Optional<BusInventory> busInventory = busInventoryRepository.findById(paymentMessage.getBusId());
            if (busInventory.isEmpty()) {
                throw new RuntimeException("Invalid bus id");
            }
            busInventory.ifPresent(inventory -> {
                logger.info("Available seats for bus {}: {}", paymentMessage.getBusId(), inventory.getAvailableSeats());
                logger.info("Seats requested : {}", paymentMessage.getNoOfSeats());
                int remainingSeats = inventory.getAvailableSeats() - paymentMessage.getNoOfSeats();
                busInventoryRepository.updateAvailableSeatsByBusId(
                        remainingSeats,
                        paymentMessage.getBusId());
                logger.info("Updated available seats to {}", remainingSeats);
            });


            //send message to booking confirmation topic
            kafkaProducer.sendMessage(bookingConfirmationTopic, paymentMessage.getBookingId());
            logger.info("Successfully sent the booking confirmation message to booking confirmation kafka topic");
        }catch(Exception e){
            logger.info("Exception occurred while processing message {}. Exception details : {}",message,e.getMessage());
            kafkaProducer.sendMessage(inventoryUpdateFailureTopic,paymentMessage.getBookingId());
        }
    }

    @Transactional
    @KafkaListener(topics = "book-my-bus-booking-confirmation-failure-topic", groupId = "console-consumer-68654")
    public void consumeBookingConfirmationFailureMessage(String message) throws JsonProcessingException {

        logger.info("Received message: " + message);

        ObjectMapper mapper = new ObjectMapper();
        InventoryUpdateMessage inventoryUpdateMessage = mapper.readValue(message,InventoryUpdateMessage.class);

            //update available seats for this bus

            Optional<BusInventory> busInventory = busInventoryRepository.findById(inventoryUpdateMessage.getBusId());
            if (busInventory.isEmpty()) {
                throw new RuntimeException("Invalid bus id");
            }
            busInventory.ifPresent(inventory -> {
                logger.info("Available seats for bus {}: {}", inventoryUpdateMessage.getBusId(), inventory.getAvailableSeats());
                logger.info("No of seats for which booking confirmation failed : {}", inventoryUpdateMessage.getNoOfBookings());
                int totalSeats = inventory.getAvailableSeats() + inventoryUpdateMessage.getNoOfBookings();
                busInventoryRepository.updateAvailableSeatsByBusId(
                        totalSeats,
                        inventoryUpdateMessage.getBusId());
                logger.info("Updated available seats to {}", totalSeats);
            });

            //send message to booking confirmation topic
            kafkaProducer.sendMessage(inventoryUpdateFailureTopic, inventoryUpdateMessage.getBookingId());
            logger.info("Successfully sent the booking confirmation message to booking confirmation kafka topic");


    }
}
