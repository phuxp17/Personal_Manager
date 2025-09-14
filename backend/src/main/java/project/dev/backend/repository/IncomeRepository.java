package project.dev.backend.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import project.dev.backend.entity.Expense;
import project.dev.backend.entity.Income;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    // select * from incomes where profile_id = ? order by date desc
    List<Income> findByProfileIdOrderByDateDesc(Long profileId);

    // select * from incomes where profile_id = ? order by date desc limit 5
    List<Income> findTop5ByProfileIdOrderByDateDesc(Long profileId);


    @Query("select sum(e.amount) from Expense e where e.profile.id = :profileId")
    BigDecimal findTotalIncomeByProfileId(@Param("profileId") Long profileId);

    // select * from incomes where profile_id = ? and date between ? and ? and name like %?%
    List<Income> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId, LocalDate startDate, LocalDate endDate, String keyword, Sort sort);

    // select * from incomes where profile_id = ? and date between ? and ?
    List<Income> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    //select * from incomes where profile_id = ? and date = ?
    List<Income> findByProfileIdAndDate(Long profileId, LocalDate date);
}