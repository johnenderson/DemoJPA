package tech.buildrun.demojpa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.buildrun.demojpa.controller.dto.ApiResponse;
import tech.buildrun.demojpa.controller.dto.CreateUserDTO;
import tech.buildrun.demojpa.controller.dto.PaginationResponse;
import tech.buildrun.demojpa.controller.dto.UpdateUserDTO;
import tech.buildrun.demojpa.entity.UserEntity;
import tech.buildrun.demojpa.service.UserService;

import java.net.URI;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserEntity>> listAll(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                           @RequestParam(name = "orderBy", defaultValue = "desc") String orderBy,
                                                           @RequestParam(name = "name", required = false) String name,
                                                           @RequestParam(name = "age", required = false) Long age) {

        var pageResponse = userService.findAll(page, pageSize, orderBy, name, age);

        return ResponseEntity.ok(new ApiResponse<>(
                pageResponse.getContent(),
                new PaginationResponse(pageResponse.getNumber(),
                        pageResponse.getSize(),
                        pageResponse.getTotalElements(),
                        pageResponse.getTotalPages()
                )
        ));
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<UserEntity> findById(@PathVariable("userId") Long userId) {
        var user = userService.findById(userId);

        return user.isPresent() ?
                ResponseEntity.ok(user.get()) :
                ResponseEntity.notFound().build();

    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody CreateUserDTO dto) {
        var user = userService.createUser(dto);

        return ResponseEntity.created(
                URI.create("/users/"
                        + user.getUserId())).build();
    }

    @PutMapping(path = "/{userId}")
    public ResponseEntity<Void> updateUser(@PathVariable("userId") Long userId,
                                           @RequestBody UpdateUserDTO dto) {
        var user = userService.updateById(userId, dto);

        return user.isPresent() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }

    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<Void> deleteById(@PathVariable("userId") Long userId) {

        var userDeleted = userService.deleteById(userId);

        return userDeleted ?
                ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }
}
