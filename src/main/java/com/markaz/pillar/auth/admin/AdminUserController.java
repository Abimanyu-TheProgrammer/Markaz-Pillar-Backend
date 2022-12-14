package com.markaz.pillar.auth.admin;

import com.markaz.pillar.auth.admin.model.AuthUserDTO;
import com.markaz.pillar.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/admin/user")
@PreAuthorize("isAuthenticated() and hasAuthority('CRUD_USERS')")
public class AdminUserController {
    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public Page<AuthUserDTO> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int n) {
        return userRepository.findAllByRoleName("MEMBER", PageRequest.of(page, n))
                .map(AuthUserDTO::mapFrom);
    }

    @GetMapping(params = "username")
    public AuthUserDTO getUser(@RequestParam String username)  {
        return AuthUserDTO.mapFrom(
                userRepository.findByUsername(username)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not found!"))
        );
    }
}
