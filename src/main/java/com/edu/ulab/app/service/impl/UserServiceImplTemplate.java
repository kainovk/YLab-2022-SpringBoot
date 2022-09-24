package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImplTemplate implements UserService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public UserDto createUser(UserDto userDto) {
        final String INSERT_SQL = "INSERT INTO PERSON(FULL_NAME, TITLE, AGE) VALUES (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);

        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        String UPDATE_SQL =
                "UPDATE PERSON " +
                "SET FULL_NAME = ?, TITLE = ?, AGE = ? " +
                 "WHERE ID = ?";
        Long id = userDto.getId();
        int rowsAffected = jdbcTemplate.update(UPDATE_SQL,
                userDto.getFullName(),
                userDto.getTitle(),
                userDto.getAge(),
                userDto.getId());
        if (rowsAffected != 1) {
            log.info("User does not exist by id: {}", id);
            throw new NotFoundException("User does not exist by id: " + id);
        }
        return userDto;
    }

    @Override
    public UserDto getUserById(Long id) {
        final String SELECT_BY_ID_SQL =
                "SELECT ID, FULL_NAME, TITLE, AGE FROM PERSON " +
                "WHERE ID = ?";
        UserDto userFound;
        try {
            userFound = jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, this::mapRowToUserDto, id);
        } catch (EmptyResultDataAccessException e) {
            log.info("User not found by id: {}", id);
            throw new NotFoundException("User not found by id: " + id);
        }
        return userFound;
    }

    @Override
    public void deleteUserById(Long id) {
        String DELETE_BY_ID_SQL =
                "DELETE FROM PERSON " +
                "WHERE ID = ?";
        try {
            jdbcTemplate.update(DELETE_BY_ID_SQL, id);
        } catch (EmptyResultDataAccessException e) {
            log.info("User does not exist by id: {}", id);
            throw new NotFoundException("User does not exist by id: " + id);
        }
    }

    private UserDto mapRowToUserDto(ResultSet resultSet, int rowNum) throws SQLException {
        return UserDto.builder()
                .id(resultSet.getLong("ID"))
                .fullName(resultSet.getString("FULL_NAME"))
                .title(resultSet.getString("TITLE"))
                .age(resultSet.getInt("AGE"))
                .build();
    }
}
