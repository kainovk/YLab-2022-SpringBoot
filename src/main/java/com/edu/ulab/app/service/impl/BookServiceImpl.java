package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.storage.Storage;
import com.edu.ulab.app.storage.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final Storage storage;

    private final BookMapper bookMapper;

    @Override
    public BookDto createBook(BookDto bookDto) {
        BookRepository bookRepository = storage.getBookRepository();

        BookEntity bookEntity = bookMapper.bookDtoToBookEntity(bookDto);
        BookEntity savedBook = bookRepository.save(bookEntity);
        return bookMapper.bookEntityToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        BookRepository bookRepository = storage.getBookRepository();

        BookEntity foundBook = findBookById(bookDto.getId(), bookRepository);
        BookEntity updatedBook = bookRepository.save(foundBook);
        return bookMapper.bookEntityToBookDto(updatedBook);
    }

    @Override
    public BookDto getBookById(Long id) {
        BookRepository bookRepository = storage.getBookRepository();

        BookEntity foundBook = findBookById(id, bookRepository);
        return bookMapper.bookEntityToBookDto(foundBook);
    }

    @Override
    public List<BookDto> getAllBooks() {
        BookRepository bookRepository = storage.getBookRepository();

        return bookRepository.findAll()
                .stream()
                .map(bookMapper::bookEntityToBookDto)
                .toList();
    }

    @Override
    public void deleteBookById(Long id) {
        BookRepository bookRepository = storage.getBookRepository();

        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
        }
    }

    private BookEntity findBookById(Long id, BookRepository bookRepository) {
        return bookRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Book with id " + id + " not found")
        );
    }
}
