package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.UserEntity;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.storage.Storage;
import com.edu.ulab.app.storage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Storage storage;

    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        UserRepository userRepository = storage.getUserRepository();

        UserEntity userEntity = userMapper.userDtoToUserEntity(userDto);
        UserEntity savedUser = userRepository.save(userEntity);
        return userMapper.userEntityToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        UserRepository userRepository = storage.getUserRepository();

        UserEntity foundUser = findUserById(userDto.getId(), userRepository);
        UserEntity updatedUser = userRepository.save(foundUser);
        return userMapper.userEntityToUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        UserRepository userRepository = storage.getUserRepository();

        UserEntity foundUser = findUserById(id, userRepository);
        return userMapper.userEntityToUserDto(foundUser);
    }

    @Override
    public void deleteUserById(Long id) {
        UserRepository userRepository = storage.getUserRepository();

        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
    }

    private UserEntity findUserById(Long id, UserRepository userRepository) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("User with id " + id + " not found")
        );
    }
}
