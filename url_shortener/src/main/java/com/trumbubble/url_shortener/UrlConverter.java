package com.trumbubble.url_shortener;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="url_lookup")
public class UrlConverter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String longURL;
    private String shortURL;
}
