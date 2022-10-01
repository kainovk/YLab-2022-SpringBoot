package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.entity.UserEntity;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    @DisplayName("Создание книги. Должно активировать репозиторий")
    void saveBook_shouldTriggerRepository() {
        // given
        BookDto bookDto = prepareValidBookDto();
        BookEntity bookEntity = prepareValidBookEntity();

        // when
        when(bookMapper.bookDtoToBookEntity(any())).thenReturn(bookEntity);
        bookService.createBook(bookDto);

        // then
        ArgumentCaptor<BookEntity> captor = ArgumentCaptor.forClass(BookEntity.class);
        verify(bookRepository).save(captor.capture());

        assertThat(captor.getValue()).isEqualTo(bookMapper.bookDtoToBookEntity(bookDto));
    }

    @Test
    @DisplayName("Обновление книги. Должно активировать репозиторий")
    void updateBook_shouldTriggerRepository() {
        // given
        long id = 1;
        BookEntity bookEntity = prepareValidBookEntity();
        bookEntity.setId(id);

        // when
        when(bookRepository.findById(id)).thenReturn(Optional.of(bookEntity));
        when(bookRepository.existsById(anyLong())).thenReturn(true);
        when(bookMapper.bookDtoToBookEntity(any())).thenReturn(bookEntity);
        bookService.updateBook(bookMapper.bookEntityToBookDto(bookEntity));

        // then
        ArgumentCaptor<BookEntity> captor = ArgumentCaptor.forClass(BookEntity.class);
        verify(bookRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(bookEntity);
    }

    @Test
    @DisplayName("Получение книги. Должно активировать репозиторий")
    void getBook_shouldTriggerRepository() {
        // given
        long id = 1L;
        BookEntity bookEntity = prepareValidBookEntity();
        bookEntity.setId(id);

        // when
        when(bookRepository.findById(id)).thenReturn(Optional.of(bookEntity));
        BookDto foundBookDto = bookService.getBookById(id);

        // then
        verify(bookRepository).findById(id);
        assertThat(foundBookDto).isEqualTo(bookMapper.bookEntityToBookDto(bookEntity));
    }

    @Test
    @DisplayName("Получение всех книг. Должно активировать репозиторий")
    void getAllBooks_shouldTriggerRepository() {
        // when
        bookService.getAllBooks();

        // then
        verify(bookRepository).findAll();
    }

    @Test
    @DisplayName("Удаление книги. Должно активировать репозиторий")
    void deleteBookById_shouldTriggerRepository() {
        // given
        long id = 1L;
        BookEntity bookEntity = prepareValidBookEntity();
        bookEntity.setId(id);
        bookRepository.save(bookEntity);

        // when
        when(bookRepository.existsById(id)).thenReturn(true);
        bookService.deleteBookById(id);

        // then
        verify(bookRepository).deleteById(id);
    }

    @Test
    @DisplayName("Поиск книги по несуществующему id. Должно выдать исключение")
    void findBookById_ShouldThrowOnNonExistingId() {
        // given
        long id = 1;

        // when
        // then
        assertThatThrownBy(() -> bookService.getBookById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Book with id %d not found", id);
    }

    private BookEntity prepareValidBookEntity() {
        BookEntity book = new BookEntity();
        book.setTitle("test");
        book.setAuthor("Test Author");
        book.setPageCount(1000);
        return book;
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
}
