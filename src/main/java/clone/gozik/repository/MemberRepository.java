package clone.gozik.repository;

import clone.gozik.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByKakaoId(Long id);

    Optional<Member> findByNickName(String nickName);

}
