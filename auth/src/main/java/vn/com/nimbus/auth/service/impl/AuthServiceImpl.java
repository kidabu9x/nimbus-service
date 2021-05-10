package vn.com.nimbus.auth.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.com.nimbus.auth.model.mapper.AuthMapper;
import vn.com.nimbus.auth.model.request.OauthRequest;
import vn.com.nimbus.auth.model.response.AuthResponse;
import vn.com.nimbus.auth.model.response.ProfileResponse;
import vn.com.nimbus.auth.service.AuthService;
import vn.com.nimbus.common.model.error.ErrorCode;
import vn.com.nimbus.common.model.exception.BaseException;
import vn.com.nimbus.common.service.JwtService;
import vn.com.nimbus.data.domain.User;
import vn.com.nimbus.data.domain.constant.UserSource;
import vn.com.nimbus.data.repository.UserRepository;

import java.util.Optional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponse oauth(OauthRequest request) {
        if (request == null) {
            throw new BaseException(ErrorCode.INVALID_PARAMETERS);
        }
        if (StringUtils.isEmpty(request.getFirstName()) && StringUtils.isEmpty(request.getLastName())) {
            throw new BaseException(ErrorCode.INVALID_PARAMETERS, "first or last name cannot be empty");
        }
        request.setEmail(request.getEmail().trim());

        User user = new User();
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            user = userOpt.get();
        }
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        final String DEFAULT_AVATAR = "https://i.stack.imgur.com/34AD2.jpg";
        user.setAvatar(!StringUtils.isEmpty(request.getAvatar()) ? request.getAvatar() : DEFAULT_AVATAR);
        user.setSource(UserSource.NATIVE);
        user = userRepository.save(user);

        String token = jwtService.createJwt(user);
        AuthResponse response = new AuthResponse();
        response.setAccessToken(token);
        return response;
    }

    @Override
    public ProfileResponse getProfile(Long id) {
        if (id == null) {
            throw new BaseException(ErrorCode.INVALID_PARAMETERS);
        }
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return AuthMapper.INSTANCE.toProfileResponse(userOpt.get());
    }
}
