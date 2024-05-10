package com.example.bookmybusinventoryservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String bookingConfirmationTopic;

    Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate,
                         String booking_confirmation_topic_name) {
        this.kafkaTemplate = kafkaTemplate;
        this.bookingConfirmationTopic=booking_confirmation_topic_name;
    }

    public void sendMessage(String message) {
        kafkaTemplate.send(bookingConfirmationTopic, message);
        logger.info("Message {} has been successfully sent to the topic: {}" ,message, bookingConfirmationTopic);
    }
}
