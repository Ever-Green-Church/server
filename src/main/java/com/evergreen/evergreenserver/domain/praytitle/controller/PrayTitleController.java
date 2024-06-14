package com.evergreen.evergreenserver.domain.praytitle.controller;

import com.evergreen.evergreenserver.domain.praytitle.dto.PostPrayTitleDto;
import com.evergreen.evergreenserver.domain.praytitle.dto.PrayTitleResponseDto;
import com.evergreen.evergreenserver.domain.praytitle.service.PrayTitleService;
import com.evergreen.evergreenserver.global.filter.UserDetailsImpl;
import com.evergreen.evergreenserver.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/prayTitles")
public class PrayTitleController {

    private final PrayTitleService prayTitleService;

    @PostMapping("")
    public ResponseEntity<ApiResponse> updatePrayTitle(@RequestBody PostPrayTitleDto postPrayTitleDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        prayTitleService.updatePrayTitle(postPrayTitleDto, userDetails.getUser());

        return ResponseEntity.ok().body(new ApiResponse<>("기도제목 업데이트 완료", HttpStatus.OK.value()));
    }

    @GetMapping("/{prayTitleId}")
    public ResponseEntity<ApiResponse> getPrayTitle(@PathVariable Long prayTitleId) {
        PrayTitleResponseDto prayTitleResponseDto = prayTitleService.getPrayTitle(prayTitleId);

        return ResponseEntity.ok().body(new ApiResponse("기도제목 조회 성공", HttpStatus.OK.value(), prayTitleResponseDto));
    }
}
