package central.util;

import central.lang.Assertx;
import lombok.*;

import javax.annotation.Nullable;

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
            minor = Integer.parseInt(Objectx.get(Arrayx.get(splits, 1), "0"));
            revision = Integer.parseInt(Objectx.get(Arrayx.get(splits, 2), "0"));
            build = Integer.parseInt(Objectx.get(Arrayx.get(splits, 3), "0"));
        }
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
