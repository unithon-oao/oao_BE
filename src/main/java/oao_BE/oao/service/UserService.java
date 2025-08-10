package oao_BE.oao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oao_BE.oao.domain.User;
import oao_BE.oao.dto.response.GetUserResponse;
import oao_BE.oao.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 사용자 정보 가져오기
    public ResponseEntity<?> getUserInfo(Long userId) {
        return userRepository.findByUserId(userId)
                .map(user -> ResponseEntity.ok(GetUserResponse.from(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
