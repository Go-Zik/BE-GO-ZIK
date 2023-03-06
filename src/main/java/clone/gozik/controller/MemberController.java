package clone.gozik.controller;
//import clone.gozic.jwt.JwtUtil;
import clone.gozik.dto.MemberDto;
import clone.gozik.dto.MessageDto;
import clone.gozik.dto.TestDto;
import clone.gozik.jwt.JwtUtil;
import clone.gozik.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class MemberController {
    private final MemberService memberService;

//    private final JwtUtil jwtUtil;

    @PostMapping("/join")
    public ResponseEntity<MessageDto> join(@RequestBody MemberDto.JoinRequestDto JoinRequest){
        return memberService.signup(JoinRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<MessageDto> login(@RequestBody MemberDto.loginRequestDto loginRequest, HttpServletResponse response){
        return memberService.login(loginRequest, response);
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<MessageDto> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        // code: 카카오 서버로부터 받은 인가 코드
        System.out.println("code = " + code);
        String createToken = String.valueOf(memberService.kakaoLogin(code, response));

        // Cookie 생성 및 직접 브라우저에 Set
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, createToken.substring(7));
        cookie.setPath("/");
        response.addCookie(cookie);

        return memberService.kakaoLogin(code, response);
    }
}

