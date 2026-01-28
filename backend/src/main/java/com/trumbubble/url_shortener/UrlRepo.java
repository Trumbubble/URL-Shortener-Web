package com.trumbubble.url_shortener;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//I will decide if I need the Long ID, but if I don't I can switch to String here
@Repository
public interface UrlRepo extends JpaRepository<UrlConverter,Long>{
    Optional<UrlConverter> findByShortURL(String shortURL);
    Optional<UrlConverter> findByLongURL(String longURL);
}
