package clone.gozik.dto;

import clone.gozik.entity.MemberRoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberDto {
    @Getter
    public static class JoinRequestDto{
        private String email;
        private String password;
        private String nickName;
        private MemberRoleEnum role;
    }
@Getter
    public static class loginRequestDto{
        private String email;
        private String password;
    }

    @Getter
    @NoArgsConstructor
    public static class KakaoMemberInfoDto {
        private Long id;
        private String email;
        public KakaoMemberInfoDto(Long id, String email) {
            this.id = id;
            this.email = email;
        }
    }
}
