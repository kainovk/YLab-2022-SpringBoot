package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.UserEntity;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImpl;
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
 * Тесты сервиса {@link UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("Создание юзера. Должно активировать репозиторий")
    void saveUser_shouldTriggerRepository() {
        // given
        UserDto userDto = prepareValidUserDto();
        UserEntity userEntity = prepareValidUserEntity();

        // when
        when(userMapper.userDtoToUserEntity(any())).thenReturn(userEntity);
        userService.createUser(userDto);

        // then
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());

        assertThat(captor.getValue()).isEqualTo(userMapper.userDtoToUserEntity(userDto));
    }

    @Test
    @DisplayName("Обновление юзера. Должно активировать репозиторий")
    void updateUser_shouldTriggerRepository() {
        // given
        long id = 1;
        UserEntity userEntity = prepareValidUserEntity();
        userEntity.setId(id);

        UserDto userDto = prepareValidUserDto();

        // when
        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userMapper.userDtoToUserEntity(any())).thenReturn(userEntity);
        when(userMapper.userEntityToUserDto(any())).thenReturn(userDto);
        userService.updateUser(userMapper.userEntityToUserDto(userEntity));

        // then
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(userEntity);
    }

    @Test
    @DisplayName("Получение юзера. Должно активировать репозиторий")
    void getUser_shouldTriggerRepository() {
        // given
        long id = 1L;
        UserEntity userEntity = prepareValidUserEntity();
        userEntity.setId(id);

        // when
        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));
        UserDto foundUserDto = userService.getUserById(id);

        // then
        verify(userRepository).findById(id);
        assertThat(foundUserDto).isEqualTo(userMapper.userEntityToUserDto(userEntity));
    }

    @Test
    void deleteUserById_shouldTriggerRepository() {
        // given
        long id = 1L;
        UserEntity userEntity = prepareValidUserEntity();
        userEntity.setId(id);
        userRepository.save(userEntity);

        // when
        when(userRepository.existsById(id)).thenReturn(true);
        userService.deleteUserById(id);

        // then
        verify(userRepository).deleteById(id);
    }

    @Test
    @DisplayName("Поиск юзера по несуществующему id. Должно выдать исключение")
    void findUserById_ShouldThrowOnNonExistingId() {
        // given
        long id = 1;

        // when
        // then
        assertThatThrownBy(() -> userService.getUserById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with id %d not found", id);
    }

    private UserEntity prepareValidUserEntity() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setAge(111);
        user.setTitle("reader");
        user.setFullName("Test Test");
        return user;
    }

    private UserDto prepareValidUserDto() {
        return UserDto.builder()
                .id(1L)
                .fullName("Kirill")
                .title("some title")
                .age(50)
                .build();
    }
}
