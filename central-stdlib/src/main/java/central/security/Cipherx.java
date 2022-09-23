/*
 * MIT License
 *
 * Copyright (c) 2022-present Alan Yeh <alan@yeh.cn>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package central.security;

import central.security.cipher.CipherImpl;
import central.security.cipher.impl.*;
import lombok.experimental.UtilityClass;

/**
 * 加解密工具
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
@UtilityClass
public class Cipherx {
    /**
     * 不加密
     */
    public static final CipherImpl NONE = new NoneImpl();
    /**
     * RSA
     */
    public static final CipherImpl RSA = new RSAImpl();

    /**
     * DES
     */
    public static final CipherImpl DES = new DESImpl();

    /**
     * 3DES
     */
    public static final CipherImpl DESede = new DESedeImpl();

    /**
     * AES
     */
    public static final CipherImpl AES = new AESImpl();

    /**
     * 国密算法 SM1 (对称算法)，使用硬件加密
     * SM1 算法是分组密码算法，分组长度为128位，密钥长度都为 128 比特，算法安全保密强度及相关软硬件实现性能与 AES 相当，算法不公开，仅以 IP 核的形式存在于芯片中。
     * 采用该算法已经研制了系列芯片、智能IC卡、智能密码钥匙、加密卡、加密机等安全产品，广泛应用于电子政务、电子商务及国民经济的各个应用领域（包括国家政务通、警务通等重要领域）。
     */
    public static final CipherImpl SM1 = new NotImpl();
    /**
     * 国密算法 SM2 (非对称算法)
     * SM2算法就是ECC椭圆曲线密码机制，但在签名、密钥交换方面不同于ECDSA、ECDH等国际标准，而是采取了更为安全的机制。另外，SM2推荐了一条256位的曲线作为标准曲线。
     * SM2标准包括总则，数字签名算法，密钥交换协议，公钥加密算法四个部分，并在每个部分的附录详细说明了实现的相关细节及示例。
     *
     * 密文有 C1|C2|C3 和 C1｜C3｜C2 的区别，本实现采用 C1|C2|C3 作为密文
     */
    public static final CipherImpl SM2 = new SM2Impl();
    /**
     * 国密算法 SM4 (对称算法)
     * 无线局域网标准的分组数据算法。对称加密，密钥长度和分组长度均为 128 位
     * 本实现采用 CBC 分组加密
     */
    public static final CipherImpl SM4 = new SM4Impl();
}
