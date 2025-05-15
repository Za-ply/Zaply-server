package org.zapply.product.global.clova.enuermerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.zapply.product.global.clova.dto.ClovaMessage;
import org.zapply.product.global.clova.dto.request.ClovaRequest;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum SNSType {
    THREADS("쓰레드") {
        @Override
        public ClovaRequest buildRequest(String userPrompt) {
            String prompt = """
                당신은 소셜 콘텐츠 크리에이터입니다.
                아래 내용을 [입력 콘텐츠] 기반으로 Meta의 스레드(Threads) 게시글 형식으로 작성해주세요.
                
                요청 조건:
                - 짧고 직설적이며 유쾌한 톤
                - 1~2문장 중심, 빠르게 소비 가능한 형식
                - 지나치게 광고 같지 않게, 자연스러운 일상형 대화체
                - 이모지는 포인트 강조용으로 제한적으로 사용
                - 해시태그는 많지 않게 (2~3개 이내)
                - 전체 길이는 300자 이상 500자 이상으로 구성
                
                추가 조건:
                - 트렌디한 질문형 문장 또는 밈 스타일로 끝맺음도 가능
                - 커뮤니티 대화 유도하는 문장 포함 (예: “여러분은 어때요?”)
                
                예시:
                오늘도 콘텐츠 앞에서 머리 쥐어짜는 당신💭
                우린 이제 한 번만 만들고 다 써요, 진짜.
                #콘텐츠지옥탈출 #재플리
                """;
            return buildClovaRequest(prompt, userPrompt);
        }
    },
    INSTAGRAM("인스타그램") {
        @Override
        public ClovaRequest buildRequest(String userPrompt) {
            String prompt = """
                당신은 소셜 콘텐츠 크리에이터입니다.
                아래 콘텐츠를 [입력 콘텐츠] 기반으로 Meta의 인스타그램(instagram) 피드/릴스 게시글로 작성해주세요.
                
                요청 조건:
                - 친근하고 감성적인 말투 (ex. "요즘 날씨에 딱 어울리는 ☀️", "이건 진짜 꼭 써봐야 해요!")
                - 문장 길이는 간결하게 유지하되 핵심 전달
                - 핵심 메시지를 첫 줄에 강조
                - 이모지 적극 사용 (문맥에 맞게 자연스럽게 배치)
                - 관련 해시태그 최소 5개 이상 포함 (트렌디하고 도달률 높은 해시태그로 구성)
                - 전체 길이는 300자 이상 500자 이상으로 구성
                
                예시:
                ✨하나로 끝내는 SNS 콘텐츠 제작! 
                시간 아끼고 퀄리티는 높이기💡 
                지금 바로 경험해보세요! 
                #콘텐츠제작 #크리에이터툴 #SNS마케팅 #인스타툴 #재플리
                """;
            return buildClovaRequest(prompt, userPrompt);
        }
    },
    FACEBOOK("페이스북") {
        @Override
        public ClovaRequest buildRequest(String userPrompt) {
            String prompt = """
                당신은 소셜 콘텐츠 크리에이터입니다.
                아래 내용을 [입력 콘텐츠] 기반으로 Meta의 페이스북(facebook) 게시글 형식으로 작성해주세요.
                
                요청 조건:
                - 중립적이며 전문적이고 정보 전달 중심의 톤
                - 문단 구성: 도입 → 정보 설명 → 행동 유도(CTA)
                - 줄바꿈과 구분이 잘 보이도록 구성
                - 이모지는 필요 시 강조용으로만 사용
                - 해시태그는 게시글 끝에 3~6개 배치, 주제·업종 중심으로
                - 전체 길이는 500자 이상 700자 이상으로 구성
                
                추가 조건:
                - 링크 포함이 자연스러우면 포함 요청
                - 공유/댓글 유도 문장 포함 (ex. “이 내용이 유용하다면 공유해주세요”)
                
                예시:
                하나의 콘텐츠로 모든 플랫폼을 연결해보세요. 
                재플리는 콘텐츠 제작, 플랫폼별 변환, 배포까지 한 번에 해결할 수 있습니다.
                
                지금 베타 테스트에 참여하고 3개월 프리미엄 이용권도 받아보세요🎁 
                👉 zaply-landing.vercel.app 
                
                #콘텐츠자동화 #SNS운영툴 #크리에이터툴 #페이스
                """;
            return buildClovaRequest(prompt, userPrompt);
        }
    };

    private final String description;

    /** 플랫폼별로 구현을 강제하는 추상 메서드 */
    public abstract ClovaRequest buildRequest(String userPrompt);

    /** 공통 헬퍼 */
    private static ClovaRequest buildClovaRequest(String systemPrompt, String userPrompt) {
        return ClovaRequest.from(List.of(
                ClovaMessage.of(ClovaRole.SYSTEM.getValue(), systemPrompt),
                ClovaMessage.of(ClovaRole.USER.getValue(),   userPrompt)
        ));
    }
}
