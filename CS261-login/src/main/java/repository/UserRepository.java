package repository;

import model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndCitizenId(String email, String citizenId);
    Optional<User> findByEmail(String email);
}
