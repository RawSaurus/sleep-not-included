package com.rawsaurus.sleep_not_included.user.service;

import com.rawsaurus.sleep_not_included.user.dto.UserRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

@Service
public class KeycloakAdminService {

    @Value("${sni.security.admin-username}")
    private String adminUsername;
    @Value("${sni.security.admin-password}")
    private String adminPassword;
    @Value("${sni.security.keycloak-server-url}")
    private String keycloakServerUrl;
    @Value("${sni.security.realm}")
    private String realm;
    @Value("${sni.security.client-id}")
    private String clientId;
    @Value("${sni.security.client-uuid}")
    private String clientUuid;
    @Value("${sni.security.client-id-frontend}")
    private String clientIdFrontend;
    @Value("${sni.security.client-uuid-frontend}")
    private String clientUuidFrontend;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getAdminAccessToken(){
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("username", adminUsername);
        params.add("password", adminPassword);
        params.add("grant_type", "password");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
//        System.out.println(adminUsername + "\n" + adminPassword +"\n" + realm + "\n" + keycloakServerUrl + "\n" + clientId);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "http://localhost:8443/realms/sni/protocol/openid-connect/token",
                entity,
                Map.class
        );

        return response.getBody().get("access_token").toString();
    }

    public String createUser(String token, UserRequest request){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("username", request.username());
        userPayload.put("email", request.email());
        userPayload.put("enabled", true);

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", request.password());
        credentials.put("temporary", false);

        userPayload.put("credentials", List.of(credentials));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userPayload, headers);

        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users";
        ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                entity,
                String.class
        );

        if(!response.getStatusCode().equals(HttpStatus.CREATED)){
            throw new RuntimeException("Failed to create user in keycloak" + response.getBody());
        }

        URI location = response.getHeaders().getLocation();
        if(location == null){
            throw new RuntimeException("Location is empty for " + response.getBody());
        }

        String path = location.getPath();
        return path.substring(path.lastIndexOf("/")+1);
    }

    public void assignRealRoleToUser(String token, String username, String roleName, String userId){
//        String token = getAdminAccessToken();
        Map<String, Object> roleRep = getRealmRoleRepresentation(token, roleName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<List<Map<String, Object>>> entity = new HttpEntity<>(List.of(roleRep), headers);

        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/clients/" + clientUuidFrontend;

        ResponseEntity<Void> response = restTemplate.postForEntity(
                url,
                entity,
                Void.class
        );

        if(!response.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("Failed to assign " + roleName + " to user " + username + " : HTTP " + response.getStatusCode());
        }
    }

    private Map<String, Object> getRealmRoleRepresentation(String token, String roleName){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = keycloakServerUrl + "/admin/realms/" + realm + "/clients/" + clientUuidFrontend + "/roles/" + roleName;
        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
        );
        if(!response.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("HTTP: " + response.getStatusCode());
        }

        return response.getBody();
    }
}
