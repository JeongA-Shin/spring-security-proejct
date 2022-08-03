package group.springsecurityproject.config.oauth;

import group.springsecurityproject.config.auth.PrincipalDetails;
import group.springsecurityproject.config.oauth.provider.GoogleUserInfo;
import group.springsecurityproject.config.oauth.provider.NaverUserInfo;
import group.springsecurityproject.config.oauth.provider.OAuth2UserInfo;
import group.springsecurityproject.model.User;
import group.springsecurityproject.repository.UserRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;



//즉 정리해보자면
// Seuciry Session(Security Context Holder에 담겨짐) 안에는 Authentication 객체만 들어갈 수 있음
// 그리고 이 Authentication 안에는UserDetails 객체(자체 로그인시)와 Oauth2User(Oauth 로그인시)만 들어갈 수 있는 거임
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    
    
    //구글로부터 "받은" userRequest 정보를 후처리하는 함수
    // - securityconfig의 oauth설정에 의해 oauth 로그인 후 자동으로 호출됨
    // !!! 즉 따라서 해당 함수를 실행하면서 자동으로 spring security의 session안에 authentication을 생성하고 그 안에 Oauth2User를 넣음
    //원래는 코드를 받아야 하지만,
    // accesstoken (userRequest.getAccessToken()) + 사용자 정보(super.loadUser(userRequest).getAttributes())를 바로 한 번에 받아옴
    
    /**
     * 해당 메서드가 실행 후 종료시 @AuthenticationPrincipal 어노테이션이 활성화됨 (만들어짐) == authentication 객체가 생성됨
     * @param userRequest the user request
     * @return
     * @throws OAuth2AuthenticationException
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //System.out.println("userRequest: "+userRequest);
        //System.out.println("getAccessToken: "+userRequest.getAccessToken().getTokenValue()); //accessToken  받아온 결과
    
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인을 완료 -> 구글이 code를 리턴(Oauth-client 라이브러리가 받아옴 - 자동)
        //-> Oauth-client 라이브러리가 받아온 코드로 accessToken 요청(자동으로)-> 그리고 accessToken 받아옴 -> 해당 accessToken은 userRequest에 담긴 정보
        //그리고 userRequest 정보로 회원 프로필을 받아와야함 -> loadUser 함수 -> 회원 프로필 받아올 수 있음
        //System.out.println("getAttribute: "+super.loadUser(userRequest).getAttributes()); // 사용자 정보 받아온 결과
        /**
         * 어떤 sns 등을 사용해서 oauth 로그인을 사용할 지 모르니까 분기 태워줘야 함
         */
        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            System.out.println("google login request");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            System.out.println("naver login request");
            // 이건 왜냐면 application.yml의 설정에 의해 네이버가 attribute 하위 중 response 키에 해당 정보들을 넘겨주므로
            // 긍까 이런 형식임: attibutes={code=00,message=success, response={id=3,username="jeong"}}
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
        }else{
            System.out.println("Unsupported login");
        }
        
        // 강제 회원가입 진행해볼 예정 - 강제 회원가입을 위해 필요한 정보 모으기
        String provider = oAuth2UserInfo.getProvider(); //oauth 로그인시 어느 소셜로 로그인했는지. e.g. google, naver..현재는 google
        String providerId = oAuth2UserInfo.getProviderId(); //provider에서 제공한 사용자 pk (즉 "구글"사용자 정보에서의 pk임. 우리 자체 사용자의 pk가 아니라)
        String username = provider+"_"+providerId; //google_1029305236 (그냥 예시)
        String email = oAuth2UserInfo.getEmail();
        String role ="ROLE_USER";
        
        //근데 이미 회원가입이 되어있으면 안 됨
        // 이미 회원가입이 되어있는지 확인
        User userEntity = userRepository.findByUsername(username);
        //회원가입이 안 되어있을 때만 강제 회원가입 시키기
        if(userEntity==null){
            userEntity=User.builder()
                .username(username)
                .email(email)
                .role(role)
                .provider(provider)
                .providerId(providerId)
                .build();
            userRepository.save(userEntity);
        }
        
        //authentication 안에 담길 PrincipalDetails 객체 생성(리턴)(OAuth2User와 UserDetails 모두 담기기 가능하도록 implements 되어 있음)
        return new PrincipalDetails(userEntity,oAuth2User.getAttributes());
        //현재 함수의 반환형이 OAuth2User인데 , PrincipalDetails가 OAuth2User를 implement하기 때문에 같은 자료형으로 취급 가능
    
        //즉, 따라서 Oauth로 로그인 후
        // (oauth-client에 의해 자동?으로) authentication 안에
        // 해당 함수의 리턴값인 new PrincipalDetails(userEntity,oAuth2User.getAttributes()); 가 들어가게 됨
        //즉 세션 정보로 저장되는 것
    }
}

