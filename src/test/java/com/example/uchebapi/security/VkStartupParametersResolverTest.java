package com.example.uchebapi.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class VkStartupParametersResolverTest {

    private static Map<String, String> params;

    @BeforeAll
    static void setUp() {
        params = new HashMap<>();

        params.put("sign", "3La9t28jtWvKaCBUZ4lrxFbHHAZLZXkArds0x2c7qt8");
        params.put("vk_access_token_settings", "friends,docs");
        params.put("vk_app_id", "51584814");
        params.put("vk_are_notifications_enabled", "0");
        params.put("vk_is_app_user", "1");
        params.put("vk_is_favorite", "1");
        params.put("vk_language", "ru");
        params.put("vk_platform", "mobile_web");
        params.put("vk_ref", "other");
        params.put("vk_testing_group_id", "2");
        params.put("vk_ts", "1691922420");
        params.put("vk_user_id", "218328309");
    }


    @Test
    void testResolver() {
        VkStartupParametersResolver resolver = new VkStartupParametersResolver();
        Assertions.assertEquals(218328309, resolver.getVkId(params)); //они просрочены
    }
}
