package com.trumbubble.url_shortener;

public class UrlConverterService {
    private UrlRepo repository;

    public UrlConverter addUrl(String longUrl)
    {
        UrlConverter url = new UrlConverter();
        url.setLongURL(longUrl);
        url = repository.save(url);

        String shortUrl = Encoder.encode(url.getId());
        url.setShortURL(shortUrl);

        return repository.save(url);
    }

    public String getLongUrl(String shortUrl)
    {
        return repository.findById(Encoder.decode(shortUrl)).map(UrlConverter::getLongURL).orElse(null);
    }
}
