package com.example.bookmybusinventoryservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {


    @Value("${kafka.booking.confirmation.topic.name}")
    String bookingConfirmationTopicName;

    @Bean("booking_confirmation_topic_name")
    String getBookingKafkaTopic() {
        return bookingConfirmationTopicName;
    }
}
