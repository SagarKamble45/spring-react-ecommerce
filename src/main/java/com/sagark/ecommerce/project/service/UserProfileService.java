package com.sagark.ecommerce.project.service;

import com.sagark.ecommerce.project.paylod.ChangePasswordRequest;
import com.sagark.ecommerce.project.paylod.UserProfileResponse;
import com.sagark.ecommerce.project.paylod.UserProfileUpdateRequest;
import jakarta.validation.Valid;

public interface UserProfileService {
    // Get the currently authenticated user's profile
    UserProfileResponse getMyProfile();

    // Update the currently authenticated user's profile
    UserProfileResponse updateMyProfile(UserProfileUpdateRequest request);

    // Change the currently authenticated user's password
    void changePassword(ChangePasswordRequest request);
}
