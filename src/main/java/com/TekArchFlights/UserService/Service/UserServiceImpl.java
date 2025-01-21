package com.TekArchFlights.UserService.Service;

import com.TekArchFlights.UserService.DTO.UserDTO;
import com.TekArchFlights.UserService.Service.Interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final RestTemplate restTemplate;

    @Value("${datastore.service.url}")
    private String datastoreServiceUrl;

    @Autowired
    public UserServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users from datastore service at {}", datastoreServiceUrl);
        try {
            ResponseEntity<List<UserDTO>> response = restTemplate.exchange(
                    datastoreServiceUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<UserDTO>>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Error fetching users: {}", ex.getMessage());
            throw new RuntimeException("Failed to fetch users from datastore service", ex);
        }
    }

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        String url =  datastoreServiceUrl + "/" + id;
        log.info("Fetching user with ID {} from datastore service at {}", id, url);
        try {
            ResponseEntity<UserDTO> response = restTemplate.getForEntity(url, UserDTO.class);
            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("User with ID {} not found", id);
            return Optional.empty();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Error fetching user with ID {}: {}", id, ex.getMessage());
            throw new RuntimeException("Failed to fetch user from datastore service", ex);
        }
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        log.info("Creating a new user in datastore service");
        try {
            ResponseEntity<UserDTO> response = restTemplate.postForEntity( datastoreServiceUrl, userDTO, UserDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Error creating user: {}", ex.getMessage());
            throw new RuntimeException("Failed to create user in datastore service", ex);
        }
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        String url =  datastoreServiceUrl+ "/" + id;
        log.info("Updating user with ID {} at {}", id, url);
        try {
            restTemplate.put(url, userDTO);
            log.info("Successfully updated user with ID {}", id);

            // Fetch the updated user for confirmation
            return getUserById(id).orElseThrow(() -> new RuntimeException("User not found after update for ID: " + id));
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("User with ID {} not found during update", id);
            throw new RuntimeException("User not found for update: " + id, ex);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Error updating user with ID {}: {}", id, ex.getMessage());
            throw new RuntimeException("Failed to update user in datastore service", ex);
        }
    }

    @Override
    public void deleteUser(Long id) {
        String url =  datastoreServiceUrl+ "/" + id;
        log.info("Deleting user with ID {} at {}", id, url);
        try {
            restTemplate.delete(url);
            log.info("Successfully deleted user with ID {}", id);
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("User with ID {} not found during deletion", id);
            throw new RuntimeException("User not found for deletion: " + id, ex);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Error deleting user with ID {}: {}", id, ex.getMessage());
            throw new RuntimeException("Failed to delete user in datastore service", ex);
        }
    }
}
