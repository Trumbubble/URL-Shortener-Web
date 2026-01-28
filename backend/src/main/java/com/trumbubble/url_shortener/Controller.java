package com.trumbubble.url_shortener;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/urls")
@CrossOrigin(origins = "http://localhost:3000")
public class Controller {
    
    @Autowired
    private UrlConverterService service;

    @PostMapping(produces = "application/json")
    public ResponseEntity<Map<String,String>> create(@RequestBody Map<String, String> body) {
        String longUrl = body.get("longUrl");
        String shortCode = service.addUrl(longUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("shortUrl", "http://localhost:8080/" + shortCode));
    }

    @GetMapping(produces = "application/json")
    public String getLongUrl(String shorter) {
        return service.getLongUrl(shorter);
    }

}
