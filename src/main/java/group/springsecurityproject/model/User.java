package group.springsecurityproject.model;

import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String email;
    private String role; //ROLE_USER, ROLE_ADMIN
    
    private String provider; //oauth 로그인시 어느 소셜로 로그인했는지. e.g. google, naver...
    private String providerId; // provider에서 제공한 사용자 pk (즉 "구글"사용자 정보에서의 pk임. 우리 자체 사용자의 pk가 아니라)
    
    @CreationTimestamp
    private Timestamp createdDate;
    
    @Builder //생성자의 인수가 많으므로 builder 패턴활용
    public User(String username, String password, String email, String role,
        String provider,String providerId,Timestamp createdDate){
        this.username= username;
        this.password=password;
        this.email=email;
        this.role=role;
        this.provider=provider;
        this.providerId = providerId;
        this.createdDate=createdDate;
    }
    
}
