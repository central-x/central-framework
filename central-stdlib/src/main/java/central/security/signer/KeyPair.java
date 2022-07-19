package central.security.signer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.Key;

/**
 * 签名密钥对
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
@Data
@AllArgsConstructor
public class KeyPair {
    /**
     * 签名密钥
     */
    private Key signKey;

    /**
     * 验签密钥
     */
    private Key verifyKey;
}

