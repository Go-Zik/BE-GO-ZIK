package clone.gozik.service;

import clone.gozik.dto.MemberDto;
import clone.gozik.entity.Member;
import clone.gozik.entity.MemberRoleEnum;
import clone.gozik.jwt.JwtUtil;
import clone.gozik.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final RandomNicknameBox randomNicknameBox;


//    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(MemberDto.JoinRequestDto JoinRequest) {
        String email = JoinRequest.getEmail();
        String password = JoinRequest.getPassword();
        MemberRoleEnum role = JoinRequest.getRole();
        String nickName = "";


        if (role.equals(MemberRoleEnum.COMPANY)) {
            nickName = JoinRequest.getNickName();
            if (memberRepository.findByNickName(nickName).isPresent()) {
                throw new IllegalArgumentException("중복된 회사명이 있습니다");
            }
        } else  //닉네임 지어주기 + 중복확인
        {
            while (true) {
                nickName = randomNicknameBox.GetPrefix() + " " + randomNicknameBox.GetSuffix();
                if (!memberRepository.findByNickName(nickName).isPresent()) {
                    break;
                }
            }
        }
        Optional<Member> memberOptional = memberRepository.findByEmail(email);


        //이메일 중복확인
        if (memberOptional.isPresent()) {
            throw new IllegalArgumentException("중복된 이메일이 있습니다");
        }

        Member member = Member.builder()
                .email(email)
                .role(role)
                .password(password)
                .nickName(nickName)
                .build();

        memberRepository.save(member);
    }

    public void login(MemberDto.loginRequestDto loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Optional<Member> memberOptional = memberRepository.findByEmail(email);
        if (memberOptional.isEmpty()) {
            throw new IllegalArgumentException("아이디가 없습니다.");
        }

        if (!password.equals(memberOptional.get().getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(memberOptional.get().getEmail(), memberOptional.get().getRole()));
    }

    public String kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        MemberDto.KakaoMemberInfoDto kakaoMemberInfo = getKakaoMemberInfo(accessToken);

        // 3. 필요시에 회원가입
        Member kakaoUser = registerKakaoUserIfNeeded(kakaoMemberInfo);

        // 4. JWT 토큰 반환
        String createToken = jwtUtil.createToken(kakaoUser.getEmail(), kakaoUser.getRole());
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, createToken);

        return createToken;
    }

    // 1. "인가 코드"로 "액세스 토큰" 요청
    private String getToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "29fe8d63c5cd8315daade453c6819f5f");
        body.add("redirect_uri", "http://localhost:8080/api/member/kakao/callback");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
    private MemberDto.KakaoMemberInfoDto getKakaoMemberInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
//        String nickname = jsonNode.get("properties")
//                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();
        return new MemberDto.KakaoMemberInfoDto(id, email);
    }

    // 3. 필요시에 회원가입
    private Member registerKakaoUserIfNeeded(MemberDto.KakaoMemberInfoDto kakaoUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserInfo.getId();
        Member kakaoUser = memberRepository.findByKakaoId(kakaoId)
                .orElse(null);
        if (kakaoUser == null) {
            // 카카오 사용자 email 동일한 email 가진 회원이 있는지 확인
            String kakaoEmail = kakaoUserInfo.getEmail();
            Member sameEmailUser = memberRepository.findByEmail(kakaoEmail).orElse(null);
            if (sameEmailUser != null) {
                kakaoUser = sameEmailUser;
                // 기존 회원정보에 카카오 Id 추가
                kakaoUser = kakaoUser.kakaoIdUpdate(kakaoId);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
//                String encodedPassword = passwordEncoder.encode(password);
                String nickName = " ";
                while (true) {
                    nickName = randomNicknameBox.GetPrefix() + " " + randomNicknameBox.GetSuffix();
                    if (!memberRepository.findByNickName(nickName).isPresent()) {
                        break;
                    }
                }
                // email: kakao email
                String email = kakaoUserInfo.getEmail();

                kakaoUser = new Member(nickName, kakaoId, email, password, MemberRoleEnum.MEMBER);
            }

            memberRepository.save(kakaoUser);
        }
        return kakaoUser;
    }
}

