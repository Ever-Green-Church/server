package com.evergreen.evergreenserver.domain.praytitle.service;

import com.evergreen.evergreenserver.domain.praytitle.dto.PostPrayTitleDto;
import com.evergreen.evergreenserver.domain.praytitle.dto.PrayTitleResponseDto;
import com.evergreen.evergreenserver.domain.praytitle.entity.PrayTitle;
import com.evergreen.evergreenserver.domain.praytitle.repository.PrayTitleRepository;
import com.evergreen.evergreenserver.domain.user.entity.User;
import com.evergreen.evergreenserver.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrayTitleService {

    private final PrayTitleRepository prayTitleRepository;

    @Transactional
    public void updatePrayTitle(PostPrayTitleDto postPrayTitleDto, User user) {
        PrayTitle prayTitle = prayTitleRepository.findByUserWithFetchJoin(user).orElse(null);
        if (prayTitle == null) {
            prayTitleRepository.save(new PrayTitle(postPrayTitleDto, user));
        } else {
            prayTitle.updatePrayTilte(postPrayTitleDto);
        }
    }

    public PrayTitleResponseDto getPrayTitle(Long prayTitleId) {
        PrayTitle prayTitle = prayTitleRepository.findById(prayTitleId).orElseThrow(() -> new ApiException("기도제목이 존재하지 않습니다.", HttpStatus.BAD_REQUEST));
        return new PrayTitleResponseDto(prayTitle);
    }
}
