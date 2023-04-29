package ru.projects.ProjectBoot.util.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.projects.ProjectBoot.models.Book;
import ru.projects.ProjectBoot.services.book.BooksService;

@Component
public class BookValidator implements Validator {

    private final BooksService booksService;

    @Autowired
    public BookValidator(BooksService booksService) {
        this.booksService = booksService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Book.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Book book = (Book) o;

        if(booksService.findByTitle(book.getTitle()).isPresent())
            errors.rejectValue("title", "Книга с таким названием уже существует");
    }
}
