package group.springsecurityproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity //spring security 필터(여기서 만드는 SecurityConfig)가 spring 필터 체인에 등록이 됨
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
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
            .loginPage("/login"); //정의된 로그인 페이지, 인증이 필요할 때 이동하는 페이지 설정하는 api입니다
        
    }
}
