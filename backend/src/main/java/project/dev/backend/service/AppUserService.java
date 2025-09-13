package project.dev.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.dev.backend.entity.Profile;
import project.dev.backend.repository.ProfileRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {
    private final ProfileRepository profileRepository;

    // Lấy dữ liệu người dùng từ database
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Profile existedProfile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));
        return User.builder()
                .username(existedProfile.getEmail())
                .password(existedProfile.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}
