package ru.job4j.auth.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.job4j.auth.model.Person;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.job4j.auth.service.PersonService;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/persons")
public class PersonController {
    private final PersonService personService;
    private final BCryptPasswordEncoder encoder;

    @GetMapping("/all")
    public ResponseEntity<List<Person>> findAll() {
        List<Person> persons = personService.findAll();
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = personService.findById(id);
        return new ResponseEntity<Person>(
                person.orElse(new Person()),
                person.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Person> create(@RequestBody Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
        var result = personService.save(person);
        return result.map(p -> new ResponseEntity<>(p, HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.CONFLICT));
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        personService.save(person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        var person = new Person();
        person.setId(id);
        personService.delete(person);
        return ResponseEntity.ok().build();
    }

}
