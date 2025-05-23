package com.developer.login.user.service;

import com.developer.login.common.exception.EmailAlreadyExistException;
import com.developer.login.common.exception.NoExistUserException;
import com.developer.login.common.exception.NotVerifyUserException;
import com.developer.login.config.auth.JwtTokenProvider;
import com.developer.login.config.email.EmailService;
import com.developer.login.user.domain.Role;
import com.developer.login.user.domain.User;
import com.developer.login.user.dto.*;
import com.developer.login.user.repository.UserRepository;
import com.developer.login.user.utils.UserUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final DefaultOAuth2UserService oAuth2UserService;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        User user = userRepository.findByEmail(oAuth2User.getAttribute("email"))
                .map(entity -> entity.updatePicture(oAuth2User.getAttribute("picture")))
                .orElseGet(() -> User.builder()
                        .name(oAuth2User.getAttribute("name"))
                        .email(oAuth2User.getAttribute("email"))
                        .picture(oAuth2User.getAttribute("picture"))
                        .role(Role.FREE)
                        .isOAuthLogin(true)
                        .isVerified(true)
                        .privacyAgreed(true)
                        .privacyAgreedAt(LocalDateTime.now())
                        .uuid(UUID.randomUUID().toString())
                        .build()
                );
        userRepository.save(user);

        OAuthAttributes attributes = OAuthAttributes.of(
                registrationId,
                userNameAttributeName,
                oAuth2User.getAttributes()
        );

        return new OAuth2UserImpl(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey(),
                user
        );
    }

    public void signup(SignupRequestDto signupDto) throws MessagingException {
        if (userRepository.findByEmail(signupDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException();
        }

        String verificationToken = UUID.randomUUID().toString();
        User user = User.builder()
                .email(signupDto.getEmail())
                .password(encodePassword(signupDto.getPassword()))
                .name(signupDto.getName())
                .privacyAgreed(true)
                .privacyAgreedAt(LocalDateTime.now())
                .isOAuthLogin(false)
                .role(Role.FREE)
                .isVerified(false)
                .verificationToken(verificationToken)
                .uuid(UUID.randomUUID().toString())
                .build();

        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);
    }

    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("No exist token"));

        if (user.isVerified()) {
            return;
        }

        user.SuccessVerified();
    }

    public User login(LoginRequestDto loginRequestDto) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequestDto.getEmail());
        User user = userOptional.orElseThrow(NoExistUserException::new);

        if (!new BCryptPasswordEncoder().matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new NoExistUserException();
        }
        if (!user.isVerified()) {
            throw new NotVerifyUserException();
        }

        return user;
    }

    private String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElseThrow(NoExistUserException::new);
    }

    public UserUuidResponseDto uuid(CustomUserDetails userDetails) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);
        return new UserUuidResponseDto(user.getUuid());
    }

    public ResponseCookie tokenReset(CustomUserDetails userDetails) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);
        return jwtTokenProvider.getJwtCookie(user);
    }
}
