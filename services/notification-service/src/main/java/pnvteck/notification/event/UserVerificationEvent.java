package pnvteck.notification.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVerificationEvent {
    private Long userId;
    private String email;
    private String fullName;
    private String eventType;
}
