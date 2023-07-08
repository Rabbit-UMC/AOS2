# Git 관리 규칙


<br/>

## Branch

- `main` : 항상 실행 가능한 상태 유지 (최종 버전 올라가는 곳)
- - Team Leader가 release에서 pull request 후, merge
- `develop이니셜` : 개인 개발용
- - **developH**(하나/심세원), **developD**(다비/이유진), **developC**(초코/김현지)
- `release` : main으로 옮기기 전 검수용
- - release로 pull request 올리기
- `hotfix` : 급하게 고쳐야 하는 코드 디버깅용

<br/>

## Commit

- `Feat` : 새로운 기능 추가
- `modify` : 기존 기능 수정
- `Fix` : 버그 해결
- `Docs` : README, 주석 작성
- `style` : 스타일 관련 기능(코드 포맷팅, 세미콜론 누락, 코드 자체의 변경이 없는 경우)
- `Refactor` : 코드 개선 리팩토링, 기능의 변경 없이 코드 구조를 재조정
- `Test` : 테스트 코드 추가
- `Chore` : 빌드 업무 수정, 패키지 매니저 수정 (ex.gitignore 수정)
<br/>
아래의 명령어를 통해 commit setting
  
```
git config --global commit.template .gitmessage.txt
```

commit 작성 예시 (제목만 해도 됨)

```
git commit -m "<feat> : 커밋 제목 // 짧고 간단하게
                                  // 2칸 띄고
부가 설명"                        // 이번 커밋으로 인한 변경점
```
