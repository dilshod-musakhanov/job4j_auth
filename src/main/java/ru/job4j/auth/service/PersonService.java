package ru.job4j.auth.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.auth.dto.PersonDto;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;

    public Optional<Person> save(Person person) {
        return findByLogin(person.getLogin())
                .map(existingPerson -> Optional.<Person>empty())
                .orElseGet(() -> Optional.of(personRepository.save(person)));
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Optional<Person> findById(int id) {
        return personRepository.findById(id);
    }

    public Optional<Person> findByLogin(String login) {
        return personRepository.findByLogin(login);
    }

    public void delete(Person person) {
        personRepository.delete(person);
    }

    public boolean updatePassword(PersonDto personDto) {
        var optionalPerson = personRepository.findById(personDto.getId());
        if (optionalPerson.isEmpty()) {
            return false;
        }
        var person = optionalPerson.get();
        person.setPassword(personDto.getPassword());
        personRepository.save(person);
        return true;
    }
}
