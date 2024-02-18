package ru.msvdev.ds.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.msvdev.ds.server.data.service.UserService;
import ru.msvdev.ds.server.openapi.api.UserApi;
import ru.msvdev.ds.server.openapi.model.UserAuthorities;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    public ResponseEntity<Void> removeUserAuthorities(UUID userUUID, Long catalogId, UUID user) {
        userService.deleteUser(catalogId, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<UserAuthorities> updateUserAuthorities(UUID userUUID, Long catalogId, UserAuthorities userAuthorities) {
        UserAuthorities newUserAuthorities = userService.updateAuthorities(catalogId, userAuthorities);
        return new ResponseEntity<>(newUserAuthorities, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserAuthorities>> userList(UUID userUUID, Long catalogId) {
        List<UserAuthorities> userAuthorities = userService.getAllUsers(catalogId);
        return new ResponseEntity<>(userAuthorities, HttpStatus.OK);
    }
}
