package clone.gozik.controller;

import clone.gozik.dto.MessageDto;
import clone.gozik.dto.OneBoardResponseDto;
import clone.gozik.dto.RequestBoardDto;
import clone.gozik.security.UserDetailsImpl;
import clone.gozik.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class BoardController {
    private BoardService boardService;

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
    public ResponseEntity<MessageDto> recruitcreate(
            @RequestPart(value = "data") RequestBoardDto requestBoardDto,
            @RequestPart(value = "image") MultipartFile image,
            @RequestPart(value = "logo") MultipartFile logo
            ,@AuthenticationPrincipal UserDetailsImpl userDetails
    )throws IOException {
        return boardService.createBoard(requestBoardDto,image,logo,userDetails);
    }

    @PostMapping("/recruit/{id}")
    public ResponseEntity<MessageDto> recruitupdate(
            @PathVariable Long id,
            @RequestPart(value = "data") RequestBoardDto boardRequestDto,
            @RequestPart(value = "image") MultipartFile image,
            @RequestPart(value = "logo") MultipartFile logo
            ,@AuthenticationPrincipal UserDetailsImpl userDetails
    )throws IOException {

        return boardService.updateBoard(id,boardRequestDto,image,logo,userDetails);
    }

    @PutMapping("/recruit/{id}")
    public ResponseEntity<MessageDto> recruitdone(
            @PathVariable Long id
            ,@AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return boardService.doneBoard(id,userDetails);
    }

    @DeleteMapping("/recruit/{id}")
    public ResponseEntity<MessageDto> delete(
            @PathVariable Long id
            ,@AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return boardService.delete(id, userDetails);
    }
}
