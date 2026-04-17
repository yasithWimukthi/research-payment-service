package com.microservices.paymentservice.service;

import com.microservices.paymentservice.model.LogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final LogSender logSender;

    private static final String SERVICE = "payment-service";

    public String processPayment(String failure, int duration) {

        String requestId = UUID.randomUUID().toString();

        long start = System.currentTimeMillis();

        log.info("service={} requestId={} event=request_received",
                SERVICE, requestId);

        logSender.send(new LogEvent(
                Instant.now().toString(),
                "payment-service",
                "INFO",
                "request_received",
                requestId,
                null,
                null
        ));

        try {

            log.info("service={} requestId={} event=payment_validation_start",
                    SERVICE, requestId);

            logSender.send(new LogEvent(
                    Instant.now().toString(),
                    "payment-service",
                    "INFO",
                    "payment_validation_start",
                    requestId,
                    null,
                    null
            ));

            simulateFailure(failure, duration);

            log.info("service={} requestId={} event=payment_validation_end",
                    SERVICE, requestId);

            logSender.send(new LogEvent(
                    Instant.now().toString(),
                    "payment-service",
                    "INFO",
                    "payment_validation_end",
                    requestId,
                    null,
                    null
            ));

        } catch (Exception e) {

            log.error("service={} requestId={} event=payment_failed error={}",
                    SERVICE, requestId, e.getMessage());

            logSender.send(new LogEvent(
                    Instant.now().toString(),
                    "payment-service",
                    "ERROR",
                    "payment_failed",
                    requestId,
                    null,
                    e.getMessage()
            ));

            throw e;
        }

        long latency = System.currentTimeMillis() - start;

        if(latency > 1000){
            log.warn("service={} requestId={} event=slow_response latency={}",
                    SERVICE, requestId, latency);

            logSender.send(new LogEvent(
                    Instant.now().toString(),
                    "payment-service",
                    "WARN",
                    "slow_response",
                    requestId,
                    latency,
                    null
            ));
        }

        log.info("service={} requestId={} event=response_sent latency={}",
                SERVICE, requestId, latency);

        logSender.send(new LogEvent(
                Instant.now().toString(),
                "payment-service",
                "INFO",
                "response_sent",
                requestId,
                latency,
                null
        ));

        return "payment-success";
    }

    private void simulateFailure(String failure,  int duration) {

        try {

            if ("latency".equalsIgnoreCase(failure)) {

                Thread.sleep(duration);
            }

            else if ("error".equalsIgnoreCase(failure)) {

                throw new RuntimeException("Payment gateway failure");
            }

            else if ("cpu".equalsIgnoreCase(failure)) {

                long end = System.currentTimeMillis() + duration;

                while (System.currentTimeMillis() < end) {
                    Math.sqrt(Math.random());
                }
            }

            else {

                Thread.sleep(200);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}