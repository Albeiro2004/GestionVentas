package com.ventas.minipos.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramService {

    private final String BOT_TOKEN = "8492390941:AAH1bGSJIL1nPTRiZjM-HNR-IB_HPJvDGhg";
    private final String CHAT_ID = "1635363596";

    public void sendMessage(String message) {

        String url = "https://api.telegram.org/bot" + BOT_TOKEN +
                "/sendMessage?chat_id=" + CHAT_ID +
                "&text=" + message;

        RestTemplate rest = new RestTemplate();
        rest.getForObject(url, String.class);
    }
}
