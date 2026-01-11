package pnvteck.all_in_one.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestLogin {
    private String username;
    private String password;
}
