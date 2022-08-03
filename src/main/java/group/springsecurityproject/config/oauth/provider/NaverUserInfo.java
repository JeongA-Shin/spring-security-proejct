package group.springsecurityproject.config.oauth.provider;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {
    
    //OAuth2User oAuth2User = super.loadUser(userRequest);
    // oAuth2User.getAttributes();가 담김
    // 여기서 userRequest는 PrincipalOauth2UserService의 loadUser함수(로그인 후 후처리 함수, authentication 생성)의 파라미터
    
    // 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인을 완료 -> 구글이 code를 리턴(Oauth-client 라이브러리가 받아옴 - 자동)
    //-> Oauth-client 라이브러리가 받아온 코드로 accessToken 요청(자동으로)-> 그리고 accessToken 받아옴 -> 해당 accessToken은 userRequest에 담긴 정보
    //그리고 userRequest 정보로 회원 프로필을 받아와야함 -> loadUser 함수 -> 회원 프로필 받아올 수 있음
    //System.out.println("getAttribute: "+super.loadUser(userRequest).getAttributes()); // 사용자 정보 받아온 결과
    private Map<String,Object> attributes;
    
    public NaverUserInfo(Map<String,Object> attributes){
        this.attributes=attributes;
    }
    
    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }
    
    @Override
    public String getProvider() {
        return "naver";
    }
    
    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
    
    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}
