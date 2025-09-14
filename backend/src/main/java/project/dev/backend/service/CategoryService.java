package project.dev.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import project.dev.backend.dto.CategoryDTO;
import project.dev.backend.entity.Category;
import project.dev.backend.entity.Profile;
import project.dev.backend.repository.CategoryRepository;

import java.util.List;

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
    // Lấy dữ liệu danh mục cho người dùng
     public List<CategoryDTO> getCategoriesForCurrentUser(){
        Profile profile = profileService.getCurrentProfile();
        List<Category>  categories = categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDTO).toList();
     }
    // Lấy dữ liệu dạnh mục theo loại cho người dùng
    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type){
        Profile profile = profileService.getCurrentProfile();
        List<Category> categories = categoryRepository.findByTypeAndProfileId(type, profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }
    //
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO dto){
        Profile profile = profileService.getCurrentProfile();
        Category existedCategory = categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục hoặc không thể truy cập!"));
        existedCategory.setName(dto.getName());
        existedCategory.setIcon(dto.getIcon());
        existedCategory.setType(dto.getType());
        categoryRepository.save(existedCategory);
        return toDTO(existedCategory);
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
