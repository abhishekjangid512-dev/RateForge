package com.rateforge.protectedapi.controller;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.*;
@RestController
@RequestMapping("/api/demo")
public class DemoController {
    @GetMapping("/orders")
    public Map<String,Object> orders(){
        return Map.of("service","protected-service","timestamp", Instant.now().toString(),"orders", List.of(Map.of("id","ORD-101","amount",1299),Map.of("id","ORD-102","amount",849)));
    }
}
