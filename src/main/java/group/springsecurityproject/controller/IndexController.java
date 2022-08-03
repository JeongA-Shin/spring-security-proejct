package group.springsecurityproject.controller;

import group.springsecurityproject.config.auth.PrincipalDetails;
import group.springsecurityproject.model.User;
import group.springsecurityproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller //주로 View를 반환하기 위해 사용 // RestController의 주용도는 Json 형태로 객체 데이터를 반환하는 것입니다.
@RequiredArgsConstructor
public class IndexController {
    
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder; //SecurityConfig에서 bean으로 등록했기 때문에 이게 가능함
   
//  아래의 test/login 과 test/oauth2/login은 [PrincipalDetails implements UserDetails, OAuth2User] 으로 처리 전,
    // [PrincipalDetails implements UserDetails] 만 있었을 때임
    //즉 UserDetails와 OAuth2User가 각각의 자료형으로 authentication 자료형에 담겨질 때 , 각 로그인 방식에 따라 어케 처리가 달라지는 지를 나타낸 것
    // 즉 지금은 PrincipalDetails implements UserDetails, OAuth2User 으로 해주었기 때문에
    
    //따라서 Oauth로 로그인하든, 자체 로그인을 하든 사용자가 로그인 후에는
    // (oauth-client에 의해 자동?으로) authentication 안에 principalDetails 하나로 퉁쳐서 들어가게 됨 -즉 자료형을 하나로 통합?되는 것처럼 할 수 있게 됨
    
//    /**
//     * 자체 로그인 후 어떤 유저가 로그인한 건지 세션 정보를 통해 알 수 있음, 자체 로그인 - 구글 로그인을 해주고 여기 들어오면 에러남
//     * @param authentication
//     * @return
//     */
//    @GetMapping("/test/login") //자체 로그인이든 sns 로그인이든 로그인 후에는 파라미터인 authentication 안에 현재 사용자 정보가 담기는 것임
//    public @ResponseBody String testLogin(Authentication authentication){
//        //현재 PrincipalDetails가 UserDetails를 implements하고 있으므로 해당 다운 캐스팅이 가능한 것
//        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();//정보 담김, 다운 캐스팅
//        System.out.println("curr user info: "+ principalDetails.getUser());
//        return "자체 로그인에 대한 세션 정보 확인하기";
//    }
//
////    위의 함수와 똑같지만 , 어노테이션을 활용해서 받아올 수도 있음- 자체 로그인시
////    /**
////     * 로그인 후 어떤 유저가 로그인한 건지 세션 정보를 통해 알 수 있음
////     * @param userDetails 해당 파라미터도 PrincipalDetails가 userDetails를 implements하고 있으므로 자료형을 PrincipalDetails라고 해두면 됨
////     * @return
////     */
////    @GetMapping("/test/login")
////    public @ResponseBody String testLogin(@AuthenticationPrincipal PrincipalDetails userDetails){
////        System.out.println(userDetails.getUser());
////        return "세션 정보 확인하기";
////    }
//
//
//    /**
//     * Oauth 로그인 후 어떤 유저가 로그인한 건지 세션 정보를 통해 알 수 있음, Oauth 로그인
//     * @param authentication
//     * @return
//     */
//    @GetMapping("/test/oauth/login") //자체 로그인이든 sns 로그인이든 로그인 후에는 파라미터인 authentication 안에 현재 사용자 정보가 담기는 것임
//    public @ResponseBody String testOAuthLogin(Authentication authentication){
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();//자체 로그인과 캐스팅이 달라진 것
//        System.out.println("curr user info: "+ oAuth2User.getAttributes());
//        return "OAuth 로그인에 대한 세션 정보 확인하기";
//    }
//
//    /*
//    /**
//     * Oauth 로그인 후 어떤 유저가 로그인한 건지 세션 정보를 통해 알 수 있음, Oauth 로그인
//     * @param authentication
//     * @return
//
//    @GetMapping("/test/oauth/login") //자체 로그인이든 sns 로그인이든 로그인 후에는 파라미터인 authentication 안에 현재 사용자 정보가 담기는 것임
//    public @ResponseBody String testOAuthLogin(@AuthenticationPrincipal OAuth2User oauth){
//        System.out.println("curr user info: "+ oauth.getAttributes());
//        return "OAuth 로그인에 대한 세션 정보 확인하기";
//    }
//    */
//
    @GetMapping
    public String index(){
        return "index"; //mustache
        //스프링에서 뷰를 찾는 기본 경로가
        // src/main/resources/templates/... .mustache임 
        //왜냐면 dependecy에 이미 mustache가 있으므로 mustache만 찾음
        //근데 나는 html파일만 있음 -> config/webmvcconfig 참고하기
    }
    
    //따라서 Oauth로 로그인하든, 자체 로그인을 하든 사용자가 로그인 후에는
    //해당 사용자 정보를 PrincipalDetails로 받아올 수 있음
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println("principalDetails: "+principalDetails.getUser());
        return "user";
    }
    
    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }
    
    @GetMapping("/manager")
    public String manager(){
        return "manager";
    }
    
    // spring config에서 antMatcher의 configure메서드를 하기 전에는, spring security가 해당 주소를 낚아채버림
    //즉 SecurityConfig 파일 생성 후에는 spring security의 디폴트 설정?들이 작동하지 않음
    @GetMapping("/login")
    public String login(){
        return "login";
    }
    
    //실제 join로직을 처리함
    @PostMapping("/join")
    public String join(User user){
        System.out.println(user);
        user.setRole("ROLE_USER"); // 어떤 권한인지 부여
        //비밀번호 암호화
        String rawPwd = user.getPassword();
        String encPwd = bCryptPasswordEncoder.encode(rawPwd);
        user.setPassword(encPwd);
        
        userRepository.save(user); //회원으로 등록 . 위의 비밀번호 암호화 과정을 반드시 거쳐야 함
        return "redirect:/login";
    }
    
    //회원가입창으로 감
    @GetMapping("/joinForm")
    public String joinForm(){
        return "joinForm";
    }
    //@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN)")
    @Secured("ROLE_ADMIN") //와 //@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN)")
    // 특정 메서드에 대해 간단하게 권한별로 처리하고 싶을 때 - 굳이 config의 antmatchers로 하지 않고
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인정보";
    }
    
}
