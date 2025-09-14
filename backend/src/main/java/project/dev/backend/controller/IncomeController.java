package project.dev.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.dev.backend.dto.IncomeDTO;
import project.dev.backend.service.IncomeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/income")
public class IncomeController {


    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO dto){
        IncomeDTO createdIncome = incomeService.addIncome(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIncome);
    }
    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getExpense(){
        List<IncomeDTO> income = incomeService.getCurrentMonthIncomeForCurrentUser();
        return ResponseEntity.ok(income);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id){
        incomeService.deleteIncome(id);
        return ResponseEntity.noContent().build();
    }

}
