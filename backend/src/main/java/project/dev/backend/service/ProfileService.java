package project.dev.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.dev.backend.dto.AuthDTO;
import project.dev.backend.dto.ProfileDTO;
import project.dev.backend.entity.Profile;
import project.dev.backend.repository.ProfileRepository;
import project.dev.backend.util.JwtUtil;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${backend.url}")
    private String backendUrl;

    // Đăng ký tài khoản
    public ProfileDTO registerProfile(ProfileDTO profileDTO){
        Profile newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);
        // Gửi activation email
        String activationLink = backendUrl + "/api/v1/activate?token=" + newProfile.getActivationToken();
        String subject = "Kích hoạt tài khoản Personal Manager";
        String body = "Nhấn vào đường link để kích hoạt tài khoản của bạn: "+ activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject, body);
        return toDTO(newProfile);
    }
    public Profile toEntity(ProfileDTO profileDTO){
        return Profile.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                // Mã hóa mật khẩu
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImgUrl(profileDTO.getProfileImgUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(Profile profile){
        return ProfileDTO.builder()
                .id(profile.getId())
                .fullName(profile.getFullName())
                .email(profile.getEmail())
                .profileImgUrl(profile.getProfileImgUrl())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    // Kích hoạt tài khoản
    public boolean activateProfile(String token){
        return profileRepository.findByActivationToken(token)
                .map(profile ->{
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }
    // Kiểm tra tài khoản đã kích hoạt chưa
    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(Profile::getIsActive)
                .orElse(false);
    }

    public Profile getCurrentProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return profileRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));
    }

    public ProfileDTO getPublicProfile(String email){
        Profile currentUser = null;
        if(email == null){
            currentUser = getCurrentProfile();
        }else {
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));
        }
        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImgUrl(currentUser.getProfileImgUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
            // Tạo JWT token
            String token = jwtUtil.generateToken(authDTO.getEmail());
            return Map.of(
                    "token", token,
                    "user",getPublicProfile(authDTO.getEmail())
            );
        }catch (Exception e){
            throw new RuntimeException("Sai email hoặc mật khẩu!");
        }
    }
}
