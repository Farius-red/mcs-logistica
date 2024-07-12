package com.juliaosystem.api.controller;

import com.common.lib.api.dtos.request.AuditRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.common.lib.api.dtos.request.RegisterUserDTO;
import com.common.lib.api.response.PlantillaResponse;
import com.juliaosystem.infrastructure.services.primary.UserService;
import com.common.lib.utils.errors.AbtractError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ImportAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})

class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private AbtractError abtractError;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAddUser() throws Exception {
        RegisterUserDTO registerUserDTO = new RegisterUserDTO();


        PlantillaResponse<RegisterUserDTO> mockResponse = PlantillaResponse.<RegisterUserDTO>builder()
                .httpStatus(HttpStatus.OK)
                .data(registerUserDTO)
                .build();
        when(userService.add(any(RegisterUserDTO.class), any(AuditRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/user/add")
                        .header("ip", "127.0.0.1")
                        .header("dominio", "example.com")
                        .header("usuario", "testUser")
                        .header("idBussines", "1")
                        .header("proceso", "testProcess")
                        .content(asJsonString(registerUserDTO))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        verify(userService).add(any(RegisterUserDTO.class),any(AuditRequest.class));
    }

    @Test
    void testGetUsers() throws Exception {



        UUID userId = UUID.randomUUID();
        Long businessId = 123L;

        PlantillaResponse<RegisterUserDTO> mockResponse = PlantillaResponse.<RegisterUserDTO>builder()
                .httpStatus(HttpStatus.OK)
                .data(new RegisterUserDTO())
                .build();
        when(userService.getUsers(any(UUID.class), anyLong(), any(AuditRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(get("/user/all")
                        .param("id", userId.toString())
                        .header("ip", "127.0.0.1")
                        .header("dominio", "example.com")
                        .header("usuario", "testUser")
                        .header("idBussines", businessId.toString())
                        .header("proceso", "testProcess")
                )

                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andReturn();

        verify(userService).getUsers(any(UUID.class), anyLong(), any(AuditRequest.class));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
