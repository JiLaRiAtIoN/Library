package ru.projects.ProjectBoot.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.projects.ProjectBoot.dto.person.PeopleResponse;
import ru.projects.ProjectBoot.dto.person.PersonDTO;
import ru.projects.ProjectBoot.models.Person;
import ru.projects.ProjectBoot.services.person.PeopleService;
import ru.projects.ProjectBoot.util.errors.person.PersonErrorResponse;
import ru.projects.ProjectBoot.util.errors.person.PersonException;
import ru.projects.ProjectBoot.util.validators.PersonValidator;

import javax.validation.Valid;
import java.util.Collections;
import java.util.stream.Collectors;

import static ru.projects.ProjectBoot.util.errors.person.ErrorUtilPerson.returnErrorsToClient;


@RestController
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    private final PersonValidator personValidator;
    private final ModelMapper modelMapper;

    @Autowired
    public PeopleController(PeopleService peopleService, PersonValidator personValidator, ModelMapper modelMapper) {
        this.peopleService = peopleService;
        this.personValidator = personValidator;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<HttpStatus> login(@RequestBody @Valid PersonDTO personDTO,
                                            BindingResult bindingResult) {
        Person personToLogin = convertToPerson(personDTO);

        if(bindingResult.hasErrors())
            returnErrorsToClient(bindingResult);
        peopleService.login(personToLogin);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping()
    public PeopleResponse getPeople() {
        return new PeopleResponse(peopleService.findAll().stream().map(this::convertToPersonDTO).collect(Collectors.toList()));
        //return "people/index";
    }

    @GetMapping("/{id}")
    public PeopleResponse getOnePerson(@PathVariable("id") int id) {
        return new PeopleResponse(Collections.singletonList(convertToPersonDTO(peopleService.findOne(id))));
        //return "people/show";
    }

    @PostMapping("/registration")
    public ResponseEntity<HttpStatus> registration(@RequestBody @Valid PersonDTO personDTO,
                                                   BindingResult bindingResult) {
        Person personToAdd = convertToPerson(personDTO);

        personValidator.validate(personToAdd, bindingResult);

        if(bindingResult.hasErrors())
            returnErrorsToClient(bindingResult);
        peopleService.save(personToAdd);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("{id}/edit")
    public ResponseEntity<HttpStatus> edit(@RequestBody @Valid PersonDTO personDTO,
                                           @PathVariable("id") int id,
                                           BindingResult bindingResult) {
        Person personToEdit = convertToPerson(personDTO);

        personValidator.validate(personToEdit, bindingResult);

        if(bindingResult.hasErrors())
            returnErrorsToClient(bindingResult);
        peopleService.update(id, personToEdit);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {
        peopleService.delete(id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    public Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    private PersonDTO convertToPersonDTO(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handlerException(PersonException e) {
        PersonErrorResponse response = new PersonErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}