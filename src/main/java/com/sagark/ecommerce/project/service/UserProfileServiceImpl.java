
package com.sagark.ecommerce.project.service;

import com.sagark.ecommerce.project.exceptions.APIException;
import com.sagark.ecommerce.project.model.AppRole;
import com.sagark.ecommerce.project.model.Role;
import com.sagark.ecommerce.project.model.User;
import com.sagark.ecommerce.project.paylod.ChangePasswordRequest;
import com.sagark.ecommerce.project.paylod.UserProfileResponse;
import com.sagark.ecommerce.project.paylod.UserProfileUpdateRequest;
import com.sagark.ecommerce.project.repositories.UserRepository;
import com.sagark.ecommerce.project.util.AuthUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileServiceImpl(
            AuthUtil authUtil,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.authUtil = authUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Get the authenticated user's profile
    @Override
    public UserProfileResponse getMyProfile() {

        User user = authUtil.loggedInUser();

        return mapToProfileResponse(user);
    }

    // Update editable profile information
    @Override
    public UserProfileResponse updateMyProfile(UserProfileUpdateRequest request) {

        // Get the currently authenticated user
        User user = authUtil.loggedInUser();

        // Update only editable fields
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());

        // Save updated user
        User updatedUser = userRepository.save(user);

        // Return updated profile
        return mapToProfileResponse(updatedUser);
    }

    // Convert User entity into profile response DTO
    private UserProfileResponse mapToProfileResponse(User user) {

        List<AppRole> roles = user.getRoles()
                .stream()
                .map(Role::getRoleName)
                .toList();

        UserProfileResponse response = new UserProfileResponse();

        response.setUserId(user.getUserId());
        response.setUsername(user.getUserName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setGender(user.getGender());
        response.setRoles(roles);
        response.setCreatedAt(user.getCreatedAt());

        return response;
    }

    // Change authenticated user's password
    @Override
    public void changePassword(ChangePasswordRequest request) {

        User user = authUtil.loggedInUser();

        // Verify current password
        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )) {
            throw new APIException("Current password is incorrect");
        }

        // Prevent reusing the current password
        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword()
        )) {
            throw new APIException(
                    "New password must be different from current password"
            );
        }

        // Encode and save new password
        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);
    }
}
