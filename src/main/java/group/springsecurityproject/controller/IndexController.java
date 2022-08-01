package group.springsecurityproject.controller;

import group.springsecurityproject.model.User;
import group.springsecurityproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller //주로 View를 반환하기 위해 사용 // RestController의 주용도는 Json 형태로 객체 데이터를 반환하는 것입니다.
@RequiredArgsConstructor
public class IndexController {
    
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder; //SecurityConfig에서 bean으로 등록했기 때문에 이게 가능함
    @GetMapping
    public String index(){
        return "index"; //mustache
        //스프링에서 뷰를 찾는 기본 경로가
        // src/main/resources/templates/... .mustache임 
        //왜냐면 dependecy에 이미 mustache가 있으므로 mustache만 찾음
        //근데 나는 html파일만 있음 -> config/webmvcconfig 참고하기
    }
    
    @GetMapping("/user")
    public String user(){
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
