package central.security;

import central.security.signer.SignerImpl;
import central.security.signer.impl.RSAImpl;
import central.security.signer.impl.SM2Impl;

/**
 * 数字签名算法
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public class Signerx {
    public static final SignerImpl RSA = new RSAImpl();

    public static final SignerImpl SM2 = new SM2Impl();
}
