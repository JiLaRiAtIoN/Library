package ru.projects.ProjectBoot.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.projects.ProjectBoot.dto.book.BookDTO;
import ru.projects.ProjectBoot.dto.book.BooksResponse;
import ru.projects.ProjectBoot.util.validators.BookValidator;
import ru.projects.ProjectBoot.models.Book;
import ru.projects.ProjectBoot.services.book.BooksService;
import ru.projects.ProjectBoot.util.errors.book.BookErrorResponse;
import ru.projects.ProjectBoot.util.errors.book.BookException;

import javax.validation.Valid;

import java.util.Collections;
import java.util.stream.Collectors;

import static ru.projects.ProjectBoot.util.errors.book.ErrorsUtilBook.returnErrorsToClient;

@RestController
@RequestMapping("/books")
public class BooksController {

    private final BooksService booksService;
    private final BookValidator bookValidator;
    private final ModelMapper modelMapper;

    @Autowired
    public BooksController(BooksService booksService, BookValidator bookValidator, ModelMapper modelMapper) {
        this.booksService = booksService;
        this.bookValidator = bookValidator;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public BooksResponse getBooks() {
        return new BooksResponse(booksService.findAll().stream().map(this::convertToBookDTO).collect(Collectors.toList()));
        //return "books/index";
    }

    @GetMapping("/{id}")
    public BooksResponse getOneBook(@PathVariable("id") int id) {
        return new BooksResponse(Collections.singletonList(convertToBookDTO(booksService.findOne(id))));
        //return "books/show";
    }

    @PostMapping("/registration")
    public ResponseEntity<HttpStatus> registration(@RequestBody @Valid BookDTO bookDTO,
                                                   BindingResult bindingResult) {
        Book bookToAdd = convertToBook(bookDTO);

        bookValidator.validate(bookToAdd, bindingResult);

        if(bindingResult.hasErrors())
            returnErrorsToClient(bindingResult);
        booksService.save(bookToAdd);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}/edit")
    public ResponseEntity<HttpStatus> edit(@RequestBody @Valid BookDTO bookDTO,
                                           @PathVariable("id") int id,
                                           BindingResult bindingResult) {
        Book bookToEdit = convertToBook(bookDTO);

        bookValidator.validate(bookToEdit, bindingResult);

        if(bindingResult.hasErrors())
            returnErrorsToClient(bindingResult);
        booksService.update(id, bookToEdit);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {
        booksService.delete(id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}/release")
    public ResponseEntity<HttpStatus> release(@PathVariable("id") int id) {
        booksService.release(id);
        //return "redirect:/books/" + id;

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id_book}/assign/{id_person}")
    public ResponseEntity<HttpStatus> assign(@RequestBody @Valid BookDTO bookDTO,
                                             @PathVariable("id_book") int idBook,
                                             @PathVariable("id_person") int idPerson) {
        booksService.assign(idBook, idPerson);
        //return "redirect:/books/" + id;

        return ResponseEntity.ok(HttpStatus.OK);
    }

    private Book convertToBook(BookDTO bookDTO) {
        return modelMapper.map(bookDTO, Book.class);
    }

    private BookDTO convertToBookDTO(Book book) {
        return modelMapper.map(book, BookDTO.class);
    }

    @ExceptionHandler
    private ResponseEntity<BookErrorResponse> handlerException(BookException e) {
        BookErrorResponse response = new BookErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}