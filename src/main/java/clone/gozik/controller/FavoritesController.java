package clone.gozik.controller;


import clone.gozik.dto.MessageDto;
import clone.gozik.security.MemberDetailsImpl;
import clone.gozik.service.FavoritesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorite")
public class FavoritesController {

    private final FavoritesService favoritesService;



    //게시글을 즐겨찾기하거나 취소하기
    @PostMapping("/{boardid}")
    public MessageDto favoritesOrCancle(@AuthenticationPrincipal MemberDetailsImpl memberDetails, @PathVariable Long boardid){
        return favoritesService.doOrCancle(memberDetails, boardid);
    }

    //게시글을 즐겨찾기 한 사람의 수
    @GetMapping("/board/{boardid}")
    public int favoirtescount(@PathVariable Long boardid){
        return favoritesService.favorites(boardid);
    }


    //모든 게시글의 게시글 당 즐겨찾기한 사람 수 조회하기
    @GetMapping("/board/all")
    public HashMap<Long,Long> getallboardfavor(){
        return favoritesService.allfavor();
    }

    //한 사람이 즐겨찾기 한 게시글 류
    @GetMapping("/member")
    public List getboardmemberfavor(@AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return favoritesService.getmembersfavor(memberDetails);
    }



}
