package com.app.weather.global.jwt.custom;

import com.app.weather.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String uuid) throws UsernameNotFoundException {
        return new CustomUserDetails(userRepository.findByUuid(uuid).orElseThrow());//커스텀 예외 추가 (UserNotFoundException::new)
    }
}
