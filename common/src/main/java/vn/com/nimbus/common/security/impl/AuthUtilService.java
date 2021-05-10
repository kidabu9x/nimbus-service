package vn.com.nimbus.common.security.impl;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.com.nimbus.common.security.AuthUtil;
import vn.com.nimbus.common.service.JwtService;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class AuthUtilService implements AuthUtil {

    private final JwtService jwtService;

    @Autowired
    public AuthUtilService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    private PublicKey getPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        return this.loadPubKey("-----BEGIN PUBLIC KEY-----\nMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAstKpYuR1uxUyr3xwjWsd\n3lxMY79ME1ZQlALyHsRao3lP4F419bD9Q4Rvv8S3Irb6cpFLZgFn+U1/6alNuMjk\nwc4gQD01Eg23OJXtzaXmxg+b4TrLZJadOXHEGczOb4qIofTM4SP+tSNcLSxJJDdJ\nYyFOiZ6UqZi7dNlM6aGU5GKxGnK4ND2FpuaI5rv3isyCV71BRk9kv8wzKYTy3dO+\noTl0EZ0vHDWA8rXqL17WKsrHdx0u0dLzOSCUv4JEezbnRxDgM/ZvszuOFq8nOqBv\nE2viM1u9Tvq6xPmpMV1tRfG3Y7uK6rZhqorGVHwwbftovAdU/lFqduif4NNhJ3Vq\n8/d6L6CPZH/aojUDAlXGketoiW3aOVZF3ESDKRUHWc5s9PJ//QQSkVWipmeYHCex\nvogTsqBswzHhP1yaXaNA/xJNWUuCtq8r82C40RFkjQC5eAUHUvqlHmBdLk2VN+Zm\nSDuoqMOke2X+6SwahB6DxMke+9grzxhMYgebgLwij6SRoBnG7Kxgh1ozGguRIu+1\nQpFjkyJ3fwf+tlD9qcSaWleXwDQ3s52Hu31S+BDEL5/R1KsGBhQJiSEfkmqhIOfE\nw+rRjoO4BNhJYiipcDuBobZb6OiLXrARWVDSjRiavHACCweLQ5Pj48NfubR41exF\nHtz3bmTMDHrBMXdSJqv974kCAwEAAQ==\n-----END PUBLIC KEY-----");
    }

    @Override
    public Claims decode(String token) {
        return jwtService.parseToken(token);
    }

    private PublicKey loadPubKey(String encodedKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        encodedKey = normalizeKey(encodedKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        byte[] keyBytes = Base64.getDecoder().decode(encodedKey);
        return kf.generatePublic(new X509EncodedKeySpec(keyBytes));
    }

    private String normalizeKey(String encodedKey) {
        return encodedKey.replace("\n", "")
                .replace(BEGIN_PUBLIC_KEY, "")
                .replace(END_PUBLIC_KEY, "")
                .replace(BEGIN_PRIVATE_KEY, "")
                .replace(END_PRIVATE_KEY, "");
    }
}
