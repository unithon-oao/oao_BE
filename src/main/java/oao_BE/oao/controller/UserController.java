package oao_BE.oao.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oao_BE.oao.domain.User;
import oao_BE.oao.dto.response.GetUserResponse;
import oao_BE.oao.repository.UserRepository;
import oao_BE.oao.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    // 사용자 정보 가져오기
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long userId) {
        return userService.getUserInfo(userId);
    }

}
