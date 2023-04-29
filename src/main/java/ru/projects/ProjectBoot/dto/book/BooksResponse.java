package ru.projects.ProjectBoot.dto.book;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BooksResponse {

    private List<BookDTO> books;

    public BooksResponse(List<BookDTO> books) {
        this.books = books;
    }
}
