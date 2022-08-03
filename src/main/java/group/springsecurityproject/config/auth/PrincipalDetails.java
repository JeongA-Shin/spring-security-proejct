package group.springsecurityproject.config.auth;

//현재 security config 파일에 설정했듯이
// /loginProc로 요청이 오면 spring security가 낚아채서 로그인을 진행시킴
// 로그인 진행 완료가 되면 security session을(일반적인 session이 아니라 security session임) 만들어줌
//  - 그리고 이 security sesssion은 "Security Context Holder"에 담김
// 그리고 여기서 이 "security session의 내용의 자료형"을 authentication(객체임. object)라고 함
// 그리고 당연히 이 authentication 안에는 유저 정보가 있음
//   - 그런데 이 때 유저 정보는 UserDetails라는 오브젝트여야 함

import group.springsecurityproject.model.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

//즉 정리해보자면
// Seuciry Session(Security Context Holder에 담겨짐) 안에는 Authentication 객체만 들어갈 수 있음
// 그리고 이 Authentication 안에는 UserDetails 객체(자체 로그인시)와 Oauth2User(Oauth 로그인시)만 들어갈 수 있는 거임
// 그런데 이렇게되어버리면 각 로그인마다 파라미터를 다르게 설정해줘야 하므로 컨트롤러 반복이 너무 심해짐
// 그래서 원래는 UserDetails만 implements되도록 되어있었는데, PrincipalDetails가 Oauth2User도 implements 하도록 해주자
//이렇게 되면 Oauth로 로그인하든, 자체 로그인을 핟느 authentication 안에는 principalDetails 하나로 퉁쳐서 들어가게 됨
//즉 자료형을 하나로 통합?되는 것처럼 할 수 있게 됨

//그리고 이 파일은 UserDetails와 OAuth2 객체를 만드는 로직임.
//authentication 만드는 과정은 PrincipalDetailsService 참조
@Data
public class PrincipalDetails implements UserDetails, OAuth2User {
    
    //컴포지션
    //authenticatoin안에는 userDetails 혹은 Oauth2User만 들어갈 수 있음
    //그런데 UserDetails에는 내가 만든 user객체에 대한 정보가 없음
    //그래서 이렇게 PrincipalDetails를 만들고 user를 컴포지션해줌으로서 userDetails 안에 내가 만든 user객체를 품을 수 있게 해줌
    private User user; //상속은 is a 관계, 컴포지션은 개체들 간의 has a 관계입니다.
    //상속은 클래스를 확장해 부모 클래스에서 속성 및 동작을 상속하는 기능이구요. 컴포지션은 클래스가 구성원 데이터로 다른 클래스의 객체를 포함할 수 있는 능력입니다.
    
    private Map<String,Object> attributes;
    
    //일반 로그인에 쓰임
    public PrincipalDetails(User user){
        this.user=user;
    }
    //OAuth 로그인에 쓰임
    public PrincipalDetails(User user,Map<String,Object> attributes){
        this.user=user;
        this.attributes=attributes;
    }
    
    
    @Override
    public Map<String, Object> getAttributes() { //OAuth2Usr에 해당되는 메서드
        return attributes;
    }
    
    @Override
    //해당 User의 권한을 리턴하는 함수
    public Collection<? extends GrantedAuthority> getAuthorities() {
        /*
        사실 원래는 그냥 user.getRole을 해주면 됨
        근데 현재 user의 role은 string타입임
        해당 메서드의 반환형은 Collection<? extends GrantedAuthority>으로 맞춰져 있으므로 - 오버라이드이니까
        반환형을 맞추어 줘야 함
        * */
        
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        
        return collect;
    }
    
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    @Override
    public String getUsername() {
        return user.getUsername();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        
        //만약 해당 서비스에 1년동안 회원이 로그인을 안 하면 휴면 계정으로 전환해야 함
        // 유저 컬럼에 로그인 당시의 시간 컬럼을 추가하고 user.getLoginDate();
        // 그리고 현재 시간과 user.getLoginDate();을 비교해서 1년이 지나면 false를 반환
        // 아니면 true를 반환
        
        return true;
    }
    
    @Override
    public String getName() { ////OAuth2Usr에 해당되는 메서드
        return (String) attributes.get("sub"); //사실 별로 안 중요해서 그냥 null로 일단 강의에서는 처리함
    }
}
