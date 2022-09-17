package com.edu.ulab.app.storage.repository;

import com.edu.ulab.app.entity.BookEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookRepository implements CrudRepository<BookEntity, Long> {

    private long idGenerator = 0;

    private final Map<Long, BookEntity> books;

    @Override
    public BookEntity save(BookEntity bookEntity) {
        Long bookId = bookEntity.getId();
        if (bookId == null) {
            bookEntity.setId(++idGenerator);
            books.put(idGenerator, bookEntity);
        } else {
            books.replace(bookId, bookEntity);
        }
        return bookEntity;
    }

    @Override
    public Optional<BookEntity> findById(Long id) {
        if (books.containsKey(id)) {
            return Optional.of(books.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<BookEntity> findAll() {
        return new ArrayList<>(books.values());
    }

    @Override
    public void deleteById(Long id) {
        books.remove(id);
    }

    @Override
    public void delete(BookEntity entity) {
        books.remove(entity.getId());
    }

    public boolean existsById(Long id) {
        return books.containsKey(id);
    }
}
