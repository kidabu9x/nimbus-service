package vn.com.nimbus.common.data.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import vn.com.nimbus.common.data.domain.Users;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);
}
