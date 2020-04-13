package vn.com.nimbus.common.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.stereotype.Service;
import vn.com.nimbus.common.config.ConfigData;
import vn.com.nimbus.common.config.ConfigLoader;
import vn.com.nimbus.common.data.domain.Users;
import vn.com.nimbus.common.model.constant.KeyConstant;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenServiceImpl implements TokenService {

    private final String SECRET = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=";


    @Override
    public String createJwt(Users user) {
        ConfigData.OauthKey oauthConfig = ConfigLoader.getInstance().configData.getOauthKey();

        Map<String, Object> payload = this.generateTokenPayload(user);

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder().setId(user.getId().toString())
                .setIssuedAt(now)
                .addClaims(payload)
                .signWith(SignatureAlgorithm.HS256, TextCodec.BASE64.decode(SECRET));

        if (oauthConfig.getTtl() >= 0) {
            long expMillis = nowMillis + oauthConfig.getTtl();
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    @Override
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(TextCodec.BASE64.decode(SECRET))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }


    private Map<String, Object> generateTokenPayload(Users user) {
        Map<String, Object> payload = new HashMap<>();
        payload.put(KeyConstant.USER_ID, user.getId());
        return payload;
    }


    private PrivateKey getPrivateKey(ConfigData.OauthKey oauthKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(this.pemToDer(oauthKey.getPrivateKey()));

        return kf.generatePrivate(keySpecPKCS8);
    }

    private PublicKey getPublicKey(ConfigData.OauthKey oauthKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(this.pemToDer(oauthKey.getPrivateKey()));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }


    private byte[] pemToDer(String pem) {
        return Base64.getDecoder().decode(stripBeginEnd(pem));
    }

    private String stripBeginEnd(String pem) {
        String stripped = pem.replaceAll("-----BEGIN (.*)-----", "");
        stripped = stripped.replaceAll("-----END (.*)----", "");
        stripped = stripped.replaceAll("\r\n", "");
        stripped = stripped.replaceAll("\n", "");
        return stripped.trim();
    }
}
