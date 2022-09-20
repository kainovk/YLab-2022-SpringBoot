package com.edu.ulab.app.storage.repository;

import com.edu.ulab.app.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository implements CrudRepository<UserEntity, Long> {

    private long idGenerator = 0;

    private final Map<Long, UserEntity> users;

    @Override
    public UserEntity save(UserEntity userEntity) {
        Long userId = userEntity.getId();
        if (userId == null) {
            userEntity.setId(++idGenerator);
            users.put(idGenerator, userEntity);
        } else {
            users.replace(userId, userEntity);
        }
        return userEntity;
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<UserEntity> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    @Override
    public void delete(UserEntity userEntity) {
        users.remove(userEntity.getId());
    }

    public boolean existsById(Long id) {
        return users.containsKey(id);
    }
}
