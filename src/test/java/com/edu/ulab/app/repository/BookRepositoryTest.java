package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.entity.UserEntity;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static com.vladmihalcea.sql.SQLStatementCountValidator.assertDeleteCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertInsertCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertUpdateCount;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты репозитория {@link BookRepository}.
 */
@SystemJpaTest
class BookRepositoryTest {

    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить книгу. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void saveBook_thenAssertDmlCount() {
        // given
        UserEntity user = userRepository.save(prepareValidUserEntity());
        BookEntity book = prepareValidBookEntity();
        book.setUserEntity(user);

        // when
        BookEntity result = bookRepository.save(book);

        // then
        assertThat(result.getPageCount()).isEqualTo(1000);
        assertThat(result.getTitle()).isEqualTo("test");
        assertSelectCount(1);
        assertInsertCount(2);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Найти книгу. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void findBookById_thenAssertDmlCount() {
        // when
        Optional<BookEntity> book = bookRepository.findById(2002L);

        // then
        assertThat(book).isPresent();
        assertThat(book.get().getPageCount()).isEqualTo(5500);
        assertThat(book.get().getTitle()).isEqualTo("default book");
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Найти все книги. Размер должен равняться 2")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void findAllBooks_shouldBeFound() {
        // when
        List<BookEntity> books = bookRepository.findAll();

        // then
        assertThat(books).hasSize(2);
    }

    @DisplayName("Обновить книгу. Должно обновиться")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updateBook_shouldBeUpdated() {
        // given
        UserEntity user = userRepository.save(prepareValidUserEntity());
        BookEntity book = prepareValidBookEntity();
        book.setId(2002L);
        book.setUserEntity(user);

        // when
        BookEntity result = bookRepository.save(book);

        // then
        assertThat(result.getPageCount()).isEqualTo(1000);
        assertThat(result.getTitle()).isEqualTo("test");
    }

    @DisplayName("Удалить книгу. Должна удалиться подготовленная книга")
    @Test
    @Rollback
    @Sql({
            "classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deleteBook_shouldBeDeleted() {
        // given
        long id = 2002;

        // when
        bookRepository.deleteById(id);
        Optional<BookEntity> foundBook = bookRepository.findById(id);

        // then
        assertThat(foundBook).isEmpty();
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
}
