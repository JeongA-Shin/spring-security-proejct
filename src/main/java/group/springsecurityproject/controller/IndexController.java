package group.springsecurityproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller //주로 View를 반환하기 위해 사용 // RestController의 주용도는 Json 형태로 객체 데이터를 반환하는 것입니다.
public class IndexController {

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
    
    @GetMapping("/join")
    public String join(){
        return "join";
    }
    
    @GetMapping("/joinProc")
    public @ResponseBody String joinProc(){
        return "회원가입 완료됨";
    }
}
