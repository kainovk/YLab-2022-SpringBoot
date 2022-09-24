package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.service.BookService;
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
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public BookDto createBook(BookDto bookDto) {
        final String INSERT_SQL =
                "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) " +
                "VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, bookDto.getTitle());
                    ps.setString(2, bookDto.getAuthor());
                    ps.setLong(3, bookDto.getPageCount());
                    ps.setLong(4, bookDto.getUserId());
                    return ps;
                }, keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        String UPDATE_SQL =
                "UPDATE BOOK " +
                "SET TITLE = ?, AUTHOR = ?, PAGE_COUNT = ? " +
                "WHERE ID = ?";
        Long id = bookDto.getId();
        int rowsAffected = jdbcTemplate.update(UPDATE_SQL,
                bookDto.getTitle(),
                bookDto.getAuthor(),
                bookDto.getPageCount(),
                bookDto.getId());
        if (rowsAffected != 1) {
            log.info("Book does not exist by id: {}", id);
            throw new NotFoundException("Book does not exist by id: " + id);
        }
        return bookDto;
    }

    @Override
    public BookDto getBookById(Long id) {
        final String SELECT_BY_ID_SQL =
                "SELECT ID, USER_ID, TITLE, AUTHOR, PAGE_COUNT FROM BOOK " +
                "WHERE ID = ?";
        BookDto bookFound;
        try {
            bookFound = jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, this::mapRowToBookDto, id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Book not found by id: {}", id);
            throw new NotFoundException("Book not found by id: " + id);
        }
        return bookFound;
    }

    @Override
    public List<BookDto> getAllBooks() {
        final String SELECT_BY_ID_SQL = "SELECT * FROM BOOK";
        return jdbcTemplate.query(SELECT_BY_ID_SQL, this::mapRowToBookDto);
    }

    @Override
    public void deleteBookById(Long id) {
        String DELETE_BY_ID_SQL =
                "DELETE FROM BOOK " +
                "WHERE ID = ?";
        try {
            jdbcTemplate.update(DELETE_BY_ID_SQL, id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Book does not exist by id: {}", id);
            throw new NotFoundException("Book does not exist by id: " + id);
        }
    }

    private BookDto mapRowToBookDto(ResultSet resultSet, int rowNum) throws SQLException {
        return BookDto.builder()
                .id(resultSet.getLong("ID"))
                .userId(resultSet.getLong("USER_ID"))
                .title(resultSet.getString("TITLE"))
                .author(resultSet.getString("AUTHOR"))
                .pageCount(resultSet.getLong("PAGE_COUNT"))
                .build();
    }
}
