package project.dev.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.dev.backend.dto.ExpenseDTO;
import project.dev.backend.service.ExpenseService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO dto){
        ExpenseDTO createdExpense = expenseService.addExpense(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getExpense(){
        List<ExpenseDTO> expense = expenseService.getCurrentMonthExpenseForCurrentUser();
        return ResponseEntity.ok(expense);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id){
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
