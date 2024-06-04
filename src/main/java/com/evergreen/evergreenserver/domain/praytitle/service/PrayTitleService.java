package com.evergreen.evergreenserver.domain.praytitle.service;

import com.evergreen.evergreenserver.domain.praytitle.dto.PostPrayTitleDto;
import com.evergreen.evergreenserver.domain.praytitle.entity.PrayTitle;
import com.evergreen.evergreenserver.domain.praytitle.repository.PrayTitleRepository;
import com.evergreen.evergreenserver.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrayTitleService {

    private PrayTitleRepository prayTitleRepository;

    public void postPrayTitle(PostPrayTitleDto postPrayTitleDto, User user) {
        prayTitleRepository.save(new PrayTitle(postPrayTitleDto, user));
    }
}
