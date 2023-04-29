package ru.projects.ProjectBoot.dto.person;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PeopleResponse {

    private List<PersonDTO> people;

    public PeopleResponse(List<PersonDTO> people) {
        this.people = people;
    }
}
