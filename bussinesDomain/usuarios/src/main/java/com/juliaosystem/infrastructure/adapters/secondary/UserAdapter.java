package com.juliaosystem.infrastructure.adapters.secondary;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.UrlJwkProvider;
import com.common.lib.infraestructure.adapters.primary.AuditImpl;
import com.common.lib.utils.enums.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.common.lib.api.dtos.request.RegisterUserDTO;
import com.common.lib.api.dtos.user.KeycloakBuilderDTO;
import com.juliaosystem.api.mappers.AddressMapper;
import com.juliaosystem.api.mappers.DatesUserMapper;
import com.juliaosystem.api.mappers.UserMapper;
import com.juliaosystem.infrastructure.entitis.Permiso;
import com.juliaosystem.infrastructure.entitis.Roles;
import com.juliaosystem.infrastructure.entitis.User;
import com.juliaosystem.infrastructure.entitis.UserRol;
import com.juliaosystem.infrastructure.entitis.pk.UserRolPk;
import com.juliaosystem.infrastructure.repository.AddressRepository;
import com.juliaosystem.infrastructure.repository.DatesUserRepository;
import com.juliaosystem.infrastructure.repository.UserRepository;
import com.juliaosystem.infrastructure.repository.UserRolRepository;
import com.juliaosystem.infrastructure.services.secondary.UserServiceInter;
import com.common.lib.utils.UserResponses;
import jakarta.ws.rs.core.Response;
import com.common.lib.utils.errors.AbtractError;

