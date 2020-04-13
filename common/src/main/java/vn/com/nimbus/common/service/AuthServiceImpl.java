package vn.com.nimbus.common.service;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.config.ConfigData;
import vn.com.nimbus.common.config.ConfigLoader;
import vn.com.nimbus.common.data.domain.Users;
import vn.com.nimbus.common.data.domain.constant.UserSource;
import vn.com.nimbus.common.data.repository.UserRepository;
import vn.com.nimbus.common.exception.AppException;
import vn.com.nimbus.common.exception.AppExceptionCode;
import vn.com.nimbus.common.model.constant.AuthType;
import vn.com.nimbus.common.model.request.AuthRequest;
import vn.com.nimbus.common.model.request.RegisterRequest;
import vn.com.nimbus.common.model.response.OauthResponse;
import vn.com.nimbus.common.model.response.ProfileResponse;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final List<String> authTypes = Arrays.asList(AuthType.FACEBOOK, AuthType.GOOGLE, AuthType.NATIVE);
    private static final JacksonFactory jsonFactory = new JacksonFactory();

    @Resource
    private UserRepository userRepository;

    @Resource
    private TokenService tokenService;

    @Override
    @Transactional
    public Mono<OauthResponse> oauth(AuthRequest request) {
//        if (!authTypes.contains(request.getType()))
//            throw new AppException(AppExceptionCode.UNSUPPORTED_AUTH_TYPE);
//
//        if (StringUtils.isEmpty(request.getToken()))
//            throw new AppException(AppExceptionCode.INVALID_TOKEN_ID);
//
//        try {
//            Users user = this.storeUser(request.getToken());
//            String accessToken = tokenService.createJWT(user);
//            OauthResponse response = new OauthResponse();
//            response.setAccessToken(accessToken);
//            return Mono.just(response);
//        } catch (NoSuchAlgorithmException e) {
//            log.error("[auth] NoSuchAlgorithmException, ex: {}", e.getMessage());
//            throw new AppException(AppExceptionCode.INTERNAL_SERVER_ERROR);
//        } catch (InvalidKeySpecException e) {
//            log.error("[auth] InvalidKeySpecException, ex: {}", e.getMessage());
//            throw new AppException(AppExceptionCode.INTERNAL_SERVER_ERROR);
//        }
        return null;
    }

    @Override
    @Transactional
    public Mono<OauthResponse> register(RegisterRequest request) {
        if (!authTypes.contains(request.getType()))
            throw new AppException(AppExceptionCode.UNSUPPORTED_AUTH_TYPE);

        Users user;
        Optional<Users> optUser = userRepository.findByEmail(request.getEmail());
        if (optUser.isPresent()) {
            user = optUser.get();
            user.setAvatar(request.getAvatar());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
        } else {
            user = new Users();
            user.setAvatar(request.getAvatar());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setSource(UserSource.valueOf(request.getType()));
        }
        user = userRepository.save(user);

        String accessToken = tokenService.createJwt(user);
        OauthResponse response = new OauthResponse();
        response.setAccessToken(accessToken);
        return Mono.just(response);
    }

    private Users storeUser(String token) {
        Users user = this.googleAuth(token);
        Optional<Users> optUser = userRepository.findByEmail(user.getEmail());
        if (optUser.isPresent()) {
            Users existUser = optUser.get();
            user.setId(existUser.getId());
            user.setCreatedAt(existUser.getCreatedAt());
            user.setUpdatedAt(LocalDateTime.now());
        }
        user = userRepository.save(user);
        return user;
    }

    private Users googleAuth(String token) {
        ConfigData.GoogleOauthConfig googleOauthConfig = ConfigLoader.getInstance().configData.getGoogleOauthConfig();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(UrlFetchTransport.getDefaultInstance(), jsonFactory)
                .setAudience(Collections.singletonList(googleOauthConfig.getClientId()))
                .build();
        try {
            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String userId = payload.getSubject();

                // Get profile information from payload
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                String familyName = (String) payload.get("family_name");
                String givenName = (String) payload.get("given_name");

                Users user = new Users();
                user.setAvatar(pictureUrl);
                user.setEmail(email);
                user.setIdRef(userId);
                user.setSource(UserSource.GOOGLE);
                user.setFirstName(familyName);
                user.setLastName(givenName);
                return user;
            } else {
                log.warn("[auth] Invalid token id: {}", token);
                throw new AppException(AppExceptionCode.BLOG_NOT_FOUND);
            }
        } catch (GeneralSecurityException e) {
            log.warn("[auth] security exception, ex: {}", e.getMessage());
            throw new AppException(AppExceptionCode.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            log.warn("[auth] io exception, ex: {}", e.getMessage());
            throw new AppException(AppExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Mono<ProfileResponse> getProfile(Integer userId) {
        log.info("Get user profile by id: {}", userId);
        Optional<Users> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            log.warn("User {} not found", userId);
            throw new AppException(AppExceptionCode.USER_NOT_FOUND);
        }

        Users user = userOpt.get();
        ProfileResponse profile = new ProfileResponse();
        profile.setId(user.getId());
        profile.setAvatar(user.getAvatar());
        profile.setEmail(user.getEmail());
        profile.setFirstName(user.getFirstName());
        profile.setLastName(user.getLastName());

        return Mono.just(profile);
    }


    private Map<String, Object> generateTokenPayload(Users user) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("user_id", user.getId());
        return payload;
    }
}
