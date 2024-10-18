package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
public class Controller {
    private static final Set<User> users = new HashSet<>();

    @PostMapping("/create/{name}")
    public ResponseEntity<User> createUser(@PathVariable("name") String name) {
        log.info("Attempting to create a new user with name: {}", name);
        Optional<User> opUser = getUserFromUsers(name);
        User user = new User();
        if (!opUser.isPresent()) {
            user.setId(Math.abs(new SecureRandom().nextLong()));
            user.setName(name);
            user.setCreatedAt(getDateFromLocalDate());

            users.add(user);
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/read/{name}")
    public ResponseEntity<User> readUser(@PathVariable("name") String name) {
        log.info("Attempting to get a user with name: {}", name);

        User user = users.stream()
                .filter(u -> u.getName().equals(name))
                .findFirst()

                .orElse(new User());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/{oldName}/{newName}")
    public ResponseEntity<Set<User>> updateUser(@PathVariable("oldName") String oldName, @PathVariable("newName") String newName) {
        log.info("Attempting to update a user with name: {}", newName);
        users.stream()
                .peek(u -> {
                    if (u.getName().equals(oldName)) {
                        u.setName(newName);
                        u.setCreatedAt(getDateFromLocalDate());
                    }
                })
                .collect(Collectors.toSet());

        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<?> deleteUser(@PathVariable("name") String name) {
        log.info("Attempting to delete a user with name: {}", name);
        Optional<User> opUser = getUserFromUsers(name);
        if (opUser.isPresent()) {
            users.remove(opUser.get());
        }

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/patch/{oldName}/{newName}")
    public ResponseEntity<Set<User>> patchUser(@PathVariable("oldName") String oldName, @PathVariable("newName") String newName) {
        log.info("Attempting to patch a user with name: {}", newName);
        users.stream()
                .peek(u -> {
                    if (u.getName().equals(oldName)) {
                        u.setName(newName);
                    }
                })
                .collect(Collectors.toSet());

        return ResponseEntity.ok(users);
    }

    private Optional<User> getUserFromUsers(String name) {
        return users.stream()
                .filter(u -> u.getName().equals(name))
                .findFirst();
    }

    private Date getDateFromLocalDate() {
        return Date.from(LocalDate.now()
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
}