package com.example.uchebapi.controllers;

import com.example.uchebapi.domain.VkUser;
import com.example.uchebapi.repos.VkUserRepo;
import com.example.uchebapi.security.JwtService;
import com.example.uchebapi.security.VkStartupParametersResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
class AuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VkStartupParametersResolver vkStartupParametersResolver;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private VkUserRepo vkUserRepo;

    private String jwt;
    private Integer testVkId = 1488;


    @BeforeEach
    public void setup() {
        //VkStartupParametersResolver spoof
        when(vkStartupParametersResolver.getVkId(anyMap())).thenReturn(testVkId);

        //preparing jwt
        jwt = jwtService.generateToken(testVkId.toString());

        //preparing user for test
        if (vkUserRepo.findByVkId(testVkId).isEmpty())
            vkUserRepo.save(
                    VkUser.builder()
                            .vkId(testVkId)
                            .build()
            );
    }

    @Test
    public void testAuthorization() throws Exception {
        mockMvc.perform(get("/api/user/authenticate"))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", "access_token=" + jwt))
                .andExpect(jsonPath("$.vkId", is(testVkId)))
                .andExpect(jsonPath("$.accessToken", is(jwt)));
    }

    @Test
    public void testFirstTimeAuthorization() throws Exception {
        if (vkUserRepo.findByVkId(testVkId).isPresent())
            vkUserRepo.delete(VkUser.builder()
                    .vkId(testVkId)
                    .build()
            );

        mockMvc.perform(get("/api/user/authenticate"))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", "access_token=" + jwt))
                .andExpect(jsonPath("$.vkId", is(testVkId)))
                .andExpect(jsonPath("$.accessToken", is(jwt)));
    }

    @Test
    public void testAccessingAuthorizedEndpoint() throws Exception {
        String testString = "auth_test";
        mockMvc.perform(get("/api/helloAuth")
                        .header("Authorization", "Bearer " + jwt)
                        .param("name", testString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Hello, " + testString)));
    }

    @Test
    public void testAccessingAuthorizedEndpoint_WithWrongToken() throws Exception {
        String testString = "no_auth_test";
        mockMvc.perform(get("/api/helloAuth")
                        .param("name", testString)
                        .header("Authorization", "Bearer " + "counterfeit_token"))
                .andExpect(status().isUnauthorized());
    }

    /* for further tests
       mockMvc.perform(post("/api/group/create")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "testName"
                                }
                                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("testName")))
            .andExpect(jsonPath("$.ownerId", is(testVkId)));
    */
}
