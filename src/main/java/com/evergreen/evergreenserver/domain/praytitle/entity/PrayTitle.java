package com.evergreen.evergreenserver.domain.praytitle.entity;

import com.evergreen.evergreenserver.domain.praytitle.dto.PostPrayTitleDto;
import com.evergreen.evergreenserver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "praytitle")
public class PrayTitle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nickname;

    @Column(length = 5000)
    private String contents;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    public PrayTitle(PostPrayTitleDto postPrayTitleDto, User user) {
        this.nickname = user.getNickname();
        this.contents = postPrayTitleDto.getContents();
        this.user = user;
    }

    public void updatePrayTilte(PostPrayTitleDto postPrayTitleDto) {
        this.contents = postPrayTitleDto.getContents();
    }
}
