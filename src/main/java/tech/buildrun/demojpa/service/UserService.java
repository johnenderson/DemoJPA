package tech.buildrun.demojpa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tech.buildrun.demojpa.controller.dto.CreateUserDTO;
import tech.buildrun.demojpa.controller.dto.UpdateUserDTO;
import tech.buildrun.demojpa.entity.UserEntity;
import tech.buildrun.demojpa.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.hasText;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserEntity> findAll(Integer page,
                                    Integer pageSize,
                                    String orderBy,
                                    String name,
                                    Long age) {

        var pageRequest = getPageRequest(page, pageSize, orderBy);

        return findWithFilter(name, age, pageRequest);
    }

    private Page<UserEntity> findWithFilter(String name, Long age, PageRequest pageRequest) {
        if (hasText(name) && !isNull(age)) {
            return userRepository.findByNameAndAgeGreaterThanEqual(name, age, pageRequest);
        }

        if (hasText(name)) {
            return userRepository.findByName(name, pageRequest);
        }

        if (!isNull(age)) {
            return userRepository.findByAgeGreaterThanEqual(age, pageRequest);
        }

        return userRepository.findAll(pageRequest);
    }

    private PageRequest getPageRequest(Integer page, Integer pageSize, String orderBy) {
        var direction = Sort.Direction.DESC;
        if (orderBy.equalsIgnoreCase("asc")) {
            direction = Sort.Direction.ASC;
        }

        var pageRequest = PageRequest.of(page, pageSize, direction, "created_at");
        return pageRequest;
    }

    public Optional<UserEntity> findById(Long userId) {
        return userRepository.findById(userId);
    }

    public UserEntity createUser(CreateUserDTO dto) {

        var entity = new UserEntity();
        entity.setName(dto.name());
        entity.setAge(dto.age());
        entity.setCreatedAt(LocalDateTime.now());

        return userRepository.save(entity);
    }

    public Optional<UserEntity> updateById(Long userId,
                                           UpdateUserDTO dto) {

        var user = userRepository.findById(userId);

        if (user.isPresent()) {

            updateFields(dto, user);

            userRepository.save(user.get());
        }

        return user;
    }

    private void updateFields(UpdateUserDTO dto,
                              Optional<UserEntity> user) {
        if (hasText(dto.name())) {
            user.get().setName(dto.name());
        }

        if (!isNull(dto.age())) {
            user.get().setAge(dto.age());
        }
    }

    public boolean deleteById(Long userId) {

        var exists = userRepository.existsById(userId);

        if (exists) {
            userRepository.deleteById(userId);
        }

        return exists;

    }
}
