package group.springsecurityproject.repository;

import group.springsecurityproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //얘가 없어도 IOC가 됨. 왜냐면 JpaRepository를 상속했기 때문에
public interface UserRepository extends JpaRepository<User,Integer> {

}
