package com.juliaosystem.infrastructure.adapters.primary;

import com.auth0.jwk.Jwk;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.common.lib.api.dtos.request.LoginDTO;
import com.common.lib.api.dtos.request.RegisterUserDTO;
import com.common.lib.api.response.PlantillaResponse;
import com.common.lib.infraestructure.adapters.primary.AuditImpl;
import com.common.lib.infraestructure.services.primary.CrudPrimaryService;
import com.juliaosystem.infrastructure.services.secondary.UserServiceInter;
import com.common.lib.utils.UserResponses;
import com.common.lib.utils.enums.ResponseType;
import com.juliaosystem.utils.jtw.JwtTokenUtil;
import com.juliaosystem.utils.jtw.PasswordEncoderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.security.interfaces.RSAPublicKey;
import java.util.*;


@Service
@RequiredArgsConstructor
public class UserImpl  implements CrudPrimaryService<RegisterUserDTO,RegisterUserDTO> {

    private final UserServiceInter userServiceInter;

     private  final JwtTokenUtil jwtTokenUtil;


     private final RestTemplate restTemplate;

     private  static  final String CLIENT_ID = "client_id";

    @Value("${keycloak.token}")
    private String keycloakTokenUri;

    @Value("${keycloak.user-info-uri}")
    private String keycloakUserInfo;

    @Value("${keycloak.logout}")
    private String keycloakLogout;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.authorization-grant-type}")
    private String grantType;

    @Value("${keycloak.authorization-grant-type-refresh}")
    private String grantTypeRefresh;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.scope}")
    private String scope;

    private final UserResponses<RegisterUserDTO> userResponses;

    public Optional<PlantillaResponse<RegisterUserDTO>> login(LoginDTO loginDTO){
        return Optional.ofNullable(userServiceInter.findByEmail(loginDTO.getEmail()))
                .map(user -> {
                    if (PasswordEncoderUtil.matches(loginDTO.getPassword(), user.getData().getPassword())) {
                        List<GrantedAuthority> authorities = new ArrayList<>();
                        authorities.add(new SimpleGrantedAuthority(user.getData().getDatesUser().getNombreRol()));

                        UserDetails userDetails =  new User(loginDTO.getEmail(), loginDTO.getPassword(), authorities);
                        Map<String, Object> additionalInfo = new HashMap<>();
                        additionalInfo.put("idBussines", user.getData().getIdBussines());

                        String token = jwtTokenUtil.generateToken(userDetails, additionalInfo);
                        user.getData().setToken(token);
                        return userResponses.buildResponse(ResponseType.USER_LOGEADO.getCode(), user.getData());
                    }else{
                        return userResponses.buildResponse(ResponseType.EMAIL_NOT_FOUD.getCode(), RegisterUserDTO.builder().build());

                }});

    }


    public PlantillaResponse<RegisterUserDTO> registro(RegisterUserDTO registerUserDTO ,PlantillaResponse<RegisterUserDTO> register ){
        var userSave = userServiceInter.addUser(registerUserDTO, register);
        if(userSave.getMessage().equalsIgnoreCase((ResponseType.CREATED.getMessage())))
           return userServiceInter.llenarEntidades(userSave.getData(), registerUserDTO);
        else return userResponses.buildResponse(ResponseType.fromMessage(userSave.getMessage()).getCode(), RegisterUserDTO.builder().build());
    }



    /**
     *  login by using username and password to keycloak, and capturing token on response body
     *
     *  @param username nombre de usuario
     * @param password  contraseña
     * @return token
     */
    public String loginKeycloak(String username, String password) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username",username);
        map.add("password",password);
        map.add(CLIENT_ID,clientId);
        map.add("grant_type",grantType);
        map.add("client_secret",clientSecret);
        map.add("scope",scope);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, new HttpHeaders());
        return restTemplate.postForObject(keycloakTokenUri, request, String.class);
    }

    /**
     *  a successful user token will generate http code 200, other than that will create an exception
     *
     * @param token string contiene el token del usuario
     * @return string con el token
     */
    public String checkValidity(String token)  {
        return getUserInfo(token);
    }

    private String getUserInfo(String token) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);
        return restTemplate.postForObject(keycloakUserInfo, request, String.class);
    }

    /**
     *  logging out and disabling active token from keycloak
     *
     * @param refreshToken token
     */
    public String refresh(String refreshToken)  {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(clientId,clientId);
        map.add("grant_type",grantTypeRefresh);
        map.add("refresh_token",refreshToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, null);
        return restTemplate.postForObject(keycloakTokenUri, request, String.class);
    }

    /**
     *  logging out and disabling active token from keycloak
     *
     * @param refreshToken token
     */
    public void logout(String refreshToken) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(CLIENT_ID,clientId);
        map.add("client_secret",clientSecret);
        map.add("refresh_token",refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, null);
        restTemplate.postForObject(keycloakLogout, request, String.class);
    }

    public Map<String, Integer> getRoles(String authHeader) throws Exception {

        DecodedJWT jwt = JWT.decode(authHeader.replace("Bearer", "").trim());

        // check JWT is valid
        Jwk jwk = userServiceInter.getJwk();
        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

        algorithm.verify(jwt);

        // check JWT role is correct
        List<String> roles  = (List<String>) jwt.getClaim("realm_access").asMap().get("roles");

        // check JWT is still active
        Date expiryDate = jwt.getExpiresAt();
        if (expiryDate.before(new Date())) {
            throw new Exception("token is expired");
        }
        // all validation passed
        Map<String, Integer> hashMap = new HashMap<>();
        for (String str : roles) {
            hashMap.put(str, str.length());
        }

        return hashMap;
    }

    @Override
    public PlantillaResponse<RegisterUserDTO> add(RegisterUserDTO e) {
        var register =userServiceInter.add(e);
        if(!register.getMessage().equalsIgnoreCase(ResponseType.FALLO.getMessage()) && !register.getMessage().equalsIgnoreCase(ResponseType.PASSWORD_VALIDATION_FAIL.getMessage())) {
            register = userServiceInter.findByEmail(e.getEmail());
            return registro(e,register);
        }
        return register;
    }


    @Override
    public PlantillaResponse<RegisterUserDTO> all(UUID id, Long idBussines) {
        if(id  == null) {
            return userServiceInter.byIdBussines(idBussines);
        }else
            return userServiceInter.byId(id);
    }

}
