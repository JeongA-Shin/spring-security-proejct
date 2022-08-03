package group.springsecurityproject.config;

import group.springsecurityproject.config.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity // 필터 체인 관리 시작 어노테이션  //spring security 필터(여기서 만드는 SecurityConfig)가 spring 필터 체인에 등록이 됨
@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true) //secured 어노테이션 활성화, preAuthorize 어노테이션 활성화
// 특정 컨트롤러 메서드(즉 특정 url 요청)에 대해 간단하게 권한별로 처리하고 싶을 때 - 굳이 config의 antmatchers로 하지 않고 - indexContoller에 예시 있음
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PrincipalOauth2UserService principalOauth2UserService; //컴포지션
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*
        csrf 가 켜져 있으면 form 태그로 요청시에 csrf 속성이 추가됩니다.
        그래서 서버쪽에서 만들어준 form 태그로만 요청이 가능하게 됩니다.
        그렇기 때문에 postman 요청이 불가능해져서 disable 한거에요!!
         */
        http.csrf().disable();
        /*
         * 리소스(URL)의 허용할 권한 설정
         * 특정 리소스의 접근 허용 또는 특정 권한을 가진 사용자만 접근을 가능하게 할 수 있습니다.
         */
        http.authorizeRequests() // 리소스마다 권한 설정해줌
            .antMatchers("/user/**").authenticated() //"/user/**"로 요청이 들어오면 인증이 필요함, 즉 해당 요청에는 보안검사를 한다.
            .antMatchers("/manager/**").access("hasRole(('ROLE_ADMIN')or hasRole('ROLE_MANAGER'))")
            .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
            .anyRequest().permitAll()
            // 이제 만약 권한이 없는 페이지로 요청이 들어오면, 로그인 페이지로 자동 전환이 되도록 설정해줌, 보안 검증을 formLogin으로 설정함
            .and()
            .formLogin() ////보안 검증은 formLogin방식으로 하겠다.
            .loginPage("/login") //인증이 필요할 때(==해당 로그인이 필요할 때) 이동하는 페이지 설정하는 api입니다
            .loginProcessingUrl("/loginProc") //로그인 요청이 오면(/loginProc로 요청이 오면)
            // 시큐리티가 대신 낚아채서 로그인을 진행해줌 - 자동으로 auth/PrincipalDetailsService로 감. 즉 시큐리티에게 로그인 진행에 해당되는 요청을 알려주는 것
            // 로그인 요청은 login.html 참고, 로그인 진행 과정은 auth/PrincipalDetails 와 auth/PrincipalDetailsService순으로 참고
             // 따라서 컨트롤러에 따로 /loginProc를 해주지 않아도 됨
            .defaultSuccessUrl("/")//로그인 성공시에는 indexPage로 가게 함'
            //이제 oauth 로그인시 필요한, 관련된 설정을 함
            .and()
            .oauth2Login()//보안 검증을 oauth2방식으로 한다
            .loginPage("/login")// 얘는 인증이 필요할 때(--자체이든 oauth이든 해당 로그인이 필요할 시) 일단 이 페이지로 가게 해준다는 의미임
            //구글 로그인이 완료된 이후의 후처리가 필요함  -> tip. 원래는 코드를 받아야 하지만, accesstoken+ 사용자 정보를 바로 한 번에 받아옴
            // 이거는 PrincipalOauth2UserService 참고
            //1. 코드 받기(인증) //2. 이 코드로 구글에 access token 요청(권한) 3. 이 access token으로 구글에 해당 사용자의 정보(프로필 정보)에 접근할 수 있는 권한이 생김
            //4-1.이렇게 받아온 사용자 정보로 회원가입을 자동으로 시키기도 함
            //4-2.만약 해당 사용자 정보에 추가적인 정보가 필요하면(e,g. 쇼핑몰은 집 주소 필요) -> 자동이 아니라 추가적으로 회원가입 창이 나와서 가입시킴
            .userInfoEndpoint()//// OAuth2 로그인 성공 후 가져올 설정들
            .userService(principalOauth2UserService);
        
    }
   @Bean//bean으로 등록- 해당 메서드의 리턴되는 오브젝트를 IOC로 등록해줌, 그래서 다른 애들도 쓸 수 있음
   public BCryptPasswordEncoder encodePwd(){
        return new BCryptPasswordEncoder();
    }
}
