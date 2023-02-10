package com.example.springcloudgateway;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class SimpleController {

    @RequestMapping("/")
    public Mono<String> index() {
        return Mono.just("hello");
    }
}
