package com.edu.ulab.app.storage;

import com.edu.ulab.app.storage.repository.BookRepository;
import com.edu.ulab.app.storage.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class Storage {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
}
