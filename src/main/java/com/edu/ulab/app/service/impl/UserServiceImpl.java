package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.UserEntity;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.edu.ulab.app.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        UserEntity userEntity = userMapper.userDtoToUserEntity(userDto);
        UserEntity savedUser = userRepository.save(userEntity);
        return userMapper.userEntityToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        UserEntity foundUser = findUserById(userDto.getId());
        UserEntity updatedUser;
        if (userRepository.existsById(foundUser.getId())) {
            updatedUser = userRepository.save(foundUser);
        } else {
            log.info("User not found by id: {}", userDto.getId());
            throw new NotFoundException("User not found by id: " + userDto.getId());
        }
        return userMapper.userEntityToUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        UserEntity foundUser = findUserById(id);
        return userMapper.userEntityToUserDto(foundUser);
    }

    @Override
    public void deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
    }

    private UserEntity findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("User with id " + id + " not found")
        );
    }
}
