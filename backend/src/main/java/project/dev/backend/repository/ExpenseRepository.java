package project.dev.backend.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.dev.backend.entity.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    // select * from expenses where profile_id = ? order by date desc
    List<Expense> findByProfileIdOrderByDateDesc(Long profileId);

    // select * from expenses where profile_id = ? order by date desc limit 5
    List<Expense> findTop5ByProfileIdOrderByDateDesc(Long profileId);


    @Query("select sum(e.amount) from Expense e where e.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    // select * from expenses where profile_id = ? and date between ? and ? and name like %?%
    List<Expense> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
             Long profileId, LocalDate startDate, LocalDate endDate, String keyword, Sort sort);

    // select * from expenses where profile_id = ? and date between ? and ?
    List<Expense> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    //select * from expenses where profile_id = ? and date = ?
    List<Expense> findByProfileIdAndDate(Long profileId, LocalDate date);
}