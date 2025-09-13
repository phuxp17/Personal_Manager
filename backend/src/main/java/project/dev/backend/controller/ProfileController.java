package project.dev.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.dev.backend.dto.AuthDTO;
import project.dev.backend.dto.ProfileDTO;
import project.dev.backend.service.ProfileService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO){
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }
    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token){
        boolean isActivated = profileService.activateProfile(token);
        if (isActivated){
            return ResponseEntity.ok("Kích hoạt tài khoản thành công!");
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token không tìm thấy hoặc đã hết hạn!");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody AuthDTO authDTO){
        try {
            if(!profileService.isAccountActive(authDTO.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message","Tài khoản chưa được kích hoạt! Vui lòng kiểm tra email để kích hoạt tài khoản."));
            }
            Map<String, Object> response=profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", e.getMessage()));
        }
    }
    @GetMapping("/test")
    public String test(){
        return "Test successful";
    }
}
