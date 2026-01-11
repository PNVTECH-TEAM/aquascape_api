package pnvteck.all_in_one.user;

import lombok.RequiredArgsConstructor;
import pnvteck.all_in_one.user.dto.UserResponse;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

        private final UserRepository userRepository;

        @Override
        public UserResponse getUserById(Long id) {
                // 1. Tìm user trong DB
                UserEntity user = userRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // 2. Map Entity -> DTO
                return UserResponse.builder()
                                .userId(user.getId())
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .fullName(user.getFullName())
                                .build();
        }

        @Override
        public List<UserResponse> getAllUsers() {
                List<UserEntity> users = userRepository.findAll();

                return users.stream()
                                .map(user -> UserResponse.builder()
                                                .userId(user.getId())
                                                .username(user.getUsername())
                                                .email(user.getEmail())
                                                .fullName(user.getFullName())
                                                .build())
                                .collect(Collectors.toList());
        }
}