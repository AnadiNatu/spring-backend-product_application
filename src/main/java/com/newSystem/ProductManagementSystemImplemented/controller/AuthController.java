package com.newSystem.ProductManagementSystemImplemented.controller;


import com.newSystem.ProductManagementSystemImplemented.dto.authenticationDTO.*;
import com.newSystem.ProductManagementSystemImplemented.enitity.Users;
import com.newSystem.ProductManagementSystemImplemented.respository.UserRepository;
import com.newSystem.ProductManagementSystemImplemented.security.JwtUtil;
import com.newSystem.ProductManagementSystemImplemented.security.UserServiceImpl;
import com.newSystem.ProductManagementSystemImplemented.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signUpUser(@RequestBody SignUpDTO signUpDTO){

        if (authService.hasUserWithEmail(signUpDTO.getEmail())){

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
        UserDTO user = authService.signupUser(signUpDTO);

        if (user == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Not Created");
        }else return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login (@RequestBody AuthenticationRequest authenticationRequest){

        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail() , authenticationRequest.getPassword()));
        }catch (BadCredentialsException ex){
            throw new BadCredentialsException("Incorrect username or Password");
        }

        UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getEmail());
        Optional<Users> optionalUser = userRepository.findByEmail(authenticationRequest.getEmail());
        String jwtToken = jwtUtil.generateToken(optionalUser.get().getUsername());
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        if (optionalUser.isPresent()) {
            authenticationResponse.setJwt(jwtToken);
            authenticationResponse.setUserId(optionalUser.get().getId());
            authenticationResponse.setFullName(optionalUser.get().getFname() + optionalUser.get().getLname());
            authenticationResponse.setUserRoles(optionalUser.get().getUserRoles());
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(authenticationResponse);
    }

    @PostMapping("/upload-profile-picture/{userId}")
    public ResponseEntity<?> uploadProfilePicture(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {

        String filename = authService.updateProfilePicture(userId, file);
        return ResponseEntity.ok("Profile picture uploaded: " + filename);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest){

        authService.sendResetToken(forgotPasswordRequest.getEmail());

        return ResponseEntity.ok("Reset token sent to email");

    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){

        authService.resetPassword(
                resetPasswordRequest.getEmail(),
                resetPasswordRequest.getToken(),
                resetPasswordRequest.getNewPassword()
        );

        return ResponseEntity.ok("Password reset successful");

    }





}
