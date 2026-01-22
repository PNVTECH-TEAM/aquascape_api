package pnvteck.user.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pnvteck.common.constant.AccountStatus;
import pnvteck.common.constant.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInternalResponse {
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String passwordHash;
    private Role role;
    private AccountStatus status;
}
