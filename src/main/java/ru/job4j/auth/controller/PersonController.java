package ru.job4j.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.dto.PersonDto;
import ru.job4j.auth.model.Person;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.job4j.auth.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/persons")
public class PersonController {
    private final PersonService personService;
    private final BCryptPasswordEncoder encoder;

    private final ObjectMapper objectMapper;

    @GetMapping("/all")
    public ResponseEntity<List<Person>> findAll() {
        List<Person> persons = personService.findAll();
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = personService.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Person is not found"));
        return ResponseEntity.ok(person);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Person> create(@RequestBody Person person) {
        var login = person.getLogin();
        var password = person.getPassword();
        if (login.isEmpty() || password.isEmpty()) {
            throw new NullPointerException();
        }
        if (login.length() < 2) {
            throw new IllegalArgumentException(
                    "Invalid login. Login length must be more than 2 characters."
            );
        }
        person.setPassword(encoder.encode(person.getPassword()));
        var result = personService.save(person);
        if (result.isEmpty()) {
            throw new IllegalStateException(
                    "Account exist. Please try to login."
            );
        }
        return result.map(p -> new ResponseEntity<>(p, HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.CONFLICT));
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        personService.save(person);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/updatePassword")
    public ResponseEntity<Void> updatePassword(@RequestBody PersonDto personDto) {
        var password = personDto.getPassword();
        if (password == null) {
            throw new NullPointerException();
        }
        if (password.length() < 3) {
            throw new IllegalArgumentException(
                    "Invalid password. Password length must be minimum 3 or more characters."
            );
        }
        personDto.setPassword(encoder.encode(personDto.getPassword()));
        return personService.updatePassword(personDto) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        var person = new Person();
        person.setId(id);
        personService.delete(person);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
    public void exceptionHandler(Exception e, HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() {{
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
    }
}
