package clone.gozik.controller;

import clone.gozik.dto.MessageDto;
import clone.gozik.dto.OneBoardResponseDto;
import clone.gozik.dto.RequestBoardDto;
import clone.gozik.entity.Member;
import clone.gozik.repository.MemberRepository;
import clone.gozik.security.MemberDetailsImpl;
import clone.gozik.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    private final MemberRepository memberRepository;

    @GetMapping("/main")
    public List mainpage(){
        return boardService.mainBoard();
    }

    @GetMapping("/recruit")
    public List recruitget(){
        return boardService.allBoard();
    }

    @GetMapping("/recruit/{id}")
    public OneBoardResponseDto recruitgetone(@PathVariable Long id){
        return boardService.getBoard(id);
    }

    @PostMapping("/recruit")
    public ResponseEntity<MessageDto> recruitcreate(@AuthenticationPrincipal MemberDetailsImpl memberDetails,
                    @RequestPart(value = "data") RequestBoardDto requestBoardDto,
                    @RequestPart(value = "image") MultipartFile image,
                    @RequestPart(value = "logo") MultipartFile logo){
        System.out.println("보드 제작중");
        return boardService.createBoard(memberDetails,requestBoardDto,image,logo);
    }

    @PutMapping("/recruit/{id}")
    public ResponseEntity<MessageDto> recruitupdate(
            @PathVariable Long id,
            @RequestPart(value = "data") RequestBoardDto boardRequestDto,
            @RequestPart(value = "image") MultipartFile image,
            @RequestPart(value = "logo") MultipartFile logo
            ,@AuthenticationPrincipal MemberDetailsImpl memberDetails
    )throws IOException {

        return boardService.updateBoard(id,boardRequestDto,image,logo,memberDetails);
    }

    @PutMapping("/recruit/endday/{id}")
    public ResponseEntity<MessageDto> recruitdone(
            @PathVariable Long id
            ,@AuthenticationPrincipal MemberDetailsImpl memberDetails
    ){
        return boardService.doneBoard(id,memberDetails);
    }

    @DeleteMapping("/recruit/{id}")
    public ResponseEntity<MessageDto> delete(
            @PathVariable Long id
            ,@AuthenticationPrincipal MemberDetailsImpl memberDetails
    ){
        return boardService.delete(id, memberDetails);
    }
}
