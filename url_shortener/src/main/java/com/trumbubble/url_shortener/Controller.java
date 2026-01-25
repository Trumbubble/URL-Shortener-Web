package com.trumbubble.url_shortener;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
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

    @PostMapping(consumes = "application/json")
    public String create(@RequestBody Map<String, String> body) {
        String longUrl = body.get("longUrl");
        return service.addUrl(longUrl);
    }
}
