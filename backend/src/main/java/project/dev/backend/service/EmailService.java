package project.dev.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EmailService {
    // Use RestTemplate to call Brevo's HTTP API instead of JavaMailSender
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    @Value("${brevo.api-key}")
    private String brevoApiKey;

    private static final String BREVO_SEND_URL = "https://api.brevo.com/v3/smtp/email";

    public void sendEmail(String toAddress, String subject, String body){
        try {
            // Build headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("api-key", brevoApiKey);

            // Build payload according to Brevo API
            Map<String, Object> payload = new HashMap<>();
            Map<String, String> sender = new HashMap<>();
            sender.put("email", fromEmail);
            payload.put("sender", sender);

            List<Map<String, String>> to = new ArrayList<>();
            Map<String, String> toEntry = new HashMap<>();
            toEntry.put("email", toAddress);
            to.add(toEntry);
            payload.put("to", to);

            payload.put("subject", subject);
            payload.put("textContent", body);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_SEND_URL, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Brevo API error: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (Exception e){
            throw new RuntimeException("Failed to send email via Brevo: " + e.getMessage(), e);
        }
    }
}