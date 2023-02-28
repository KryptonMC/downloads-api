package org.kryptonmc.downloads.controller;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping({ "/downloads", "/downloads/", "/downloads/docs" })
    public ResponseEntity<?> redirectToDocs() {
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/downloads/docs/")).build();
    }
}
