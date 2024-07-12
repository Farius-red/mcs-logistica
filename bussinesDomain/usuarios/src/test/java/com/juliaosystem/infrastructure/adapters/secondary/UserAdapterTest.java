package com.juliaosystem.infrastructure.adapters.secondary;


import com.common.lib.api.dtos.request.RegisterUserDTO;
import com.common.lib.api.dtos.user.DatesUserDTO;
import com.common.lib.api.dtos.user.KeycloakBuilderDTO;
import com.juliaosystem.api.mappers.UserMapper;
import com.common.lib.api.response.PlantillaResponse;
import com.juliaosystem.infrastructure.entitis.DatesUser;
import com.juliaosystem.infrastructure.entitis.User;
import com.juliaosystem.infrastructure.entitis.UserRol;
import com.juliaosystem.infrastructure.repository.UserRepository;
import com.juliaosystem.infrastructure.services.secondary.UserServiceInter;
import com.common.lib.utils.UserResponses;
import com.common.lib.utils.enums.MensajesRespuesta;
import com.common.lib.utils.enums.ResponseType;
import com.common.lib.utils.errors.AbtractError;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAdapterTest {

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;



    @Mock
    Response response ;
    @Mock
    private UserServiceInter userServiceInter;

    @Mock
    private UserResponses<RegisterUserDTO> userResponses;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AbtractError abtractError;

    @Mock
    private UserRepository userRepository;

    @Mock KeycloakAdapter keycloak;

    @InjectMocks
    private UserAdapter userAdapter;



    @Test
    void testFindByEmail_ValidEmail_UserFound() {
        String email = "test@example.com";

        User user = new User();
        user.setEmail(email);
        user.setUserRols(Collections.singletonList(new UserRol()));

        RegisterUserDTO registerUserDTO =
                RegisterUserDTO.builder()
                        .datesUser(DatesUserDTO.builder()
                                .firstName("daniel")
                                .build())
                        .build();

        PlantillaResponse<RegisterUserDTO> expectedResponse =
                PlantillaResponse.<RegisterUserDTO>builder()
                        .data(registerUserDTO)
                        .message(ResponseType.USER_ISFOUND.getMessage())
                        .rta(true)
                        .build();

        when(userResponses.buildResponse(anyInt(), any())).thenReturn(PlantillaResponse.<RegisterUserDTO>builder()
                .data(registerUserDTO)
                .message(expectedResponse.getMessage())
                .build());



        PlantillaResponse<RegisterUserDTO> response = userAdapter.findByEmail(email);


        assertEquals(ResponseType.USER_ISFOUND.getMessage(), response.getMessage());
        assertEquals(registerUserDTO, response.getData());
    }

    @Test
    void testFindByEmail_ValidEmail_UserNotFound() {

        RegisterUserDTO registerUserDTO =
                RegisterUserDTO.builder()
                        .datesUser(DatesUserDTO.builder()
                                .firstName("daniel")
                                .build())
                        .build();

        PlantillaResponse<RegisterUserDTO> expectedResponse =
                PlantillaResponse.<RegisterUserDTO>builder()
                        .data(registerUserDTO)
                        .message(ResponseType.NO_ENCONTRADO.getMessage())
                        .rta(true)
                        .build();

        when(userResponses.buildResponse(anyInt(), any())).thenReturn(PlantillaResponse.<RegisterUserDTO>builder()
                .data(registerUserDTO)
                .message(expectedResponse.getMessage())
                .build());

        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);


        PlantillaResponse<RegisterUserDTO> response = userAdapter.findByEmail(email);


        assertEquals(ResponseType.NO_ENCONTRADO.getMessage(), response.getMessage());
    }

    @Test
    void testFindByEmail_InvalidEmail() {
        long idBusiness = 123456;
        RegisterUserDTO registerUserDTO =
                RegisterUserDTO.builder()
                        .datesUser(DatesUserDTO.builder()
                                .firstName("daniel")
                                .build())
                        .build();

        PlantillaResponse<RegisterUserDTO> expectedResponse =
                PlantillaResponse.<RegisterUserDTO>builder()
                        .data(registerUserDTO)
                        .message(ResponseType.EMAIL_VALIDATION_FAIL.getMessage())
                        .rta(true)
                        .build();

        when(userResponses.buildResponse(anyInt(), any())).thenReturn(PlantillaResponse.<RegisterUserDTO>builder()
                .data(registerUserDTO)
                .message(expectedResponse.getMessage())
                .build());

        String invalidEmail = "invalidemail";


        PlantillaResponse<RegisterUserDTO> response = userAdapter.findByEmail(invalidEmail);


        assertEquals(ResponseType.EMAIL_VALIDATION_FAIL.getMessage(), response.getMessage());
    }

    @Test
    void testLlenarEntidades_UserWithEmailAndEmptyResponse_Success() {

        long idBusiness = 123456;
        RegisterUserDTO registerUserDTO =
                RegisterUserDTO.builder()
                        .datesUser(DatesUserDTO.builder()
                                .firstName("daniel")
                                .build())
                        .build();

        PlantillaResponse<RegisterUserDTO> expectedResponse =
                PlantillaResponse.<RegisterUserDTO>builder()
                        .data(registerUserDTO)
                        .message(ResponseType.CREATED.getMessage())
                        .rta(true)
                        .build();

        when(userResponses.buildResponse(anyInt(), any())).thenReturn(PlantillaResponse.<RegisterUserDTO>builder()
                .data(registerUserDTO)
                .message(expectedResponse.getMessage())
                .build());
        User userSave = new User();
        userSave.setEmail("test@example.com");
        PlantillaResponse<RegisterUserDTO> register = new PlantillaResponse<>();
        register.setRta(false);
        register.setMessage(MensajesRespuesta.NO_ENCONTRADO.getMensaje());

        PlantillaResponse<RegisterUserDTO> response = userAdapter.llenarEntidades(userSave, register.getData());


        assertEquals(ResponseType.CREATED.getMessage(), response.getMessage());
    }

    @Test
    void testLlenarEntidades_UserWithEmailAndFilledResponse_Success() {


        RegisterUserDTO registerUserDTO =
                RegisterUserDTO.builder()
                        .datesUser(DatesUserDTO.builder()
                                .firstName("daniel")
                                .build())
                        .build();

        PlantillaResponse<RegisterUserDTO> expectedResponse =
                PlantillaResponse.<RegisterUserDTO>builder()
                        .data(registerUserDTO)
                        .message(ResponseType.CREATED.getMessage())
                        .rta(true)
                        .build();

        when(userResponses.buildResponse(anyInt(), any())).thenReturn(PlantillaResponse.<RegisterUserDTO>builder()
                .data(registerUserDTO)
                .message(expectedResponse.getMessage())
                .build());

        User userSave = new User();
        userSave.setEmail("test@example.com");
        userSave.setUserRols(Collections.singletonList(new UserRol()));
        PlantillaResponse<RegisterUserDTO> register = new PlantillaResponse<>();
        register.setRta(true);
        register.setMessage(MensajesRespuesta.USER_ISFOUND.getMensaje());


        PlantillaResponse<RegisterUserDTO> response = userAdapter.llenarEntidades(userSave, register.getData());


        assertEquals(ResponseType.CREATED.getMessage(), response.getMessage());
    }

    @Test
    void testGetUsers_EmptyList_Success() {

        long idBusiness = 123456;
        RegisterUserDTO registerUserDTO =
                RegisterUserDTO.builder()
                        .datesUser(DatesUserDTO.builder()
                                .firstName("daniel")
                                .build())
                        .build();

        PlantillaResponse<RegisterUserDTO> expectedResponse =
                PlantillaResponse.<RegisterUserDTO>builder()
                        .data(registerUserDTO)
                        .message(ResponseType.NO_ENCONTRADO.getMessage())
                        .rta(true)
                        .build();

        when(userResponses.buildResponse(anyInt(), any())).thenReturn(PlantillaResponse.<RegisterUserDTO>builder()
                .data(registerUserDTO)
                .message(expectedResponse.getMessage())
                .build());
        when(userRepository.findByIdBussines(idBusiness)).thenReturn(Collections.emptyList());


        PlantillaResponse<RegisterUserDTO> response = userAdapter.byIdBussines(idBusiness);

        assertEquals(ResponseType.NO_ENCONTRADO.getMessage(), response.getMessage());
    }

    @Test
    void testGetUsers_NonEmptyList_Success() {

        long idBusiness = 123456;
        RegisterUserDTO registerUserDTO =
                RegisterUserDTO.builder()
                        .datesUser(DatesUserDTO.builder()
                                .firstName("daniel")
                                .build())
                        .build();

        PlantillaResponse<RegisterUserDTO> expectedResponse =
                PlantillaResponse.<RegisterUserDTO>builder()
                        .data(registerUserDTO)
                        .message(ResponseType.GET.getMessage())
                        .rta(true)
                        .build();


        var user = User.builder().datesUser(DatesUser.builder()
                        .idDatesUser(UUID.randomUUID())
                        .firstName(registerUserDTO.getDatesUser().getFirstName())
                        .build())
                .build();
        when(userResponses.buildResponse(anyInt(), any())).thenReturn(PlantillaResponse.<RegisterUserDTO>builder()
                .data(registerUserDTO)
                .message(expectedResponse.getMessage())
                .build());

        when(userRepository.findByIdBussines(idBusiness)).thenReturn(Collections.singletonList(user));


        when(userMapper.getListDTO(Collections.singletonList(user))).thenReturn(Collections.singletonList(registerUserDTO));
        PlantillaResponse<RegisterUserDTO> response = userAdapter.byIdBussines(idBusiness);


        assertEquals(ResponseType.GET.getMessage(), response.getMessage());
    }



    @Test
    void testGetUserById_UserNotFound() {

        UUID id = UUID.randomUUID();
        RegisterUserDTO registerUserDTO =
                RegisterUserDTO.builder()
                        .datesUser(DatesUserDTO.builder()
                                .firstName("daniel")
                                .build())
                        .build();



        when(userResponses.buildResponse(anyInt(), any())).thenReturn(PlantillaResponse.<RegisterUserDTO>builder()
                .data(registerUserDTO)
                .message(ResponseType.NO_ENCONTRADO.getMessage())
                .build());

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());


        PlantillaResponse<RegisterUserDTO> response = userAdapter.byId(id);


        assertEquals(ResponseType.NO_ENCONTRADO.getMessage(), response.getMessage());

    }

    @Test
    void testGetUserById_UserFound() {

        UUID id = UUID.randomUUID();
        RegisterUserDTO registerUserDTO =
                RegisterUserDTO.builder()
                        .datesUser(DatesUserDTO.builder()
                                .firstName("daniel")
                                .build())
                        .build();

        when(userResponses.buildResponse(anyInt(), any())).thenReturn(PlantillaResponse.<RegisterUserDTO>builder()
                .data(registerUserDTO)
                .message(ResponseType.GET.getMessage())
                .build());




        PlantillaResponse<RegisterUserDTO> response = userAdapter.byId(id);


        assertEquals(ResponseType.GET.getMessage(), response.getMessage());

    }

    @Test
    void testGetUserById_ExceptionThrown() {

        UUID id = UUID.randomUUID();
        RegisterUserDTO registerUserDTO =
                RegisterUserDTO.builder()
                        .datesUser(DatesUserDTO.builder()
                                .firstName("daniel")
                                .build())
                        .build();

        when(userResponses.buildResponse(anyInt(), any())).thenReturn(PlantillaResponse.<RegisterUserDTO>builder()
                .data(registerUserDTO)
                .message(ResponseType.FALLO.getMessage())
                .build());


        PlantillaResponse<RegisterUserDTO> response = userAdapter.byId(id);


        assertEquals(ResponseType.FALLO.getMessage(), response.getMessage());

    }

    @Test
    void testAddWithKeyCloack_without_password() {


        RegisterUserDTO registerUserDTO =
                RegisterUserDTO.builder()
                        .email("daniel.juliao.tecni@gmail.com")
                        .keyCloak(KeycloakBuilderDTO.builder()
                                .clientId("prueba")
                                .realm("prueba")
                                .build())
                        .datesUser(DatesUserDTO.builder()
                                .firstName("daniel")
                                .build())
                        .build();




        when(userResponses.buildResponse(anyInt(), any())).thenReturn(PlantillaResponse.<RegisterUserDTO>builder()
                .data(registerUserDTO)
                .message(ResponseType.CREATED.getMessage())
                .build());


        PlantillaResponse<RegisterUserDTO> response = userAdapter.add(registerUserDTO);


        assertEquals(ResponseType.CREATED.getMessage(), response.getMessage());
    }

    @Test
    void testAddWithKeyCloack_without_Email() {

        RegisterUserDTO registerUserDTO =
                RegisterUserDTO.builder()
                        .keyCloak(KeycloakBuilderDTO.builder()
                                .clientId("prueba")
                                .realm("prueba")
                                .build())
                        .datesUser(DatesUserDTO.builder()
                                .firstName("daniel")
                                .build())
                        .build();

        when(userResponses.buildResponse(anyInt(), any())).thenReturn(PlantillaResponse.<RegisterUserDTO>builder()
                .data(registerUserDTO)
                .message(ResponseType.EMAIL_VALIDATION_FAIL.getMessage())
                .build());


        PlantillaResponse<RegisterUserDTO> response = userAdapter.add(registerUserDTO);


        assertEquals(ResponseType.EMAIL_VALIDATION_FAIL.getMessage(), response.getMessage());
    }
    @Test
    void testAddWithKeyCloack_with_Email_and_Password() {

        RegisterUserDTO registerUserDTO =
                RegisterUserDTO.builder()
                        .email("daniel.juliao.tecni@gmail.com")
                        .password("0F145678b0@123")
                        .keyCloak(KeycloakBuilderDTO.builder()
                                .clientId("prueba")
                                .realm("prueba")
                                .build())
                        .datesUser(DatesUserDTO.builder()
                                .firstName("daniel")
                                .build())
                        .build();

        when(keycloak.realmResource(any(KeycloakBuilderDTO.class))).thenReturn(realmResource);


        when(realmResource.users()).thenReturn(usersResource);

        Response mockResponse = Response.status(Response.Status.CREATED).build();

        when(usersResource.create(any())).thenReturn(mockResponse);


        when(userResponses.buildResponse(anyInt(), any())).thenReturn(PlantillaResponse.<RegisterUserDTO>builder()
                .data(registerUserDTO)
                .message(ResponseType.CREATED.getMessage())
                .build());


        PlantillaResponse<RegisterUserDTO> response = userAdapter.add(registerUserDTO);


        assertEquals(ResponseType.CREATED.getMessage(), response.getMessage());
    }
}