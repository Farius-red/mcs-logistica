package com.juliaosystem.infrastructure.adapters.primary;

import com.common.lib.api.dtos.request.RegisterUserDTO;
import com.common.lib.api.response.PlantillaResponse;
import com.juliaosystem.infrastructure.services.secondary.UserServiceInter;
import com.common.lib.utils.UserResponses;
import com.common.lib.utils.enums.ResponseType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserImplTest {

    @Mock
    private UserServiceInter userServiceInter;

    @Mock
    private UserResponses<RegisterUserDTO> userResponses;

    @InjectMocks
    private UserImpl userImpl;
    @Test
    void testGetUsers_ByIdIsNull() {

        UUID id = null;
        long idBusiness = 123;
        PlantillaResponse<RegisterUserDTO> expectedResponse = new PlantillaResponse<>();
        when(userServiceInter.byIdBussines(idBusiness)).thenReturn(expectedResponse);


        PlantillaResponse<RegisterUserDTO> response = userImpl.all(id, idBusiness);

        assertEquals(expectedResponse, response);
        verify(userServiceInter, times(1)).byIdBussines(idBusiness);
        verify(userServiceInter, never()).byId(any(UUID.class));
    }

    @Test
    void testGetUsers_ByIdIsNotNull() {
        UUID id = UUID.randomUUID();
        long idBusiness = 123;
        PlantillaResponse<RegisterUserDTO> expectedResponse = PlantillaResponse.<RegisterUserDTO>builder()
                .message(ResponseType.GET.getMessage())
                .build();

        when(userServiceInter.byId(id)).thenReturn(expectedResponse);

        var  response = userImpl.all(id, idBusiness);

        assertEquals(expectedResponse, response);
        verify(userServiceInter, times(1)).byId(id);
        verify(userServiceInter, never()).byIdBussines(anyLong());
    }

}