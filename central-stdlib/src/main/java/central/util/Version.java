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

package central.util;

import central.lang.Arrayx;
import central.lang.Assertx;
import central.lang.Stringx;
import jakarta.annotation.Nullable;
import lombok.*;

/**
 * 版本号
 * <p>
 * 主要用于解析 主版本号 . 子版本号 [ 修正版本号 [. 编译版本号 ]]
 * 如：
 * 1.2
 * 1.2.0
 * 1.2.0.1234
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Version implements Comparable<Version> {
    /**
     * 主版本号
     */
    @Getter
    @Setter
    int major;
    /**
     * 子版本号
     */
    @Getter
    @Setter
    int minor;
    /**
     * 修正版本号
     */
    @Getter
    @Setter
    int revision;
    /**
     * 编译版本号
     */
    @Getter
    @Setter
    int build;

    public Version(String version) {
        if (!Stringx.isNullOrBlank(version)) {
            Assertx.mustTrue(version.matches("[0-9]+(\\.[0-9]+)*"), "不是有效的版本号信息");

            String[] splits = version.split("[.]");
            major = Integer.parseInt(splits[0]);
            minor = Integer.parseInt(Objectx.getOrDefault(Arrayx.getOrNull(splits, 1), "0"));
            revision = Integer.parseInt(Objectx.getOrDefault(Arrayx.getOrNull(splits, 2), "0"));
            build = Integer.parseInt(Objectx.getOrDefault(Arrayx.getOrNull(splits, 3), "0"));
        }
    }

    public static Version of(String version) {
        return new Version(version);
    }

    @Override
    public String toString() {
        String version = major + "." + minor;
        if (revision != 0) {
            version += ("." + revision);
        }
        if (build != 0) {
            if (revision == 0) {
                version += ("." + revision + "." + build);
            } else {
                version += ("." + build);
            }
        }
        return version;
    }

    public String toString(boolean full) {
        if (full) {
            return major + "." + minor + "." + revision + "." + build;
        } else {
            return toString();
        }
    }

    /**
     * 比较两个版本号
     *
     * @param v 另一个版本号
     * @return 比较结果
     */
    @Override
    public int compareTo(@Nullable Version v) {
        if (v == null) return 1;

        int result = Integer.compare(this.major, v.major);
        if (result != 0) {
            return result;
        }

        result = Integer.compare(this.minor, v.minor);
        if (result != 0) {
            return result;
        }

        result = Integer.compare(this.revision, v.revision);
        if (result != 0) {
            return result;
        }

        return Integer.compare(this.build, v.build);
    }
}
