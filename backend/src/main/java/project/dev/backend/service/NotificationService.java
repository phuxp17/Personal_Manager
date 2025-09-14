package project.dev.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import project.dev.backend.dto.ExpenseDTO;
import project.dev.backend.entity.Profile;
import project.dev.backend.repository.ProfileRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Ho_Chi_Minh")
    public void sendDailyIncomeExpenseReminder(){
        log.info("Công việc bắt đầu: sendDailyIncomeExpenseReminder()");
        List<Profile> profiles = profileRepository.findAll();
        for (Profile profile: profiles) {
            String body = "Chào " + profile.getFullName() + ",<br><br>"
                    + "Đừng quên cập nhật các khoản thu chi của bạn hôm nay!<br>"
                    + "Quản lý  cá nhân dễ dàng hơn với ứng dụng của chúng tôi.<br><br>"
                    + "<a href="+ frontendUrl +" style='display:inline-block;padding:10px 20px; background-color:#4CAF50;colors:#ffffff;text-decoration:none;border-radius:5px;font-weight:bold;'>Nhấn vào đây để cập nhật ngay!</a><br><br>"
                    + "Trân trọng, Personal Manager<br>";
            emailService.sendEmail(profile.getEmail(), "Nhắc nhở cập nhật thu chi hàng ngày", body);
        }
    }

    @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Ho_Chi_Minh")
    public void sendDailyExpenseSummary() {
        log.info("Công việc bắt đầu: sendDailyExpenseSummary()");
        List<Profile> profiles = profileRepository.findAll();
        for (Profile profile : profiles) {
            List<ExpenseDTO> todayExpense = expenseService.getExpenseOnDateForUser(profile.getId(), LocalDate.now());
            if(!todayExpense.isEmpty()){
                StringBuilder table = new StringBuilder();
                table.append("<table style='border-collapse: collapse; width: 100%;'>");
                table.append("<tr style='background-color: #f2f2f2;'><th style='border:1px solid #ddd; padding: 8px;'>No</th><th style='border:1px solid #ddd; padding: 8px;'>Tên</th><th style='border:1px solid #ddd; padding: 8px;'>Số tiền</th><th style='border:1px solid #ddd; padding: 8px;'>Phân loại</th></tr>");
            int i =1;
            for (ExpenseDTO expense : todayExpense) {
                table.append("<tr>");
                table.append("<td style='border:1px solid #ddd; padding:8px;'>").append(i++).append("</td>");
                table.append("<td style='border:1px solid #ddd; padding:8px;'>").append(expense.getName()).append("</td>");
                table.append("<td style='border:1px solid #ddd; padding:8px;'>").append(expense.getAmount()).append("</td>");
                table.append("<td style='border:1px solid #ddd; padding:8px;'>").append(expense.getCategoryId() != null ? expense.getCategoryName() : "N/A").append("</td>");
            }
            table.append("</table>");
            String body = "Chào " + profile.getFullName() + ",<br><br>"
                    + "Dưới đây là tóm tắt các khoản chi của bạn trong ngày " + LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")) + ":<br><br>"
                    + table + "<br>"
                    + "Hãy tiếp tục theo dõi và quản lý tài chính cá nhân của bạn một cách hiệu quả!<br><br>"
                    + "Trân trọng, Personal Manager<br>";
            emailService.sendEmail(profile.getEmail(), "Tóm tắt khoản chi trong ngày", body);
            }
            log.info("Công việc kết thúc: sendDailyExpenseSummary()");
        }
    }
}
