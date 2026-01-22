package pnvteck.auth.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pnvteck.auth.client.dto.CreateUserRequest;
import pnvteck.auth.client.dto.UserInternalResponse;

@Service
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${user-service.base-url}")
    private String baseUrl;

    public UserInternalResponse createUser(CreateUserRequest request) {
        ResponseEntity<UserInternalResponse> response = restTemplate.postForEntity(
                baseUrl + "/internal/users",
                request,
                UserInternalResponse.class);
        return response.getBody();
    }

    public UserInternalResponse getByUsername(String username) {
        return restTemplate.getForObject(
                baseUrl + "/internal/users/by-username/" + username,
                UserInternalResponse.class);
    }

    public UserInternalResponse getByEmail(String email) {
        return restTemplate.getForObject(
                baseUrl + "/internal/users/by-email?email=" + email,
                UserInternalResponse.class);
    }

    public UserInternalResponse updateStatus(Long userId, String status) {
        String body = "{\"status\":\"" + status + "\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<UserInternalResponse> response = restTemplate.exchange(
                baseUrl + "/internal/users/" + userId + "/status",
                HttpMethod.PATCH,
                request,
                UserInternalResponse.class);
        return response.getBody();
    }
}
