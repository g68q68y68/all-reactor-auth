package com.reactorAuth.controller;

import com.reactorAuth.dto.Result;
import com.reactorAuth.entity.User;
import com.reactorAuth.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserController extends BaseController<User, UserService> {

    public UserController(UserService userService) {
        super(userService);
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Mono<Result<User>> findById(@PathVariable Long id) {
        return super.findById(id);
    }
}
