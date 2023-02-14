package com.example.businessapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RefreshScope
public class SimpleController {

    @Autowired
    private DiscoveryClient discoveryClient;
/*    @Autowired
    private RestTemplate restTemplate;*/

    @GetMapping("/config/sample")
    public String sample(
            @Value("${foo}") String foo,
            @Value("${test}") String test
    ) {
        return "foo: " + foo + ", \n" + "test: " + test;
    }
    @RequestMapping("/")
    public String home() {
        return "Hello world App1";
    }
/*

    @RequestMapping("/get")
    public String GetApp() {
        return "this is App1";
    }

    @RequestMapping("/get1")
    public String GetApp1() {
        return "this is App1";
    }

    @RequestMapping("/call-service")
    public String callService() {
        return getFirstProduct();
    }

    @RequestMapping("/services")
    public String services() {
        return serviceUrl();
    }

    public String serviceUrl() {
        List<ServiceInstance> list = discoveryClient.getInstances("business-app1");
        if (list != null && list.size() > 0) {
            List<String> strings = list.stream().map(item -> item.getUri().toString()).toList();
            return String.join(",", strings);
        }
        return null;
    }

    public String getFirstProduct() {
        return this.restTemplate.getForObject("http://business-app2/", String.class);
    }*/
}
