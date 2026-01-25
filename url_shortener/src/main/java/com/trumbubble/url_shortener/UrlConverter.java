package com.trumbubble.url_shortener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="url_lookup")
public class UrlConverter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "shorturl", nullable = false)
    private String shortURL;

    @Column(name = "longurl", nullable = false, unique = true)
    private String longURL;
}
