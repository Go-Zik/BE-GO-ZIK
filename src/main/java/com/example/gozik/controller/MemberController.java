package clone.gozic.controller;
//import clone.gozic.jwt.JwtUtil;
import clone.gozic.dto.MemberDto;
import clone.gozic.jwt.JwtUtil;
import clone.gozic.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

//    private final JwtUtil jwtUtil;

    @PostMapping("/join")
    public String join(@RequestBody MemberDto.JoinRequestDto JoinRequest){
        memberService.signup(JoinRequest);
        return "회원가입 성공";
    }
    @GetMapping("/signup")
    public ModelAndView signupPage() {
        return new ModelAndView("signup");
    }

    @PostMapping("/login")
    public String login(@RequestBody MemberDto.loginRequestDto loginRequest){
        memberService.login(loginRequest);
        return "로그인 성공";
    }
    @GetMapping("/login")
    public ModelAndView loginPage() {
        return new ModelAndView("login");
    }

    @GetMapping("/shop")
    public ModelAndView shop() {
        return new ModelAndView("index");
    }

    @GetMapping("/kakao/callback")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        // code: 카카오 서버로부터 받은 인가 코드
        System.out.println("code = " + code);
        String createToken = memberService.kakaoLogin(code, response);

        // Cookie 생성 및 직접 브라우저에 Set
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, createToken.substring(7));
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/api/member/shop";
    }

}

