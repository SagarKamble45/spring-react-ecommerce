
package com.sagark.ecommerce.project.paylod;

import com.sagark.ecommerce.project.model.AppRole;
import com.sagark.ecommerce.project.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long userId;

    private String username;

    private String email;

    private String phone;

    private Gender gender;

    private List<AppRole> roles;

    private LocalDateTime createdAt;
}
