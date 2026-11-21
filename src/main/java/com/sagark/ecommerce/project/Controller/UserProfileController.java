package com.sagark.ecommerce.project.Controller;

import com.sagark.ecommerce.project.paylod.ChangePasswordRequest;
import com.sagark.ecommerce.project.paylod.UserProfileResponse;
import com.sagark.ecommerce.project.paylod.UserProfileUpdateRequest;
import com.sagark.ecommerce.project.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService){
        this.userProfileService = userProfileService;
    }

    // Get the currently authenticated users profile
    @GetMapping("/user/profile")
    public ResponseEntity<UserProfileResponse> getMyProfile(){
        UserProfileResponse profile = userProfileService.getMyProfile();

        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    // Update the currently authenticated user's profile
    @PutMapping("/user/profile")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @Valid @RequestBody UserProfileUpdateRequest request) {

        UserProfileResponse updatedProfile =
                userProfileService.updateMyProfile(request);

        return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
    }

    // Change the currently authenticated user's password
    @PutMapping("/user/profile/change-password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
            ){
        userProfileService.changePassword(request);
        return ResponseEntity.ok("Password changed successfully");
    }
}
