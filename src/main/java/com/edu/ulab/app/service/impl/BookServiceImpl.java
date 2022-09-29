package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.edu.ulab.app.repository.BookRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @Override
    public BookDto createBook(BookDto bookDto) {
        BookEntity bookEntity = bookMapper.bookDtoToBookEntity(bookDto);
        BookEntity savedBook = bookRepository.save(bookEntity);
        return bookMapper.bookEntityToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        BookEntity foundBook = bookMapper.bookDtoToBookEntity(bookDto);
        BookEntity updatedBook;
        if (bookRepository.existsById(foundBook.getId())) {
            updatedBook = bookRepository.save(foundBook);
        } else {
            log.info("Book not found by id: {}", bookDto.getId());
            throw new NotFoundException("Book not found by id: " + bookDto.getId());
        }
        return bookMapper.bookEntityToBookDto(updatedBook);
    }

    @Override
    public BookDto getBookById(Long id) {
        BookEntity foundBook = findBookById(id);
        return bookMapper.bookEntityToBookDto(foundBook);
    }

    @Override
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::bookEntityToBookDto)
                .toList();
    }

    @Override
    public void deleteBookById(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
        }
    }

    private BookEntity findBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Book with id " + id + " not found")
        );
    }
}
