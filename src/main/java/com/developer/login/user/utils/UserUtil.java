package com.developer.login.user.utils;

import com.developer.login.common.exception.NoExistUserException;
import com.developer.login.user.domain.User;
import com.developer.login.user.dto.CustomUserDetails;
import com.developer.login.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class UserUtil {
    public static User ConvertUser(UserRepository userRepository, CustomUserDetails userDetails) {
        Optional<User> OptionalUser = userRepository.findByEmail(userDetails.getUsername());
        return OptionalUser.orElseThrow(NoExistUserException::new);
    }

    public static CustomUserDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }
}
