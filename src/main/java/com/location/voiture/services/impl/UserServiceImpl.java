package com.location.voiture.services.impl;


import com.location.voiture.dao.UserDao;
import com.location.voiture.domain.*;
import com.location.voiture.enumeration.Role;
import com.location.voiture.models.User;
import com.location.voiture.models.UserPrincipal;
import com.location.voiture.services.IUserService;
import com.location.voiture.services.LoginAttemptService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.location.voiture.constant.FileConstant.*;
import static com.location.voiture.constant.UserServiceConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.springframework.util.MimeTypeUtils.*;


@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements IUserService, UserDetailsService {


    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    UserDao userDao;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    LoginAttemptService loginAttemptService;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userDao.findByUsername(userName);
        if (user == null) {
            LOGGER.error(NO_USER_FOUND_BY_USERNAME + userName);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + userName);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userDao.save(user);
            LOGGER.info(FOUND_USER_BY_USER_NAME + userName);
            return new UserPrincipal(user);
        }

    }

    @Override
    public void addAdminUser(){
       if (userDao.findByUsername("admin")!=null)
           return;
        User user =new User();
        user.setUserId(generateUserId());
        String password ="admin";
        user.setFirstName("admin");
        user.setLastName("admin");
        user.setUsername("admin");
        user.setEmail("admin@admin.com");
        user.setJoinDate(new Date());
        user.setPassword(encodePassword(password));
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(Role.ROLE_ADMIN.name());
        user.setAuthorities(Role.ROLE_ADMIN.getAuthorities());
        userDao.save(user);
    }

    private void validateLoginAttempt(User user) {
        if (user.isNotLocked()) {
            if (loginAttemptService.hasExceededMaxOfAttempt(user.getUsername())) {
                user.setNotLocked(false);
            } else {
                user.setNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException {

        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodePassword(password));
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        // userDao.save(user);
        LOGGER.info("user password : " + password);
        return user;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public User addNewUser(String firstName, String lastName, String username, String email, String role, String password, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        validateNewUsernameAndEmail(EMPTY, username, email);
        User user = new User();
       // String password = generatePassword();
        user.setUserId(generateUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setJoinDate(new Date());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodePassword(password));
        //System.out.println(password);
        user.setActive(isActive);
        user.setNotLocked(isNonLocked);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userDao.save(user);
        saveProfileImage(user, profileImage);
        LOGGER.info("New user password: " + password);
        return user;

    }


    @Override
    public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        User currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNonLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userDao.save(currentUser);
        saveProfileImage(currentUser, profileImage);
        return currentUser;

    }

    @Override
    public void deleteUser(String username) throws IOException {
        User user = userDao.findByUsername(username);
        Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
        FileUtils.deleteDirectory(userFolder.toFile());
        userDao.deleteById(user.getId());
    }

    @Override
    public void resetPassword(User user) throws EmailNotFoundException {
        User byUsername = userDao.findByUsername(user.getUsername());
        if (byUsername == null) {
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + user.getUsername());
        } else {
           // String newPassword = generatePassword();
            byUsername.setPassword(encodePassword(user.getPassword()));
            userDao.save(byUsername);
            LOGGER.info("resit password for " + user.getUsername() + " password is: " + user.getPassword());

            //  TODO
            //emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
        }
    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        User user = validateNewUsernameAndEmail(username, EMPTY, EMPTY);
        saveProfileImage(user, profileImage);
        return user;


    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());

    }

    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException, NotAnImageFileException {
        if (profileImage != null) {
            if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileImage.getContentType())) {
                throw new NotAnImageFileException(profileImage.getOriginalFilename() + " is not an image file, please upload an image");
            }
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
            userDao.save(user);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH
                + username + DOT + JPG_EXTENSION).toUriString();
    }


    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(6);
    }

    private String generateUserId() {
        String userId1 = RandomStringUtils.randomAlphabetic(2).toUpperCase();
        String userId2 = RandomStringUtils.randomNumeric(3);
        return userId1 + userId2;
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UsernameExistException, EmailExistException, UserNotFoundException {
        User userByEmail = findUserByEmail(newEmail);
        User userByUsername = findUserByUsername(newUsername);

        if (StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            if (userByUsername != null && !currentUser.getId().equals(userByUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXIST);
            }

            if (userByEmail != null && !currentUser.getId().equals(userByEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {

            if (userByUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXIST);
            }
            if (userByEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    @Override
    public List<User> getUsers() {
        return userDao.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userDao.findByEmail(email);
    }


}
