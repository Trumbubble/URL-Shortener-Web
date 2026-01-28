package com.trumbubble.url_shortener;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class UrlConverterService {
    private final UrlRepo repository;

    public UrlConverterService(UrlRepo repository) {
        this.repository = repository;
    }

    @Transactional
    public String addUrl(String longUrl)
    {
        UrlConverter url = new UrlConverter();
        url.setLongURL(longUrl);
        String shortUrl = " ";
        url.setShortURL(shortUrl);
        url = repository.save(url);

        shortUrl = Encoder.encode(url.getId());
        url.setShortURL(shortUrl);

        repository.save(url);

        return shortUrl;
    }

    public String getLongUrl(String shortUrl)
    {
        return repository.findById(Encoder.decode(shortUrl)).map(UrlConverter::getLongURL).orElse(null);
    }
}
