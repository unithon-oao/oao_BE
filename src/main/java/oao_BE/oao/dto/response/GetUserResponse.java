package oao_BE.oao.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import oao_BE.oao.domain.User;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetUserResponse {
    private Long userId;
    private String name;
    private String introduction;
    private String profile;
    private String email;
    private String portfolio;
    private String field;

    public static GetUserResponse from(User u) {
        return GetUserResponse.builder()
                .userId(u.getUserId())
                .name(u.getName())
                .introduction(u.getIntroduction())
                .profile(u.getProfile())
                .email(u.getEmail())
                .portfolio(u.getPortfolio())
                .field(u.getField())
                .build();
    }
}