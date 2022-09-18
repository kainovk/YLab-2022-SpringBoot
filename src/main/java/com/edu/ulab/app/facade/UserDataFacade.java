package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.web.request.BookRequest;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.request.UserBookUpdateRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserDataFacade {
    private final UserService userService;
    private final BookService bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserService userService,
                          BookService bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

        List<BookRequest> bookRequests = userBookRequest.getBookRequests();
        List<Long> bookIdList = collectBookIdsFromBookRequests(createdUser, bookRequests);
        log.info("Collected book ids: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(createdUser.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public UserBookResponse updateUserWithBooks(UserBookUpdateRequest userBookRequest) {
        log.info("Got user book update request: {}", userBookRequest);
        UserDto userDto = userMapper.userUpdateRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto updatedUser = userService.updateUser(userDto);
        log.info("Updated user: {}", updatedUser);

        Long userId = userDto.getId();
        deleteAllBooksByUserId(userId);
        log.info("Deleted all books of user with id {}: ", userId);

        List<BookRequest> bookRequests = userBookRequest.getBookRequests();
        List<Long> bookIdList = collectBookIdsFromBookRequests(updatedUser, bookRequests);
        log.info("Collected book ids: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(updatedUser.getId())
                .booksIdList(bookIdList)
                .build();
    }

    private void deleteAllBooksByUserId(Long userId) {
        bookService.getAllBooks()
                .stream()
                .filter(book -> book.getUserId().equals(userId))
                .map(BookDto::getId)
                .forEach(bookService::deleteBookById);
    }

    private List<Long> collectBookIdsFromBookRequests(UserDto updatedUser, List<BookRequest> bookRequests) {
        return bookRequests.stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(updatedUser.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(BookDto::getId)
                .toList();
    }

    public UserBookResponse getUserWithBooks(Long userId) {
        log.info("Got user id: {}", userId);

        UserDto foundUser = userService.getUserById(userId);
        log.info("Found user: {}", foundUser);

        List<Long> booksIdList = bookService.getAllBooks()
                .stream()
                .filter(book -> book.getUserId().equals(userId))
                .map(BookDto::getId)
                .toList();
        log.info("Collected book ids: {}", booksIdList);

        return UserBookResponse.builder()
                .userId(foundUser.getId())
                .booksIdList(booksIdList)
                .build();
    }

    public void deleteUserWithBooks(Long userId) {
        userService.getUserById(userId);
        deleteAllBooksByUserId(userId);
        userService.deleteUserById(userId);
    }
}
