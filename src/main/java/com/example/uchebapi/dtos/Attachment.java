package com.example.uchebapi.dtos;


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

public record Attachment(String title, String url) {

    public Attachment(String title, String url) {
        this.title = title;

        if (!Pattern.matches("https://vk.com/.*", url)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong url");
        }
        this.url = url;
    }
}