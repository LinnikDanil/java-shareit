package ru.practicum.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.user.dto.UserCreateDto;
import ru.practicum.user.dto.UserUpdateDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("");
    }

    public ResponseEntity<Object> getUserById(long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> saveNewUser(UserCreateDto userDto) {
        return post("", userDto);
    }

    public ResponseEntity<Object> updateUser(UserUpdateDto userDto, long userId) {
        return patch("/" + userId, userDto);
    }

    public void deleteUser(long userId) {
        delete("/" + userId);
    }
}
