package project.dev.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import project.dev.backend.dto.ExpenseDTO;
import project.dev.backend.entity.Category;
import project.dev.backend.entity.Expense;
import project.dev.backend.entity.Profile;
import project.dev.backend.repository.CategoryRepository;
import project.dev.backend.repository.ExpenseRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    // Thêm khoản chi
    public ExpenseDTO addExpense(ExpenseDTO dto){
        Profile profile = profileService.getCurrentProfile();
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục."));
        Expense newExpense = toEntity(dto, profile, category);
        newExpense = expenseRepository.save(newExpense);
        return toDTO(newExpense);
    }
    // Tất cả khoản chi cho tháng/dựa trên mốc thời gian
    public List<ExpenseDTO> getCurrentMonthExpenseForCurrentUser(){
        Profile profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<Expense> list = expenseRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }
    // Xóa khoản chi
    public void deleteExpense(Long expenseId){
        Profile profile = profileService.getCurrentProfile();
        Expense entity = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khoản chi."));
        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("không được phép xóa khoản phi này");
        }
        expenseRepository.delete(entity);
    }

    // Danh sách 5 khoản chi gần nhất
    public List<ExpenseDTO> getLatest5ExpenseForUser(){
        Profile profile = profileService.getCurrentProfile();
        List<Expense> list = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    //Lấy tổng khoản chi
    public BigDecimal getTotalExpensesForUser(){
        Profile profile = profileService.getCurrentProfile();
        BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    // filter khoản  chi
    public List<ExpenseDTO> filterExpense (LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        Profile profile = profileService.getCurrentProfile();
        List<Expense> list = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return list.stream().map(this::toDTO).toList();
    }

    //Notifications
    public List<ExpenseDTO> getExpenseOnDateForUser(Long profileId, LocalDate date){
        List<Expense> list = expenseRepository.findByProfileIdAndDate(profileId, date);
        return list.stream().map(this::toDTO).toList();
    }

    //toDTO toEntity
    private Expense toEntity(ExpenseDTO dto, Profile profile, Category category){
        return Expense.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private ExpenseDTO toDTO(Expense expense){
        return ExpenseDTO.builder()
                .id(expense.getId())
                .name(expense.getName())
                .icon(expense.getIcon())
                .amount(expense.getAmount())
                .date(expense.getDate())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .categoryId(expense.getCategory() != null ? expense.getCategory().getId() : null)
                .categoryName(expense.getCategory().getName())
                .build();
    }
}
