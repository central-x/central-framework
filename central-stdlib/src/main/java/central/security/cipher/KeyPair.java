package central.security.cipher;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.Key;

/**
 * 加解密密钥对
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */

@Data
@AllArgsConstructor
public class KeyPair {
    /**
     * 加密密钥
     */
    private Key encryptKey;
    /**
     * 解密密钥
     */
    private Key decryptKey;
}
