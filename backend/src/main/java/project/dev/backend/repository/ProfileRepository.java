package project.dev.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.dev.backend.entity.Profile;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    //select * from profiles where email = ?
    Optional<Profile> findByEmail(String email);

    //select * from profiles where activation_token = ?
    Optional<Profile> findByActivationToken(String token);
}