/**
 * 아래 내용은 제일 처음에 구글 OAuth 로그인만 있을 때 사용한 코드
 */

////즉 정리해보자면
//// Seuciry Session(Security Context Holder에 담겨짐) 안에는 Authentication 객체만 들어갈 수 있음
//// 그리고 이 Authentication 안에는UserDetails 객체(자체 로그인시)와 Oauth2User(Oauth 로그인시)만 들어갈 수 있는 거임
//@Service
//@RequiredArgsConstructor
//public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
//
//    private final UserRepository userRepository;
//
//    //private final BCryptPasswordEncoder bCryptPasswordEncoder;
//
//    //구글로부터 "받은" userRequest 정보를 후처리하는 함수
//    // - securityconfig의 oauth설정에 의해 oauth 로그인 후 자동으로 호출됨
//    // !!! 즉 따라서 해당 함수를 실행하면서 자동으로 spring security의 session안에 authentication을 생성하고 그 안에 Oauth2User를 넣음
//    //원래는 코드를 받아야 하지만,
//    // accesstoken (userRequest.getAccessToken()) + 사용자 정보(super.loadUser(userRequest).getAttributes())를 바로 한 번에 받아옴
//
//    /**
//     * 해당 메서드가 실행 후 종료시 @AuthenticationPrincipal 어노테이션이 활성화됨 (만들어짐) == authentication 객체가 생성됨
//     * @param userRequest the user request
//     * @return
//     * @throws OAuth2AuthenticationException
//     */
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        //System.out.println("userRequest: "+userRequest);
//        //System.out.println("getAccessToken: "+userRequest.getAccessToken().getTokenValue()); //accessToken  받아온 결과
//
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//        // 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인을 완료 -> 구글이 code를 리턴(Oauth-client 라이브러리가 받아옴 - 자동)
//        //-> Oauth-client 라이브러리가 받아온 코드로 accessToken 요청(자동으로)-> 그리고 accessToken 받아옴 -> 해당 accessToken은 userRequest에 담긴 정보
//        //그리고 userRequest 정보로 회원 프로필을 받아와야함 -> loadUser 함수 -> 회원 프로필 받아올 수 있음
//        //System.out.println("getAttribute: "+super.loadUser(userRequest).getAttributes()); // 사용자 정보 받아온 결과
//
//        // 강제 회원가입 진행해볼 예정 - 강제 회원가입을 위해 필요한 정보 모으기
//        String provider = userRequest.getClientRegistration().getClientId(); //oauth 로그인시 어느 소셜로 로그인했는지. e.g. google, naver..현재는 google
//        String providerId = oAuth2User.getAttribute("sub"); //provider에서 제공한 사용자 pk (즉 "구글"사용자 정보에서의 pk임. 우리 자체 사용자의 pk가 아니라)
//        String username = provider+"_"+providerId; //google_1029305236 (그냥 예시)
//        String email = oAuth2User.getAttribute("email");
//        //String password = bCryptPasswordEncoder.encode("겟인데어"); //사실 크게 의미 없음
//        String role ="ROLE_USER";
//
//        //근데 이미 회원가입이 되어있으면 안 됨
//        // 이미 회원가입이 되어있는지 확인
//        User userEntity = userRepository.findByUsername(username);
//        //회원가입이 안 되어있을 때만 강제 회원가입 시키기
//        if(userEntity==null){
//            userEntity=User.builder()
//                .username(username)
//                //.password(password)
//                .email(email)
//                .role(role)
//                .provider(provider)
//                .providerId(providerId)
//                .build();
//            userRepository.save(userEntity);
//        }
//
//        //authentication 안에 담길 PrincipalDetails 객체 생성(리턴)(OAuth2User와 UserDetails 모두 담기기 가능하도록 implements 되어 있음)
//        return new PrincipalDetails(userEntity,oAuth2User.getAttributes());
//        //현재 함수의 반환형이 OAuth2User인데 , PrincipalDetails가 OAuth2User를 implement하기 때문에 같은 자료형으로 취급 가능
//
//        //즉, 따라서 Oauth로 로그인 후
//        // (oauth-client에 의해 자동?으로) authentication 안에
//        // 해당 함수의 리턴값인 new PrincipalDetails(userEntity,oAuth2User.getAttributes()); 가 들어가게 됨
//        //즉 세션 정보로 저장되는 것
//    }
//}

