package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.repository.UserRepository;
import com.itsuda.perfume.security.CustomUserDetails;
import com.itsuda.perfume.security.factory.OAuth2UserInfo;
import com.itsuda.perfume.security.factory.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            return this.process(userRequest, super.loadUser(userRequest));
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw ex;
        }
    }

    public OAuth2User process(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        EProvider provider = EProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oauth2User.getAttributes());

        UserRepository.UserSecurityForm userDto = userRepository.findBySerialIdAndProvider(userInfo.getId(), provider)
                .orElseGet(() -> {
                    User user = userRepository.save(User.builder()
                            .serialId(userInfo.getId())
                            .provider(provider)
                            .role(ERole.GUEST)
                            // 사용자로부터 선택적으로 제공받는 정보
                            .email(userInfo.getEmail())
                            .imageUrl(userInfo.getProfileImageUrl())
                            .build());
                    return UserRepository.UserSecurityForm.invoke(user);
                });

        return CustomUserDetails.create(userDto, userInfo.getAttributes());
    }
}
