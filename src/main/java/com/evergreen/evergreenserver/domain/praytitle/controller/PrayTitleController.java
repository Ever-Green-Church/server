package com.evergreen.evergreenserver.domain.praytitle.controller;

import com.evergreen.evergreenserver.domain.praytitle.dto.PostPrayTitleDto;
import com.evergreen.evergreenserver.domain.praytitle.service.PrayTitleService;
import com.evergreen.evergreenserver.global.filter.UserDetailsImpl;
import com.evergreen.evergreenserver.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/praytitle")
public class PrayTitleController {

    private PrayTitleService prayTitleService;

    @PostMapping("")
    public ResponseEntity<ApiResponse> postPrayTitle(@RequestBody PostPrayTitleDto postPrayTitleDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        prayTitleService.postPrayTitle(postPrayTitleDto, userDetails.getUser());

        return ResponseEntity.ok().body(new ApiResponse<>("기도제목 업데이트 완료", HttpStatus.OK.value()));
    }
}