package vn.com.nimbus.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.data.domain.Users;
import vn.com.nimbus.common.data.domain.constant.UserSource;
import vn.com.nimbus.common.data.repository.UserRepository;
import vn.com.nimbus.common.exception.AppException;
import vn.com.nimbus.common.exception.AppExceptionCode;
import vn.com.nimbus.common.model.request.RegisterRequest;

import javax.annotation.Resource;
import java.util.Optional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserRepository userRepository;

    private final String DEFAULT_AVATAR = "https://i.stack.imgur.com/34AD2.jpg";

    @Override
    @Transactional
    public Mono<Object> register(RegisterRequest request) {
        Optional<Users> optUser = userRepository.findByEmail(request.getEmail());
        if (optUser.isPresent())
            throw new AppException(AppExceptionCode.EMAIL_HAS_EXISTS);

        if (StringUtils.isEmpty(request.getFirstName()) && StringUtils.isEmpty(request.getLastName()))
            throw new AppException(AppExceptionCode.FIRST_OR_LAST_NAME_CANT_BE_EMPTY);

        Users user = new Users();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAvatar(!StringUtils.isEmpty(request.getAvatar()) ? request.getAvatar() : DEFAULT_AVATAR);
        user.setSource(UserSource.NATIVE);
        user = userRepository.save(user);

        return Mono.just(user);
    }
}
