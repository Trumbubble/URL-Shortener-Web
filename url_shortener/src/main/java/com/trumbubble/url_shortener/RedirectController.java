package com.trumbubble.url_shortener;

import java.lang.reflect.Method;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Redirect controller:
 * 1) try decode -> id -> findById
 * 2) fallback to findByShortURL(token)
 */
@Controller
public class RedirectController {

    private final Encoder encoder;
    private final UrlRepo repo;

    public RedirectController(Encoder encoder, UrlRepo repo) {
        this.encoder = encoder;
        this.repo = repo;
    }

    @GetMapping("/{shorter:.+}")
    public ResponseEntity<Void> redirect(@PathVariable String shorter) {
        System.out.println("[redirect] requested token='" + shorter + "'");

        // 1) Try decode -> id -> PK lookup
        try {
            Long id = encoder.decode(shorter);
            System.out.println("[redirect] decoder returned id=" + id);
                Optional<?> byId = repo.findById(id);
                // System.out.println("[redirect] repo.findById(" + id + ").isPresent=" + byId.isPresent());
                if (byId.isPresent()) {
                    String longUrl = extractLongUrl(byId.get());
                    // System.out.println("[redirect] (by id) longUrl=" + longUrl);
                if (isValidRedirectTarget(longUrl)) {
                    return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, longUrl).build();
                } else {
                    // System.out.println("[redirect] (by id) invalid longUrl or empty: " + longUrl);
                }
            }
        } catch (Exception e) {
            System.out.println("[redirect] decoder threw: " + e);
            // continue to fallback
        }

        // 2) Fallback: find by the token string directly
        try {
            Optional<?> byToken = repo.findByShortURL(shorter);
            // System.out.println("[redirect] repo.findByShortURL('" + shorter + "').isPresent=" + byToken.isPresent());
            if (byToken.isPresent()) {
                String longUrl = extractLongUrl(byToken.get());
                // System.out.println("[redirect] (by token) longUrl=" + longUrl);
                if (isValidRedirectTarget(longUrl)) {
                    return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, longUrl).build();
                } else {
                    // System.out.println("[redirect] (by token) invalid longUrl or empty: " + longUrl);
                }
            }
        } catch (NoSuchMethodError ex) {
            System.out.println("[redirect] repo.findByShorter method missing on UrlRepo: " + ex);
        }

        System.out.println("[redirect] no match for token='" + shorter + "'");
        return ResponseEntity.notFound().build();
    }

    // Try several common getter names via reflection to extract the long URL string.
    private String extractLongUrl(Object entity) {
        if (entity == null) return null;
        String[] candidates = { "getLongurl", "getLongUrl", "getLongURL", "getLong_url", "getUrl", "getLong" };
        for (String name : candidates) {
            try {
                Method m = entity.getClass().getMethod(name);
                Object val = m.invoke(entity);
                if (val instanceof String) return ((String) val).trim();
            } catch (NoSuchMethodException ignored) {
            } catch (Exception e) {
                System.out.println("[redirect] error calling " + name + ": " + e);
            }
        }
        return null;
    }

    private boolean isValidRedirectTarget(String url) {
        if (url == null) return false;
        String lower = url.toLowerCase();
        return lower.startsWith("http://") || lower.startsWith("https://");
    }
}
