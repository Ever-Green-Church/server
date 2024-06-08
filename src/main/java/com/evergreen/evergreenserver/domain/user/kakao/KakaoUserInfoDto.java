package com.evergreen.evergreenserver.domain.user.kakao;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoUserInfoDto {

    private Long id;
    private String nickname;
    private String email;
    private String imageUrl;
}
