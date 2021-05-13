package io.spring.api;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.spring.api.exception.InvalidRequestException;
import io.spring.application.UserQueryService;
import io.spring.application.data.UserData;
import io.spring.application.data.UserWithToken;
import io.spring.core.service.JwtService;
import io.spring.core.user.EncryptService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
public class UsersApi {
    private final UserRepository userRepository;
    private final UserQueryService userQueryService;
    private final String defaultImage;
    private final EncryptService encryptService;
    private final JwtService jwtService;

    @Autowired
    public UsersApi(UserRepository userRepository,
                    UserQueryService userQueryService,
                    EncryptService encryptService,
                    @Value("${image.default}") String defaultImage,
                    JwtService jwtService) {
        this.userRepository = userRepository;
        this.userQueryService = userQueryService;
        this.encryptService = encryptService;
        this.defaultImage = defaultImage;
        this.jwtService = jwtService;
    }

    @PostMapping("users")
    public ResponseEntity<Map<String, UserWithToken>> createUser(@Valid @RequestBody RegisterParam registerParam,
                                                                 BindingResult bindingResult) {
        checkInput(registerParam, bindingResult);
        final var user = new User(
            registerParam.getEmail(),
            registerParam.getUsername(),
            encryptService.encrypt(registerParam.getPassword()),
            "",
            defaultImage);
        userRepository.save(user);
        UserData userData = userQueryService.findById(user.getId()).orElseThrow(InternalError::new);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userResponse(new UserWithToken(userData, jwtService.toToken(user))));
    }

    private void checkInput(@Valid @RequestBody RegisterParam registerParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
        if (userRepository.findByUsername(registerParam.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "DUPLICATED", "duplicated username");
        }
        if (userRepository.findByEmail(registerParam.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "DUPLICATED", "duplicated email");
        }
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
    }

    @PostMapping("/users/login")
    public ResponseEntity<Map<String, UserWithToken>> userLogin(@Valid @RequestBody LoginParam loginParam,
                                                                BindingResult bindingResult) {
        Optional<User> optional = userRepository.findByEmail(loginParam.getEmail());
        if (optional.isPresent() && encryptService.check(loginParam.getPassword(), optional.get().getPassword())) {
            final UserData userData = userQueryService.findById(optional.get().getId()).orElseThrow(InternalError::new);
            return ResponseEntity.ok(userResponse(new UserWithToken(userData, jwtService.toToken(optional.get()))));
        } else {
            bindingResult.rejectValue("password", "INVALID", "invalid email or password");
            throw new InvalidRequestException(bindingResult);
        }
    }

    private Map<String, UserWithToken> userResponse(UserWithToken userWithToken) {
        return Collections.singletonMap("user", userWithToken);
    }
}

@Getter
@JsonRootName("user")
@NoArgsConstructor
class LoginParam {
    @NotBlank(message = "can't be empty")
    @Email(message = "should be an email")
    private String email;
    @NotBlank(message = "can't be empty")
    private String password;
}

@Getter
@JsonRootName("user")
@NoArgsConstructor
class RegisterParam {
    @NotBlank(message = "can't be empty")
    @Email(message = "should be an email")
    private String email;
    @NotBlank(message = "can't be empty")
    private String username;
    @NotBlank(message = "can't be empty")
    private String password;
}