import com.common.lib.api.response.PlantillaResponse;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URL;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserAdapter   implements UserServiceInter {

    private static final Logger logger = LoggerFactory.getLogger(UserAdapter.class);
    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().registerModule(new JavaTimeModule());
    private final UserResponses<RegisterUserDTO> userResponses;
    private final  UserResponses<User> userUserResponsesUser;


    @Value("${keycloak.jwk-set-uri}")
    private String jwksUrl;

    @Value("${keycloak.certs-id}")
    private String certsId;
    private final AbtractError abtractError;

    private final UserMapper userMapper;
    private final KeycloakAdapter keycloak;
    private final PermisosAdapter permisosAdapter;
    private final RolAdapter rolAdapter;
    private  final UserRolRepository userRolRepository;
    private final UserRepository userRepository;
    private final DatesUserRepository datesUserRepository;
    private  final AddressRepository addressRepository;
    private  final AddressMapper addressMapper;
    private  final DatesUserMapper datesUserMapper;


    @Override
    public PlantillaResponse<RegisterUserDTO> findByEmail(String email) {
          User user;
        if (!validarEmail(email))
            return userResponses.buildResponse(ResponseType.EMAIL_VALIDATION_FAIL.getCode(), RegisterUserDTO.builder().build());
         else
            user = userRepository.findByEmail(email);

        if (user != null) {
            RegisterUserDTO userDTO = userMapper.getDTO(user);
            if(user.getDatesUser() != null) {
                userDTO.getDatesUser().setNombreRol(user.getUserRols().getFirst().getRol().getNameRol().name());
            }
            return userResponses.buildResponse(ResponseType.USER_ISFOUND.getCode(), userDTO);
        } else
            return userResponses.buildResponse(ResponseType.NO_ENCONTRADO.getCode(), RegisterUserDTO.builder().build());
    }




    private PlantillaResponse<RegisterUserDTO> buildRegisterUserResponse(User user) {
        RegisterUserDTO userDTO = userMapper.getDTO(user);
        return userResponses.buildResponse(ResponseType.CREATED.getCode(), userDTO );
    }




    private boolean validarEmail(String email){
        if(email == null)
            return false;
      return email.matches(EmailValidationPattern.VALID.getPattern());
    }

    public boolean validarPassword(RegisterUserDTO registerUserDTO){
        if(registerUserDTO.getPassword()== null)
            return false;
        return registerUserDTO.getPassword().matches(PasswordValidationPattern.VALID.getPattern());
    }

    private  Set<Permiso> asignarPermisoNuevoUsuario(){
    Set<Permiso> permisosCliente = new HashSet<>();
    permisosCliente.add(permisosAdapter.findByNombrePermiso(PermisoEnum.NUEVO_USUARIO));
    return  permisosCliente;
    }

    private User asignarRolYPermisosNuevoUsuario(User user){
    Roles rol = rolAdapter.findByNombreRol(RolEnum.USUARIO);
    rol.setPermisos(asignarPermisoNuevoUsuario());
    user.setUserRols(Collections.singletonList(UserRol.builder().rol(rol).build()));
    return user;
    }

    @Override
    public PlantillaResponse<RegisterUserDTO> llenarEntidades(User userSave , RegisterUserDTO registerUserDTO){
    try {
        if (!userSave.getEmail().isEmpty()) {
            registerUserDTO.setId(userSave.getId_usuario());
            registerUserDTO.getDatesUser().setIdDatesUser(UUID.randomUUID());
            registerUserDTO.getDatesUser().setIdUrl(DatesUserMapper.generarCadenaAleatoria());

            datesUserRepository.saveDatesUser(registerUserDTO);
            abtractError.logInfo("llenarEntidades().SaveDatesUser():" + MensajesRespuesta.CREADO +" los datos de usuario  " +  OBJECT_MAPPER.writeValueAsString(registerUserDTO.getDatesUser()));
            fillPhone(registerUserDTO);
           fillAddress(registerUserDTO);
            userRolRepository.saveAll(guardarUserRol(userSave));

            abtractError.logInfo("llenarEntidades() :" + MensajesRespuesta.CREADO +" los datos de usuario rol   " +  OBJECT_MAPPER.writeValueAsString(userSave.getUserRols()));
            return buildRegisterUserResponse(userSave);
        }
    }catch (Exception e){
        return validarErrorResponsellenarEntidades(e,registerUserDTO);
    }
     return  userResponses.buildResponse(ResponseType.FALLO.getCode(),RegisterUserDTO.builder()
             .build());
   }

    @Override
    public PlantillaResponse<RegisterUserDTO> byId(UUID id) {
        try {
            var listUsers = userRepository.findById(id);
            if(listUsers.isEmpty()){
                abtractError.logInfo("getUserById.getUsers():" + MensajesRespuesta.NO_ENCONTRADO +"  de usuario para el   id  " +  OBJECT_MAPPER.writeValueAsString(id));
                return userResponses.buildResponse(ResponseType.NO_ENCONTRADO.getCode(), RegisterUserDTO.builder().build());
            }else {
                var listRegisterDTO = userMapper.getListDTO(listUsers.stream().toList());
                abtractError.logInfo("getUserById.getUsers():" + MensajesRespuesta.GET +" de usuario  " +     OBJECT_MAPPER.writeValueAsString(listUsers));
                return userResponses.buildResponse(ResponseType.GET.getCode(), RegisterUserDTO.builder().build(),listRegisterDTO);
            }
        }catch (Exception e){
            abtractError.logError(e);
            return   userResponses.buildResponse(ResponseType.FALLO.getCode(), RegisterUserDTO.builder().build());
        }
    }

    @Override
    public PlantillaResponse<RegisterUserDTO> byIdBussines(Long idBusiness) {
        try {
            var listUsers = userMapper.getListDTO(userRepository.findByIdBussines(idBusiness));
            if(listUsers.isEmpty()){
                abtractError.logInfo("UserAdapter.getUsers():" + MensajesRespuesta.NO_ENCONTRADO +"  de usuario para el negocio con id  " +  OBJECT_MAPPER.writeValueAsString(idBusiness));
                return userResponses.buildResponse(ResponseType.NO_ENCONTRADO.getCode(), RegisterUserDTO.builder().build());
            }else {
                abtractError.logInfo("UserAdapter.getUsers():" + MensajesRespuesta.GET +" los datos de usuario para el negocio " +     OBJECT_MAPPER.writeValueAsString(listUsers));
                return userResponses.buildResponse(ResponseType.GET.getCode(), RegisterUserDTO.builder().build(),listUsers);
            }
        }catch (Exception e){
            abtractError.logError(e);
            return   userResponses.buildResponse(ResponseType.FALLO.getCode(), RegisterUserDTO.builder().build());
        }
    }



    private void fillPhone(RegisterUserDTO registerUserDTO){
        try {
            if (registerUserDTO.getDatesUser().getPhone() != null && !registerUserDTO.getDatesUser().getPhone().isEmpty() ) {
                registerUserDTO.getDatesUser().getPhone().forEach(phoneDTO -> datesUserRepository.savePhone(phoneDTO, registerUserDTO.getDatesUser().getIdDatesUser()));
                abtractError.logInfo("llenarEntidades().phone :" + MensajesRespuesta.CREADO + " los telefonos del usuario  " + OBJECT_MAPPER.writeValueAsString(registerUserDTO.getDatesUser().getPhone()));
            }
        }catch (Exception e){
            abtractError.logError(e);
        }
   }

    private  void fillAddress(RegisterUserDTO registerUserDTO){
       try {
           if (registerUserDTO.getDatesUser().getAddresses() != null && !registerUserDTO.getDatesUser().getPhone().isEmpty()) {
               var listAddress =addressMapper.mapToEntityList(registerUserDTO.getDatesUser().getAddresses());
               listAddress.forEach(item -> {
                   if(  item.getDatesUser() == null ){
                       item.setDatesUser(datesUserMapper.getEntyti(registerUserDTO.getDatesUser()));
                   }
                   addressRepository.save(item);

               });
               abtractError.logInfo("llenarEntidades().address :" + MensajesRespuesta.CREADO + " las direcciones  del usuario  " + OBJECT_MAPPER.writeValueAsString(registerUserDTO.getDatesUser().getPhone()));

           }
       }catch (Exception e){
           abtractError.logError(e);
       }
   }


   private   PlantillaResponse<RegisterUserDTO> validarErrorResponsellenarEntidades(Exception e , RegisterUserDTO registerUserDTO){
       abtractError.logError(e);
        if (e.getMessage().contains("fkqyhoaebvd8qqsobu7jddhm7f3"))
           return  userResponses.buildResponse(ResponseType.FALLO_CREATE_PHONE.getCode(),registerUserDTO);
        if(e.getMessage().contains("fkc6t6ir1ytmvrpjf1me1c1tr6j"))
            return  userResponses.buildResponse(ResponseType.FALLO_CREATE_DATOS_USER.getCode(),registerUserDTO);

       return  userResponses.buildResponse(ResponseType.FALLO.getCode(),registerUserDTO);
    }

   @Override
   @Transactional(propagation = Propagation.REQUIRED)
    public PlantillaResponse<User> addUser(RegisterUserDTO registerUserDTO, PlantillaResponse<RegisterUserDTO> register){
        PlantillaResponse<User> response = new PlantillaResponse<>();
        try {
            if (!register.isRta() && register.getMessage().equalsIgnoreCase(MensajesRespuesta.NO_ENCONTRADO.getMensaje())) {
                User user = asignarRolYPermisosNuevoUsuario(userMapper.getEntyti(registerUserDTO));
                user.setBalance(0.0);
                User userSave = userRepository.save(user);
                response = userUserResponsesUser.buildResponse(ResponseType.CREATED.getCode(), userSave);
            }
            if (register.getMessage().equalsIgnoreCase(MensajesRespuesta.USER_ISFOUND.getMensaje()))
                return userUserResponsesUser.buildResponse(ResponseType.USER_ISFOUND.getCode(), User.builder().build());
        }catch (Exception e){
            logger.error("Algo salio mal en metodo UserAdapter.verificarReponseAdd()" , e);
            response=  userUserResponsesUser.buildResponse(ResponseType.FALLO.getCode(), User.builder().build());
        }

        return response;
    }


    private List<UserRol>   guardarUserRol(User userSave){
        List<UserRol> userRols = userSave.getUserRols();
        userRols.forEach(userRol -> {
            userRol.setUserRolPk(UserRolPk.builder()
                    .user(userSave.getId_usuario())
                    .rol(userRol.getRol().getIdRol())
                    .build());
            userRol.setRol(Roles.builder()
                            .idRol(userRol.getUserRolPk().getRol())
                    .build());
        });
        return userRols;
    }

    @Override
    @Cacheable(value = "jwkCache")
    public Jwk getJwk() throws Exception {
        if (jwksUrl == null) {
            throw new IllegalArgumentException("jwksUrl is null");
        }
        URI uri = new URI(jwksUrl);
        URL url = uri.toURL();
        UrlJwkProvider urlJwkProvider = new UrlJwkProvider(url);
        try {
            return urlJwkProvider.get(certsId.trim());
        } catch (Exception e) {
           abtractError.logError(e);
            throw e;
        }
    }

    private   PlantillaResponse<RegisterUserDTO> addUserKeycloack( RegisterUserDTO registerUserDTO) {
        try {
            RealmResource realmResource =  keycloak.realmResource(KeycloakBuilderDTO.builder().realm(registerUserDTO.getKeyCloak().getRealm()).clientId(registerUserDTO.getKeyCloak().getClientId()).build());

            UsersResource usersResource = realmResource.users();
            UserRepresentation userRepresentation = new UserRepresentation();

            userRepresentation.setUsername(registerUserDTO.getEmail());
            userRepresentation.setEmail(registerUserDTO.getEmail());
            userRepresentation.setFirstName(registerUserDTO.getDatesUser().getFirstName());
            userRepresentation.setLastName(registerUserDTO.getDatesUser().getSecondName());
            userRepresentation.setEmailVerified(false);
            userRepresentation.setEnabled(true);
            cretePassword(registerUserDTO,userRepresentation);

            return createUserKeycloak(userRepresentation,usersResource,registerUserDTO);
        } catch (Exception e) {
            abtractError.logError(e);
            return userResponses.buildResponse(ResponseType.FALLO.getCode(),registerUserDTO);
        }
    }

    private  PlantillaResponse<RegisterUserDTO>   createUserKeycloak(UserRepresentation userRepresentation , UsersResource usersResource , RegisterUserDTO registerUserDTO){
        PlantillaResponse<RegisterUserDTO> res;
        try (var response = usersResource.create(userRepresentation)) {
            if (response.getStatus() == Response.Status.CREATED.getStatusCode())
                res = userResponses.buildResponse(ResponseType.CREATED.getCode(), registerUserDTO);

            else if (response.getStatus() == 409)
                res = userResponses.buildResponse(ResponseType.USER_ISFOUND.getCode(), registerUserDTO);
            else res = userResponses.buildResponse(ResponseType.FALLO.getCode(), registerUserDTO);
        }catch (Exception e){
            res = userResponses.buildResponse(ResponseType.FALLO.getCode(), registerUserDTO);
            res.setMessage(res.getMessage() + e.getMessage());
            abtractError.logError(e);
        }
        return res;
    }

    private void cretePassword(RegisterUserDTO registerUserDTO ,  UserRepresentation userRepresentation){
        if(validarPassword(registerUserDTO)) {
            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
            credentialRepresentation.setValue(registerUserDTO.getPassword());
            credentialRepresentation.setTemporary(false);
            userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        }
    }



    @Override
    public PlantillaResponse<RegisterUserDTO> add(RegisterUserDTO registerUserDTO) {
        if(registerUserDTO != null) {
            if (validarEmail(registerUserDTO.getEmail())) {
                return addUserKeycloack(registerUserDTO);
            } else {
                return userResponses.buildResponse(ResponseType.EMAIL_VALIDATION_FAIL.getCode(), registerUserDTO);
            }
        } else
            return userResponses.buildResponse(ResponseType.FALLO.getCode(), RegisterUserDTO.builder().build());
    }
}
