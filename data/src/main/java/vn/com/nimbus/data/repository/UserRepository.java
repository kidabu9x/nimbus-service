package vn.com.nimbus.data.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import vn.com.nimbus.data.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
