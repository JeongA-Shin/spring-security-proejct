package group.springsecurityproject.config.auth;

import group.springsecurityproject.model.User;
import group.springsecurityproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//이 파일은 authentication 객체를 만드는 로직임.
//userDeatils 만드는 과정은 PrincipalDetailsService 참조

//security config에서 loginProcessingUrl("/loginProc");을 설정함.
// 따라서 /loginProc 요청이 오면 자동으로 UserDetailsService를 implements하고, IOC에 등록되어 있는 서비스를 찾음
//그리고 또 자동으로 loadUserByUsername 함수가 실행이 됨
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository; //컴포지션
    
    /**
     * 해당 메서드가 실행 후 종료시 @AuthenticationPrincipal 어노테이션이 활성화됨 (만들어짐) == authentication 객체가 생성됨
     * @param username 로그인 요청시 form에서 username 네이밍이 일치해야 함
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findByUsername(username);
        if(userEntity != null){ //해당되는 유저가 있다면
            return new PrincipalDetails(userEntity); // UserDetails 안에는 유저 정보(유저 객체)가 들어가야 함.
            // 그리고 UserDetails 객체가 생성되면(리턴되면) 시큐리티가 자동으로 얘를 authentication으로 감쌈
            // 결론적으로 (자동으로)authentication이 만들어짐
            // 그리고 해당 유저정보는 자동으로 security session에 담김
        }
        return null; //없다면
    }
}
