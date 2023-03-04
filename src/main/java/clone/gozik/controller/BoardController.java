package clone.gozik.controller;

import clone.gozik.dto.OneBoardResponseDto;
import clone.gozik.dto.RequestBoardDto;
import clone.gozik.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class BoardController {
    private BoardService boardService;

    @GetMapping("/recruit")
    public List recruitget(){
        return boardService.allBoard();
    }

    @GetMapping("/recruit/{id}")
    public OneBoardResponseDto recruitgetone(@PathVariable Long id){
        return boardService.getBoard(id);
    }

    @PostMapping("/recruit")
    public String recruitcreate(
            @RequestPart(value = "data") RequestBoardDto requestBoardDto,
            @RequestPart(value = "image") MultipartFile image,
            @RequestPart(value = "logo") MultipartFile logo
            //            ,@AuthenticationPrincipal UserDetailsImpl userDetails
    )throws IOException {
        boardService.createBoard(requestBoardDto,image,logo);
        return "작성완료";
    }

    @PostMapping("/recruit/{id}")
    public String recruitupdate(
            @PathVariable Long id,
            @RequestPart(value = "data") RequestBoardDto boardRequestDto,
            @RequestPart(value = "image") MultipartFile image,
            @RequestPart(value = "logo") MultipartFile logo
    )throws IOException {
        boardService.updateBoard(id,boardRequestDto,image,logo);
        return "수정완료";
    }

    @PutMapping("/recruit/{id}")
    public String recruitdone(@PathVariable Long id){
        boardService.doneBoard(id);
        return "마감완료";
    }

}
