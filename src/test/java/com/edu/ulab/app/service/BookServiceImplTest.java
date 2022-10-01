package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.entity.UserEntity;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Тесты сервиса {@link BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
class BookServiceImplTest {

    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookRepository bookRepository;

    @Mock
    BookMapper bookMapper;

    @Test
    @DisplayName("Создание книги. Должно пройти успешно.")
    void saveBook_Test() {
        // given
        BookDto bookDto = prepareValidBookDto();

        BookDto result = prepareValidBookDto();
        result.setId(1L);

        BookEntity book = prepareValidBookEntity();

        BookEntity savedBook = prepareValidBookEntity();
        savedBook.setId(1L);

        // when
        when(bookMapper.bookDtoToBookEntity(any())).thenReturn(book);
        when(bookRepository.save(any())).thenReturn(savedBook);
        when(bookMapper.bookEntityToBookDto(any())).thenReturn(result);

        // then
        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
    }

    private BookEntity prepareValidBookEntity() {
        BookEntity book = new BookEntity();
        book.setTitle("test");
        book.setAuthor("Test Author");
        book.setPageCount(1000);
        return book;
    }

    private UserEntity prepareValidUserEntity() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setAge(111);
        user.setTitle("reader");
        user.setFullName("Test Test");
        return user;
    }

    @NotNull
    private BookDto prepareValidBookDto() {
        return BookDto.builder()
                .userId(1L)
                .author("test author")
                .title("test title")
                .pageCount(1000)
                .build();
    }


    // update
    // get
    // get all
    // delete

    // * failed
}
