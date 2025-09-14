package project.dev.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.dev.backend.dto.ExpenseDTO;
import project.dev.backend.dto.FilterDTO;
import project.dev.backend.dto.IncomeDTO;
import project.dev.backend.service.ExpenseService;
import project.dev.backend.service.IncomeService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
public class FilterController {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filter){
        LocalDate startDate = filter.getStartDate() != null ? filter.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filter.getEndDate() != null ? filter.getEndDate() : LocalDate.now();
        String keyword = filter.getKeyword() != null ? filter.getKeyword() : "";
        String sortField = filter.getSortField() != null ? filter.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);
        if ("income".equals(filter.getType())) {
            List<IncomeDTO> income = incomeService.filterIncome(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(income);
        } else if ("expense".equals(filter.getType())) {
            List<ExpenseDTO> expense = expenseService.filterExpense(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(expense);
        }else {
            return ResponseEntity.badRequest().body("Không đúng loại. Phải là 'income' hoặc 'expense'.");
        }
    }
}
