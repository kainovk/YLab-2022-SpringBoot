package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.entity.UserEntity;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
 * Тесты репозитория {@link UserRepository}.
 */
@SystemJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить юзера. Число insert должно равняться 1")
    @Test
    @Rollback
    @Sql("classpath:sql/1_clear_schema.sql")
    void saveUser_thenAssertDmlCount() {
        // given
        UserEntity user = prepareValidUserEntity();

        // when
        UserEntity result = userRepository.save(user);

        // then
        assertThat(result.getAge()).isEqualTo(111);
        assertSelectCount(1);
        assertInsertCount(1);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Найти юзера. Должен найтись подготовленный юзер")
    @Test
    @Rollback
    @Sql({
            "classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql"
    })
    void findUserById_shouldBeFound() {
        // given
        long id = 1001L;

        // when
        userRepository.findById(id);
        Optional<UserEntity> foundUser = userRepository.findById(id);

        // then
        assertThat(foundUser).isPresent().isNotEqualTo(Optional.empty());
        assertThat(foundUser.get().getFullName()).isEqualTo("default user");
    }

    @DisplayName("Найти всех юзеров. Должен найтись подготовленный юзер")
    @Test
    @Rollback
    @Sql({
            "classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql"
    })
    void findAllUsers_shouldBeFound() {
        // when
        List<UserEntity> users = userRepository.findAll();


        // then
        assertThat(users).isNotEqualTo(List.of());
        assertThat(users).hasSize(1);
    }

    @DisplayName("Обновить юзера. Должен обновиться подготовленный юзер")
    @Test
    @Rollback
    @Sql({
            "classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql"
    })
    void updateUser_shouldBeUpdated() {
        // given
        UserEntity user = prepareValidUserEntity();
        user.setId(1001L);

        // when
        UserEntity result = userRepository.save(user);

        // then
        assertThat(user.getTitle()).isNotEqualTo("reader");
        assertThat(user.getTitle()).isEqualTo(result.getTitle());
    }

    @DisplayName("Удалить юзера. Должен удалиться подготовленный юзер")
    @Test
    @Rollback
    @Sql({
            "classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql"
    })
    void deleteUser_shouldBeDeleted() {
        // given
        long id = 1001L;

        // when
        userRepository.deleteById(id);
        Optional<UserEntity> foundUser = userRepository.findById(id);

        // then
        assertThat(foundUser).isEmpty();
    }

    private UserEntity prepareValidUserEntity() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setAge(111);
        user.setTitle("other reader");
        user.setFullName("Test Test");
        return user;
    }
}
