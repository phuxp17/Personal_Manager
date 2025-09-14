package project.dev.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.dev.backend.dto.ExpenseDTO;
import project.dev.backend.dto.IncomeDTO;
import project.dev.backend.dto.RecentTransactionDTO;
import project.dev.backend.entity.Profile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String, Object> getDashboardData(){
        Profile profile = profileService.getCurrentProfile();
        Map<String, Object> returnValue = new LinkedHashMap<>();
        List<IncomeDTO> latestIncome = incomeService.getLatest5IncomeForUser();
        List<ExpenseDTO> latestExpense = expenseService.getLatest5ExpenseForUser();
        List<RecentTransactionDTO> recentTransactions = concat(latestIncome.stream().map(income ->
                RecentTransactionDTO.builder()
                        .id(income.getId())
                        .profileId(profile.getId())
                        .icon(income.getIcon())
                        .name(income.getName())
                        .amount(income.getAmount())
                        .date(income.getDate())
                        .updatedAt(income.getUpdatedAt())
                        .createdAt(income.getCreatedAt())
                        .type("income")
                        .build()),
                latestExpense.stream().map(expense ->
                        RecentTransactionDTO.builder()
                                .id(expense.getId())
                                .profileId(profile.getId())
                                .icon(expense.getIcon())
                                .amount(expense.getAmount())
                                .name(expense.getName())
                                .date(expense.getDate())
                                .createdAt(expense.getCreatedAt())
                                .updatedAt(expense.getUpdatedAt())
                                .type("expense")
                                .build()))
                                .sorted((a,b) ->{
                                    int cmp = b.getDate().compareTo(a.getDate());
                                    if(cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null){
                                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                                    }
                                    return cmp;
                                }).collect(Collectors.toList());
        returnValue.put("totalBalance", incomeService.getTotalIncomesForUser()
                .subtract(expenseService.getTotalExpensesForUser()));
        returnValue.put("totalIncome", incomeService.getTotalIncomesForUser());
        returnValue.put("totalExpense", expenseService.getTotalExpensesForUser());
        returnValue.put("recent5Expenses", latestExpense);
        returnValue.put("recent5Incomes", latestIncome);
        returnValue.put("recentTransactions", recentTransactions);
        return returnValue;
    }
}
