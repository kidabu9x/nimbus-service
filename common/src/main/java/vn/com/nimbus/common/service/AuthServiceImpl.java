package vn.com.nimbus.common.service;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.data.domain.Users;
import vn.com.nimbus.common.data.repository.UserRepository;
import vn.com.nimbus.common.exception.AppException;
import vn.com.nimbus.common.exception.AppExceptionCode;
import vn.com.nimbus.common.model.constant.AuthType;
import vn.com.nimbus.common.model.request.AuthRequest;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserRepository userRepository;

    private final String DEFAULT_AVATAR = "https://i.stack.imgur.com/34AD2.jpg";

    private final String idAuth = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjZmY2Y0MTMyMjQ3NjUxNTZiNDg3NjhhNDJmYWMwNjQ5NmEzMGZmNWEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiMjcyOTg2ODE1MTY0LWFvYzhqbTgwcGgyMTZlYmNzaTI3YWJyamM2bm1icDA3LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiMjcyOTg2ODE1MTY0LWFvYzhqbTgwcGgyMTZlYmNzaTI3YWJyamM2bm1icDA3LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTEzMTkxOTg4NDU2NDExMjI3NDU3IiwiZW1haWwiOiJkdW9uZ25rLm5pbWJ1c0BnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXRfaGFzaCI6ImRsbWFnUnRSeTd0b1NwQUM0UU44dUEiLCJuYW1lIjoiRHVvbmcgTmd1eWVuIEtoYW5oIiwicGljdHVyZSI6Imh0dHBzOi8vbGg0Lmdvb2dsZXVzZXJjb250ZW50LmNvbS8tUzJCOUZfWHlqUFUvQUFBQUFBQUFBQUkvQUFBQUFBQUFBQUEvQUFLV0pKTjVfMnRUUUl6Rk5ibHdLSWNpLWdZSEZnUnV4dy9zOTYtYy9waG90by5qcGciLCJnaXZlbl9uYW1lIjoiRHVvbmciLCJmYW1pbHlfbmFtZSI6Ik5ndXllbiBLaGFuaCIsImxvY2FsZSI6InZpIiwiaWF0IjoxNTg2NzExMzA1LCJleHAiOjE1ODY3MTQ5MDUsImp0aSI6ImY5MDJjYmNjZWQxNmI5ODc3OTE4MTVmYmZhYjVmZDgwMDEwMDcwYjEifQ.kjC6xtdTNUeyLbeNnjB2TfkwCVADDoVRzE5Bgmq3V3-ZVyxAwGesYo1WuQHqccFvQj1GkpmDFLVYBoW-6439M1pJBGyvMJNWCmSb3ilTNcYAcK9uLYUfZKhTyCH7Ysr-BQrSqC_2ULEXQokZxqwujcu5Rp4h_zdRqNgy0XWq4rejWSzr-mjN-kUEKU7LXwq7luT0GIqxjnt2YMEOAoh6VuAOSvfQk-_BcBWAX4jlSWdmCVZ0gKSrEfNI1itcS2YEJgmfsblaLLA0Z6UiC46-sJ8nqKuz8jiCJCjMUr5XjQ28cOfNcRCXPSSZzLWE4f_9WeLm9wVYRzp4Wjqq5mtAQA";

    private final List<String> authTypes = Arrays.asList(AuthType.FACEBOOK, AuthType.GOOGLE, AuthType.NATIVE);

    private final String CLIENT_ID = "272986815164-aoc8jm80ph216ebcsi27abrjc6nmbp07.apps.googleusercontent.com";

    private static final JacksonFactory jsonFactory = new JacksonFactory();

    @Override
    @Transactional
    public Mono<Object> oauth(AuthRequest request) {
        if (!authTypes.contains(request.getType()))
            throw new AppException(AppExceptionCode.UNSUPPORTED_AUTH_TYPE);

        try {
            CountDownLatch latch = new CountDownLatch(1);
            latch.await();
            this.googleAuth();
            latch.countDown();
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(AppExceptionCode.INTERNAL_SERVER_ERROR);
        }

        return Mono.just(new Users());
    }

    private void googleAuth() throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(UrlFetchTransport.getDefaultInstance(), jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        GoogleIdToken idToken = verifier.verify("eyJhbGciOiJSUzI1NiIsImtpZCI6IjZmY2Y0MTMyMjQ3NjUxNTZiNDg3NjhhNDJmYWMwNjQ5NmEzMGZmNWEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiMjcyOTg2ODE1MTY0LWFvYzhqbTgwcGgyMTZlYmNzaTI3YWJyamM2bm1icDA3LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiMjcyOTg2ODE1MTY0LWFvYzhqbTgwcGgyMTZlYmNzaTI3YWJyamM2bm1icDA3LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTEzMTkxOTg4NDU2NDExMjI3NDU3IiwiZW1haWwiOiJkdW9uZ25rLm5pbWJ1c0BnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXRfaGFzaCI6ImRsbWFnUnRSeTd0b1NwQUM0UU44dUEiLCJuYW1lIjoiRHVvbmcgTmd1eWVuIEtoYW5oIiwicGljdHVyZSI6Imh0dHBzOi8vbGg0Lmdvb2dsZXVzZXJjb250ZW50LmNvbS8tUzJCOUZfWHlqUFUvQUFBQUFBQUFBQUkvQUFBQUFBQUFBQUEvQUFLV0pKTjVfMnRUUUl6Rk5ibHdLSWNpLWdZSEZnUnV4dy9zOTYtYy9waG90by5qcGciLCJnaXZlbl9uYW1lIjoiRHVvbmciLCJmYW1pbHlfbmFtZSI6Ik5ndXllbiBLaGFuaCIsImxvY2FsZSI6InZpIiwiaWF0IjoxNTg2NzExMzA1LCJleHAiOjE1ODY3MTQ5MDUsImp0aSI6ImY5MDJjYmNjZWQxNmI5ODc3OTE4MTVmYmZhYjVmZDgwMDEwMDcwYjEifQ.kjC6xtdTNUeyLbeNnjB2TfkwCVADDoVRzE5Bgmq3V3-ZVyxAwGesYo1WuQHqccFvQj1GkpmDFLVYBoW-6439M1pJBGyvMJNWCmSb3ilTNcYAcK9uLYUfZKhTyCH7Ysr-BQrSqC_2ULEXQokZxqwujcu5Rp4h_zdRqNgy0XWq4rejWSzr-mjN-kUEKU7LXwq7luT0GIqxjnt2YMEOAoh6VuAOSvfQk-_BcBWAX4jlSWdmCVZ0gKSrEfNI1itcS2YEJgmfsblaLLA0Z6UiC46-sJ8nqKuz8jiCJCjMUr5XjQ28cOfNcRCXPSSZzLWE4f_9WeLm9wVYRzp4Wjqq5mtAQA");
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            // Use or store profile information
            // ...

        } else {
            System.out.println("Invalid ID token.");
        }

    }
}
