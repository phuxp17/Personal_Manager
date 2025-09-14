package project.dev.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import project.dev.backend.dto.ExpenseDTO;
import project.dev.backend.dto.IncomeDTO;
import project.dev.backend.entity.Category;
import project.dev.backend.entity.Expense;
import project.dev.backend.entity.Income;
import project.dev.backend.entity.Profile;
import project.dev.backend.repository.CategoryRepository;
import project.dev.backend.repository.IncomeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    // Thêm khoản thu
    public IncomeDTO addIncome(IncomeDTO dto){
        Profile profile = profileService.getCurrentProfile();
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục."));
        Income newIncome = toEntity(dto, profile, category);
        newIncome = incomeRepository.save(newIncome);
        return toDTO(newIncome);
    }
    // Tất cả khoản thu cho tháng/dựa trên mốc thời gian
    public List<IncomeDTO> getCurrentMonthIncomeForCurrentUser(){
        Profile profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<Income> list = incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }
    // Xóa khoản chi
    public void deleteIncome(Long incomeId){
        Profile profile = profileService.getCurrentProfile();
        Income entity = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khoản thu nhập."));
        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("không được phép xóa khoản thu nhập này");
        }
        incomeRepository.delete(entity);
    }
    // Danh sách 5 khoản thu gần nhất
    public List<IncomeDTO> getLatest5IncomeForUser(){
        Profile profile = profileService.getCurrentProfile();
        List<Income> list = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    //Lấy tổng khoản thu
    public BigDecimal getTotalIncomesForUser(){
        Profile profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalIncomeByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    // filter khoản  thu
    public List<IncomeDTO> filterIncome (LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        Profile profile = profileService.getCurrentProfile();
        List<Income> list = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return list.stream().map(this::toDTO).toList();
    }

    //toDTO toEntity
    private Income toEntity(IncomeDTO dto, Profile profile, Category category){
        return Income.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private IncomeDTO toDTO(Income income){
        return IncomeDTO.builder()
                .id(income.getId())
                .name(income.getName())
                .icon(income.getIcon())
                .amount(income.getAmount())
                .date(income.getDate())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .categoryId(income.getCategory() != null ? income.getCategory().getId() : null)
                .categoryName(income.getCategory().getName())
                .build();
    }
}
