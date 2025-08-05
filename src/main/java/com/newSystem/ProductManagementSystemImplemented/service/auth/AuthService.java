package com.newSystem.ProductManagementSystemImplemented.service.auth;

import com.newSystem.ProductManagementSystemImplemented.dto.authenticationDTO.SignUpDTO;
import com.newSystem.ProductManagementSystemImplemented.dto.authenticationDTO.UserDTO;
import com.newSystem.ProductManagementSystemImplemented.enitity.Users;
import com.newSystem.ProductManagementSystemImplemented.enums.UserRoles;
import com.newSystem.ProductManagementSystemImplemented.exception.ExpiredTokenException;
import com.newSystem.ProductManagementSystemImplemented.exception.PasswordResetTokenException;
import com.newSystem.ProductManagementSystemImplemented.exception.UserWithEmailNotFoundException;
import com.newSystem.ProductManagementSystemImplemented.mapper.AppMapper;
import com.newSystem.ProductManagementSystemImplemented.respository.UserRepository;
import com.newSystem.ProductManagementSystemImplemented.security.JwtUtil;
import com.newSystem.ProductManagementSystemImplemented.security.UserServiceImpl;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;


@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AppMapper mapper;

    @Value("${file.user-upload-dir}")
    private String uploadDir;

    @PostConstruct
    public void  createAdmin(){

        try {
            Optional<Users> optionalUsers = userRepository.findByUserRoles(UserRoles.ADMIN);

            if (optionalUsers.isEmpty()) {

                Users users = new Users();

                users.setFname("Admin F");
                users.setLname("Admin L");
                users.setEmail("admin@test.com");
                users.setPassword(new BCryptPasswordEncoder().encode("password"));
                users.setPhoneNumber("0000000000");
                users.setUserRoles(UserRoles.ADMIN);
                users.setProductOrders(null);
                users.setProfilePicture(null);
                userRepository.save(users);

                System.out.println("Admin created successfully");
            } else {
                System.out.println("Admin already created");
            }
        }catch(Exception ex){
            System.err.println("Failed to initialize admin: " + ex.getMessage());
        }
    }

    public UserDTO signupUser(SignUpDTO signUpDTO){
        try {
            Users users = new Users();

            users.setFname(signUpDTO.getFname());
            users.setLname(signUpDTO.getLname());
            users.setEmail(signUpDTO.getEmail());
            users.setPassword(new BCryptPasswordEncoder().encode(signUpDTO.getPassword()));
            users.setPhoneNumber(signUpDTO.getPhoneNumber());
            users.setUserRoles(UserRoles.USER);
            users.setProfilePicture(null);
            Users createdUsers = userRepository.save(users);

            return mapper.toUserDTO(createdUsers);
        }catch(Exception ex){
            throw new UsernameNotFoundException("Failed to signup user: " + ex.getMessage());
        }
    }

    public String updateProfilePicture(Long userId , MultipartFile file){
        try{
            Users users = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir , filename);

            Files.createDirectories(filePath.getParent());
            Files.write(filePath , file.getBytes());

            users.setProfilePicture(filename);
            userRepository.save(users);

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile picture: " + e.getMessage());
        }
    }

    public boolean hasUserWithEmail(String username){
        try {
            return userRepository.findByEmail(username).isPresent();
        }catch(Exception ex){
            throw new UserWithEmailNotFoundException("Failed to check user existence by email: " + ex.getMessage());
        }
    }

    public void sendResetToken(String email){
        try {
            Users users = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));
            String resetToken = jwtUtil.generateToken(email);
            users.setResetToken(resetToken);
            userRepository.save(users);

            String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;
            emailService.sendEmail(email, "Password Reset", "Click the reset your password : " + resetLink);
        }catch(UsernameNotFoundException ex){
            throw ex;
        }catch (Exception ex){
            throw new PasswordResetTokenException("Failed to send reset token: " + ex.getMessage());
        }
    }

    public boolean validateResetToken(String token , String email){
        try {
            String extractedEmail = jwtUtil.extractUsername(token);

            if (!extractedEmail.equals(email)) {
                return false;
            }

            UserDetails userDetails = userService.loadUserByUsername(email);

            return jwtUtil.isTokenValid(token, userDetails);
        }catch(Exception ex){
            throw new PasswordResetTokenException("Failed to reset password: " + ex.getMessage());
        }
    }

    public String resetPassword(String email , String token , String newPassword){
        try {
            if (!validateResetToken(token, email)) {
                throw new ExpiredTokenException("Invalid or expired token");
            }

            Users users = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

            users.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            userRepository.save(users);

            return "Password reset successful";
        }catch (IllegalArgumentException | UsernameNotFoundException ex){
            throw ex;
        }catch (Exception ex){
            throw new PasswordResetTokenException("Failed to reset password: " + ex.getMessage());
        }
    }
}
