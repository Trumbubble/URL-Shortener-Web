package com.trumbubble.url_shortener;

import org.springframework.data.jpa.repository.JpaRepository;

//I will decide if I need the Long ID, but if I don't I can switch to String here
public interface UrlRepo extends JpaRepository<UrlConverter,Long>{

}
