package com.evergreen.evergreenserver.domain.praytitle.service;

import com.evergreen.evergreenserver.domain.praytitle.dto.PostPrayTitleDto;
import com.evergreen.evergreenserver.domain.praytitle.dto.PrayTitleResponseDto;
import com.evergreen.evergreenserver.domain.praytitle.entity.PrayTitle;
import com.evergreen.evergreenserver.domain.praytitle.repository.PrayTitleRepository;
import com.evergreen.evergreenserver.domain.user.entity.User;
import com.evergreen.evergreenserver.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrayTitleService {

    private PrayTitleRepository prayTitleRepository;

    public void postPrayTitle(PostPrayTitleDto postPrayTitleDto, User user) {
        prayTitleRepository.save(new PrayTitle(postPrayTitleDto, user));
    }

    public PrayTitleResponseDto getPrayTitle(Long prayTitleId) {
        PrayTitle prayTitle = prayTitleRepository.findById(prayTitleId).orElseThrow(() -> new ApiException("기도제목이 존재하지 않습니다.", HttpStatus.BAD_REQUEST));
        return new PrayTitleResponseDto(prayTitle);
    }
}
