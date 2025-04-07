package bt.edu.gcit.usermicroservice.service;

import bt.edu.gcit.usermicroservice.dao.UserDAO;
import bt.edu.gcit.usermicroservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import bt.edu.gcit.usermicroservice.exception.UserNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.springframework.util.StringUtils;
import java.nio.file.Path;
import bt.edu.gcit.usermicroservice.exception.FileSizeException;
import java.nio.file.Paths;
import java.util.List;

@Service 
//tells sprinf its a bean class and also treat it as a service
//code readabilty 
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    private final BCryptPasswordEncoder passwordEncoder;
    private final String uploadDir = "src/main/resources/static/images";

    @Autowired 
    @Lazy
    //not necessary here works without autowire too bc it auto injects in this new ver of java
    public UserServiceImpl(UserDAO userDAO, BCryptPasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override 
    //optional but recommended bc while compiling java will consider it as a new method
    @Transactional 
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userDAO.save(user);
    }

    // @Override
    // @Transactional(readOnly = true)
    // public User findById(Long id) {
    //     return userDAO.findById(id);
    // }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userDAO.findAll();
    }

    // @Override
    // @Transactional
    // public void deleteById(Long id) {
    //     userDAO.deleteById(id);
    // }

    // @Override
    // @Transactional
    // public User update(User user) {
    //     // First check if user exists
    //     User existingUser = userDAO.findById(user.getId());
    //     if (existingUser == null) {
    //         throw new RuntimeException("User not found with id: " + user.getId());
    //     }
    //     return userDAO.update(user);
    // }

    @Override
    public boolean isEmailDuplicate(String email) {
    User user = userDAO.findByEmail(email);
    return user != null;
    }

    @Override
    public User findByID(int theId) {
    return userDAO.findByID(theId);
    }
   
    @Transactional
    @Override
    public User updateUser(int id, User updatedUser) {
    // First, find the user by ID
    User existingUser = userDAO.findByID(id);
   
    // If the user doesn't exist, throw UserNotFoundException
    if (existingUser == null) {
    throw new UserNotFoundException("User not found with id: " + id);
    }
   
    // Update the existing user with the data from updatedUser
    existingUser.setFirstName(updatedUser.getFirstName());
    existingUser.setLastName(updatedUser.getLastName());
    existingUser.setEmail(updatedUser.getEmail());
   
    // Check if the password has changed. If it has, encode the new password before saving.
    if (!existingUser.getPassword().equals(updatedUser.getPassword())) {
   
   existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
    }
   
    existingUser.setRoles(updatedUser.getRoles());
   
    // Save the updated user and return it
    return userDAO.save(existingUser);
    }

    @Transactional
     @Override
     public void deleteById(int theId) {
     userDAO.deleteById(theId);
     }    

     @Transactional
     @Override
     public void updateUserEnabledStatus(int id, boolean enabled) {
     userDAO.updateUserEnabledStatus(id, enabled);
     }

     @Transactional
 @Override
 public void uploadUserPhoto(int id, MultipartFile photo) throws IOException {
 User user = findByID(id);
 if (user == null) {
 throw new UserNotFoundException("User not found with id " + id);
 }
 if (photo.getSize() > 1024 * 1024) {
 throw new FileSizeException("File size must be < 1MB");
 }
 // String filename = StringUtils.cleanPath(photo.getOriginalFilename());
 // Path uploadPath = Paths.get(uploadDir, filename);
 // photo.transferTo(uploadPath);
 // user.setPhoto(filename);
 // save(user);
 String originalFilename = StringUtils.cleanPath(photo.getOriginalFilename());
 String filenameExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
 String filenameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf("."));
 String timestamp = String.valueOf(System.currentTimeMillis());
 // Append the timestamp to the filename
 String filename = filenameWithoutExtension + "_" + timestamp + "." + filenameExtension;

 Path uploadPath = Paths.get(uploadDir, filename);
 photo.transferTo(uploadPath);

 user.setPhoto(filename);
 save(user);
 }

}