package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@SpringBootApplication
@Controller
public class DemoApplication {

    private static final boolean AUTO_CANCEL = false;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    EmitterProcessor<Message<DemoMessage>> processor = EmitterProcessor.create(AUTO_CANCEL);

    @PostMapping("/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> delegateToSupplier(@RequestBody String body) {
        Message<DemoMessage> message = MessageBuilder
                .withPayload(new DemoMessage(body))
                .build();

        processor.onNext(message);

        return Mono.empty();
    }

    @Bean
    public Supplier<Flux<Message<DemoMessage>>> messageSupplier() {
        return () -> processor;
    }

    private static class DemoMessage {

        private String body;

        public DemoMessage(String body) {
            this.body = body;
        }

        // TODO: intentionally removed to reproduce conversion issue
//        public DemoMessage() {
//        }
//
//        public String getBody() {
//            return body;
//        }
//
//        public void setBody(String body) {
//            this.body = body;
//        }
    }
}
