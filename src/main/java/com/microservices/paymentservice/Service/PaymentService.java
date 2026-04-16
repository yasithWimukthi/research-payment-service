package com.microservices.paymentservice.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class PaymentService {

    private static final String SERVICE = "payment-service";

    public String processPayment(String failure, int duration) {

        String requestId = UUID.randomUUID().toString();

        long start = System.currentTimeMillis();

        log.info("service={} requestId={} event=request_received",
                SERVICE, requestId);

        try {

            log.info("service={} requestId={} event=payment_validation_start",
                    SERVICE, requestId);

            simulateFailure(failure, duration);

            log.info("service={} requestId={} event=payment_validation_end",
                    SERVICE, requestId);

        } catch (Exception e) {

            log.error("service={} requestId={} event=payment_failed error={}",
                    SERVICE, requestId, e.getMessage());

            throw e;
        }

        long latency = System.currentTimeMillis() - start;

        if (latency > 1000) {
            log.warn("service={} requestId={} event=slow_response latency={}",
                    SERVICE, requestId, latency);
        }

        log.info("service={} requestId={} event=response_sent latency={}",
                SERVICE, requestId, latency);

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