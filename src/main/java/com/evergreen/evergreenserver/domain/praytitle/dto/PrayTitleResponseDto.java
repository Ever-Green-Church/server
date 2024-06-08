package com.evergreen.evergreenserver.domain.praytitle.dto;

import com.evergreen.evergreenserver.domain.praytitle.entity.PrayTitle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrayTitleResponseDto {

  private String contents;

  public PrayTitleResponseDto(PrayTitle prayTitle) {
    this.contents = prayTitle.getContents();
  }

}
