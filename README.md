# 늘푸른교회 서버

## 목적: 늘푸른교회를 검색하는 사용자들에게 적당히 그럴듯한 웹페이지를 보여주어 이상한 교회가 아님을 어필한다.

## 문제
1. **비용**
   - 적당히 그럴듯해야하므로 비용은 적을수록 좋다.
   - 도메인은 evergreenchurch.co.ke (연간 16,000원) 고려중 -> 월례회의에서 확인받기
   - AWS를 사용하면 유지비용이 월 10,000원은 나올 것으로 예상 + 의도치않은 RDS 사용으로 비용 폭발할지도 모름 (위험)
   - 집에 남는 핸드폰(안드로이드)을 이용해서 서버를 만들고 띄운다. -> PC를 돌리는 것 보다 유지비(전력)가 저렴할 것으로 예상. -> 하지만 핸드폰 내구도가 버텨줄지는 모르겠음. + 화재 위험
     
2. 인력: 취준 백수 모여라.

3. 디자인: 해줘... 아무도 안해주면 보노보노 나온다.....

## Discussion with zwan

1. 프리티어로 EC2에 DB 넣어서 배포 -> 관리자만 이미지 업로드하면 무리 x --> 하지만 프리티어 용량이 얼마나 되는지 확인해봐야... (넘어가도 EC2가 RDS보단 과금이 적을 것 같음)

2. 사실 이 모든 시행착오의 종착지가 블로그. -> 하지만 목사님 왈, "홈페이지가 있어야 한다." -> 프레임 짜보고 필요한 내용(채울거리) 전달드릴 것.

## 구조

휴대폰에 Docker  ~~~

![늘푸른교회 서버](https://github.com/Ever-Green-Church/server/assets/70873780/1baebc9c-c222-49bf-b569-c51df09e821d)
이미지 출처: https://www.flaticon.com/kr/free-icon/

<br>

---
<br>

## 코드 컨벤션
https://www.notion.so/teamsparta/Code-Convention-607ef9ce98a64ecb938762e701159604

## Github Rules
https://www.notion.so/teamsparta/Github-Rules-c4b17c3e2e4343a8a320bcd64eba9810
