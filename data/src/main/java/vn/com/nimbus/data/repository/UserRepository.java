package vn.com.nimbus.data.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import vn.com.nimbus.data.domain.Users;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);
}
