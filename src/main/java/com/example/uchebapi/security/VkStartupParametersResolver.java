package com.example.uchebapi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VkStartupParametersResolver {

    private Map<String, String> startupParameters;
    private static final int EXPIRATION_TIME = 1000 * 60 * 60; // 60 минут по документации
    private static final String ENCODING = "UTF-8";
    private static final String clientSecret = "DELETED";

    /**
     * Checks startup parameters
     *
     * @return vkId of a user or {@code -1} if parameters were incorrect/expired
     */
    public Integer getVkId(Map<String, String> startupParameters) {
        this.startupParameters = startupParameters;

        if (!isStartupParametersCorrect() || !isStartupParametersNotExpired())
            return -1;

        String vkId = startupParameters.get("vk_user_id");
        try {
            return Integer.parseInt(vkId);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private boolean isStartupParametersNotExpired() {
        try {
            String generationDateParameter = startupParameters.get("vk_ts");
            Date experationDate = new Date(
                    Long.parseLong(generationDateParameter) * 1000L
                            + EXPIRATION_TIME
            );
            return experationDate.after(new Date());

        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private boolean isStartupParametersCorrect() {
        try {
            String checkString = startupParameters.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith("vk_"))
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                    .collect(Collectors.joining("&"));

            String sign = getHashCode(checkString, clientSecret);
            return sign.equals(startupParameters.getOrDefault("sign", ""));
        } catch (Exception ex) {
            return false;
        }
    }

    private String getHashCode(String data, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(ENCODING), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);
        byte[] hmacData = mac.doFinal(data.getBytes(ENCODING));
        return new String(Base64.getUrlEncoder().withoutPadding().encode(hmacData));
    }

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return value;
    }
}