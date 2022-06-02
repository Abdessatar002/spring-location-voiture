package com.location.voiture.resources;


import com.location.voiture.domain.*;
import com.location.voiture.enumeration.Role;
import com.location.voiture.models.HttpResponse;
import com.location.voiture.models.User;
import com.location.voiture.models.UserPrincipal;
import com.location.voiture.services.IUserService;
import com.location.voiture.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;


import static com.location.voiture.constant.FileConstant.*;
import static com.location.voiture.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;

@CrossOrigin
@RestController
@RequestMapping(value = {"/","/user"})
public class UserResources extends ExceptionHandling {
    public static final String EMAIL_SENT = "An email whit new password sent to: ";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";
    @Autowired
   private IUserService userService;
    @Autowired
   private JWTTokenProvider tokenProvider;
    @Autowired
   private AuthenticationManager authenticationManager;


    @PostConstruct
    private void init(){
        userService.addAdminUser();
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        authenticate(user.getUsername() , user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders httpHeaders= getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser,httpHeaders, OK);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
                           @RequestParam("lastName") String lastName,
                           @RequestParam("username") String username,
                           @RequestParam("email") String email,
                           @RequestParam("role") String role,
                           @RequestParam("password") String password,
                           @RequestParam("isActive") String isActive,
                           @RequestParam("isNonLocked") String isNonLocked,
                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, NotAnImageFileException, IOException {

        User newUser = userService.addNewUser(firstName, lastName, username,email, role,password, Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(newUser, OK);

    }
    @PostMapping("/update")
    public ResponseEntity<User> update(@RequestParam("currentUsername") String currentUsername,
                                       @RequestParam("firstName") String firstName,
                                       @RequestParam("lastName") String lastName,
                                       @RequestParam("username") String username,
                                       @RequestParam("email") String email,
                                       @RequestParam("role") String role,
                                       @RequestParam("isActive") String isActive,
                                       @RequestParam("isNonLocked") String isNonLocked,
                                       @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        User updatedUser = userService.updateUser(currentUsername, firstName, lastName, username,email, role, Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(updatedUser, OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username){
        User user = userService.findUserByUsername(username);
        return  new ResponseEntity<>(user,OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users,OK);
    }
    @PostMapping("/resetPassword")
    public ResponseEntity<HttpResponse> resitPassword(@RequestBody User user) throws EmailNotFoundException {
            userService.resetPassword(user);
            return response(OK, EMAIL_SENT);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable String username) throws IOException {
        userService.deleteUser(username);
        return response(OK, USER_DELETED_SUCCESSFULLY);
    }



    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username, @RequestParam(value = "profileImage") MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        User user = userService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER+ username+ FORWARD_SLASH+ fileName));
    }

    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()){
            int byteRead;
            byte[] chunk = new byte[1024];
            while((byteRead = inputStream.read(chunk)) > 0){
                byteArrayInputStream.write(chunk,0,byteRead);
            }
        }
        return byteArrayInputStream.toByteArray();
    }


    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }


    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWT_TOKEN_HEADER,tokenProvider.generateJwtToken(userPrincipal));
        return httpHeaders;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
    }
}
