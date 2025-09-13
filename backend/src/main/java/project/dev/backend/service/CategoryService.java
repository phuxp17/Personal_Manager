package project.dev.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import project.dev.backend.dto.CategoryDTO;
import project.dev.backend.entity.Category;
import project.dev.backend.entity.Profile;
import project.dev.backend.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    // Tạo category
    public CategoryDTO saveCategory(CategoryDTO categoryDTO){
        Profile profile = profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Danh mục này đã tồn tại!");
        }
        Category newCategory = toEntity(categoryDTO, profile);
        newCategory = categoryRepository.save(newCategory);
        return toDTO(newCategory);
    }

    private Category toEntity(CategoryDTO categoryDTO, Profile profile){
        return Category.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .type(categoryDTO.getType())
                .profile(profile)
                .build();
    }
    private CategoryDTO toDTO(Category category){
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .icon(category.getIcon())
                .type(category.getType())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .profileId(category.getProfile() != null ? category.getProfile().getId() : null)
                .build();
    }
}
