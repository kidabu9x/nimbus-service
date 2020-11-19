package vn.com.nimbus.data.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import vn.com.nimbus.data.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
