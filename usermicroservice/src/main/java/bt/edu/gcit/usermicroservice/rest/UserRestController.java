package bt.edu.gcit.usermicroservice.rest;

import bt.edu.gcit.usermicroservice.entity.User;
import bt.edu.gcit.usermicroservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import bt.edu.gcit.usermicroservice.entity.Role;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private UserService userService;
    
    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }
 
    @PostMapping(value = "", consumes = "multipart/form-data")
    public User save(
        @RequestPart("firstName") @Valid @NotNull String firstName,
        @RequestPart("lastName") @Valid @NotNull String lastName,
        @RequestPart("email") @Valid @NotNull String email,
        @RequestPart("password") @Valid @NotNull String password,
        @RequestPart("photo") @Valid @NotNull MultipartFile photo,
        @RequestPart("roles") @Valid @NotNull String rolesJson) {
    
        try {
            // Create a new User object
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(password);
    
            // Parse the roles JSON string into a Set<Role>
            ObjectMapper objectMapper = new ObjectMapper();
            Set<Role> roles = objectMapper.readValue(rolesJson, new TypeReference<Set<Role>>() {});
            user.setRoles(roles);
    
            System.out.println("Uploading photo");
            // Save the user and get the ID
            User savedUser = userService.save(user);
    
            // Upload the user photo
            userService.uploadUserPhoto(savedUser.getId().intValue(), photo);
    
            // Return the saved user
            return savedUser;
        } catch (IOException e) {
            // Handle the exception
            throw new RuntimeException("Error while uploading photo", e);
        }
    }
    
   
   
    // @PostMapping("/users")
    // public User save(@RequestBody User user) {
    // return userService.save(user);
    //

    // @PostMapping
    // public User save(@RequestBody User user) {
    //     return userService.save(user);
    // }

    // GET - Fetch All Users
    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    // // GET - Fetch User by ID
    // @GetMapping("/{id}")
    // public User findById(@PathVariable Long id) {
    //     return userService.findById(id);
    // }

    // // DELETE - Remove User
    // @DeleteMapping("/{id}")
    // public void deleteById(@PathVariable Long id) {
    //     userService.deleteById(id);
    // }

    // @PutMapping("/{id}")
    // public ResponseEntity<User> updateUser(
    //         @PathVariable Long id,
    //         @RequestBody User userDetails) {
        
    //     // Ensure the ID in path matches the ID in request body
    //     if (!id.equals(userDetails.getId())) {
    //         return ResponseEntity.badRequest().build();
    //     }

    //     User updatedUser = userService.update(userDetails);
    //     return ResponseEntity.ok(updatedUser);
    // }

    @GetMapping("/checkDuplicateEmail")
    public ResponseEntity<Boolean> checkDuplicateEmail(@RequestParam String email) {
        boolean isDuplicate = userService.isEmailDuplicate(email);
        return ResponseEntity.ok(isDuplicate);
    }   

    /**
 * Updates a user with the given ID using the provided User object.
 *
 * @param id the ID of the user to be updated
 * @param updatedUser the User object containing the updated information
 * @return the updated User object
 */
 @PutMapping("/{id}")
 public User updateUser(@PathVariable int id, @RequestBody User updatedUser) {
 return userService.updateUser(id, updatedUser);
 }

 @DeleteMapping("/{id}")
 public void deleteUser(@PathVariable int id) {
 userService.deleteById(id);
 }

  /**
 * Update the enabled status of a user with the specified id
 *
 * @param id The ID of the user to update
 * @param enabled The new enabled status
 * @return OK if the update was successful
 */
@PutMapping("/{id}/enabled")
public ResponseEntity<?> updateUserEnabledStatus(
@PathVariable int id, @RequestBody Map<String, Boolean> requestBody) {
Boolean enabled = requestBody.get("enabled");
userService.updateUserEnabledStatus(id, enabled);
System.out.println("User enabled status updated successfully");
return ResponseEntity.ok().build();
}

}