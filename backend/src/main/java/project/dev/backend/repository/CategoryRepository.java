package project.dev.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.dev.backend.entity.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // select * from categories where profile_id = ?
    List<Category> findByProfileId(Long profileId);

    // select * from categories where id = ? and profile_id = ?
    Optional<Category> findByIdAndProfileId(Long id, Long profileId);

    // select * from categories where type = ? and profile_id = ?
    List<Category> findByTypeAndProfileId(String type, Long profileId);

    Boolean existsByNameAndProfileId(String name, Long profileId);
}