package com.auth.authmodule.controller;

import com.auth.authmodule.config.JwtTokenUtil;
import com.auth.authmodule.model.User;
import com.auth.authmodule.request.JWTRequest;
import com.auth.authmodule.request.UserDTO;
import com.auth.authmodule.request.UserRequest;
import com.auth.authmodule.response.JWTResponse;
import com.auth.authmodule.response.UserListResponse;
import com.auth.authmodule.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    /**
     * API to be used for login for the user
     */
    @PostMapping(value = "/login")
    public ResponseEntity<JWTResponse> createAuthenticationToken(@RequestBody JWTRequest authenticationRequest) throws Exception {
        final String token = getJwtToken(authenticationRequest.getEmail(), authenticationRequest.getPassword());
        return ResponseEntity.ok(new JWTResponse(token));
    }

    private String getJwtToken(String email, String password) {
        Authentication authentication = authenticateAndGetAuthentication(email, password);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtTokenUtil.generateToken(userDetails);
    }

    /**
     * API to get users to signup
     * Returns the JWT token if the signup was successful
     */
    @PostMapping(value = "/signup")
    public ResponseEntity<JWTResponse> saveUser(@RequestBody UserDTO userDto) {
        User user = userService.save(UserUserDtoConverter.mapUserDtoToUser(userDto));
        if (user != null) {
            final String token = getJwtToken(user.getEmail(), userDto.getPassword());
            return ResponseEntity.ok(new JWTResponse(token));
        }
        throw new BadCredentialsException("User credentials are not valid");
    }

    /**
     * Gets all the users in the system
     */
    @GetMapping(value = "/users")
    public ResponseEntity<UserListResponse> getUsers() {
        return ResponseEntity.ok(UserListResponse.builder()
                .users(userService.getAllUsers().stream().map(user -> UserDTO.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail()).build())
                        .collect(Collectors.toUnmodifiableList()))
                .build());
    }

    /**
     * API to be used for updates the user details
     */
    @PutMapping(value = "/user")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserRequest userRequest) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDTO updatedUserDto = UserUserDtoConverter.mapUserToUserDto(userService.updateUserByEmail(userDetails.getUsername(), userRequest));
        return ResponseEntity.ok(updatedUserDto);
    }

    private Authentication authenticateAndGetAuthentication(String userName, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userName, password,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }
}