package clone.gozik.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private MemberRoleEnum role;

    private Long kakaoId;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Favorites> favorites;

    @Builder
    public Member(String email, String password, String nickName, MemberRoleEnum role) {
        this.email = email;
        this.role = role;
        this.nickName = nickName;
        this.password = password;
    }

    @Builder
    public Member(String nickName, Long kakaoId,String email, String password, MemberRoleEnum role) {
        this.nickName = nickName;
        this.kakaoId = kakaoId;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Member kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }
}
