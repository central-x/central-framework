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

package central.lang.reflect;

import central.bean.InitializeException;
import central.util.LazyValue;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Supplier;

/**
 * TypeReference Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
public class TestTypeReference {
    /**
     * 通过 Class 构建 TypeReference
     */
    @Test
    public void case1() {
        var reference = TypeReference.of(TestTypeReference.class);

        Assertions.assertEquals(TestTypeReference.class.getTypeName(), reference.getName());
        Assertions.assertFalse(reference.isParameterized());
        Assertions.assertEquals(TestTypeReference.class, reference.getType());
    }

    /**
     * 通过类名创建 TypeReference
     */
    @Test
    public void case2() {
        var reference = TypeReference.forName("central.lang.reflect.TestTypeReference");
        Assertions.assertFalse(reference.isParameterized());
        Assertions.assertEquals(TestTypeReference.class, reference.getType());
    }

    /**
     * 通过直接实例化创建 TypeReference
     */
    @Test
    public void case3() {
        var reference = new TypeReference<TestTypeReference>() {
        };
        Assertions.assertFalse(reference.isParameterized());
        Assertions.assertEquals(TestTypeReference.class, reference.getType());
    }

    /**
     * 通过直接实例化创建泛型 TypeReference
     */
    @Test
    public void case4() {
        var reference = new TypeReference<List<String>>() {
        };
        Assertions.assertTrue(reference.isParameterized());
        Assertions.assertEquals(List.class, reference.getRawType());
        Assertions.assertEquals(String.class, reference.getActualTypeArgument(0).getType());
    }

    /**
     * 手动构建泛型
     * {@code List<String>}
     */
    @Test
    public void case5() {
        var reference = TypeReference.forListType(String.class);
        Assertions.assertTrue(reference.isParameterized());
        Assertions.assertEquals(List.class, reference.getRawType());
        Assertions.assertEquals(String.class, reference.getActualTypeArgument(0).getType());
    }

    /**
     * 手动构建泛型
     * {@code List<List<String>>}
     */
    @Test
    public void case6() {
        var reference = TypeReference.forListType(TypeReference.forListType(String.class));
        Assertions.assertTrue(reference.isParameterized());
        Assertions.assertEquals(List.class, reference.getRawType());

        var actualType = reference.getActualTypeArgument(0);
        Assertions.assertTrue(actualType.isParameterized());
        Assertions.assertEquals(List.class, actualType.getRawType());
        Assertions.assertEquals(String.class, actualType.getActualTypeArgument(0).getType());
    }

    /**
     * 手动构建泛型
     * {@code Map<String, Integer> }
     */
    @Test
    public void case7() {
        var reference = TypeReference.forMapType(String.class, Integer.class);
        Assertions.assertTrue(reference.isParameterized());
        Assertions.assertEquals(Map.class, reference.getRawType());

        Assertions.assertEquals(String.class, reference.getActualTypeArgument(0).getType());
        Assertions.assertEquals(Integer.class, reference.getActualTypeArgument(1).getType());
    }

    /**
     * 手动构建泛型
     * {@code Map<String, List<String>>}
     */
    @Test
    public void case8() {
        var reference = TypeReference.forMapType(String.class, TypeReference.forListType(Integer.class));
        Assertions.assertTrue(reference.isParameterized());
        Assertions.assertEquals(Map.class, reference.getRawType());

        Assertions.assertEquals(String.class, reference.getActualTypeArgument(0).getType());
        Assertions.assertTrue(reference.getActualTypeArgument(1).isParameterized());
        Assertions.assertEquals(List.class, reference.getActualTypeArgument(1).getRawType());
        Assertions.assertEquals(Integer.class, reference.getActualTypeArgument(1).getActualTypeArgument(0).getType());
    }

    /**
     * 实例化
     */
    @Test
    public void case9() {
        var reference = TypeReference.forListType(String.class);
        var instance = reference.newInstance(List.of("String"));
        var object = instance.getInstance();

        Assertions.assertTrue(object instanceof List<String>);
        Assertions.assertTrue(object.size() == 1);
        Assertions.assertEquals("String", object.get(0));
    }

    /**
     * 实例化
     */
    @Test
    public void case10() {
        var reference = new TypeReference<TestClass>() {
        };
        var instance = reference.newInstance(Integer.valueOf(1));
        Assertions.assertEquals(Integer.valueOf(1), instance.getInstance().getIntValue());

        instance = reference.newInstance("Str");
        Assertions.assertEquals("Str", instance.getInstance().getStrValue());

        instance = reference.newInstance(Double.valueOf(2.56));
        Assertions.assertEquals(Double.valueOf(2.56), instance.getInstance().getDoubleValue());

        instance = reference.newInstance((Supplier<String>) () -> "str");
        Assertions.assertEquals("str", instance.getInstance().getSupplierValue().get());

        instance = reference.newInstance(Integer.valueOf(1), "str", (Supplier<String>) () -> "str");
        Assertions.assertEquals(Integer.valueOf(1), instance.getInstance().getIntValue());
        Assertions.assertEquals("str", instance.getInstance().getStrValue());
        Assertions.assertEquals("str", instance.getInstance().getSupplierValue().get());

        Assertions.assertThrows(InitializeException.class, () -> reference.newInstance("test", "ex"));
    }

    @Test
    public void case11() {
        var reference = new TypeReference<TestClass2>() {
        };

        Assertions.assertEquals(TestClass2.class, reference.getRawClass());
        Assertions.assertEquals(LazyValue.class, reference.getSuperType().getRawType());
    }

    public static abstract class TestClass2 extends LazyValue<String> implements List<String>, Supplier<String> {
        public TestClass2() {
            super(() -> "test");
        }
    }

    @Getter
    public static class TestClass {
        private Integer intValue;
        private String strValue;
        private Double doubleValue;
        private Supplier<String> supplierValue;

        public TestClass() {

        }

        public TestClass(Integer value) {
            this.intValue = value;
        }

        public TestClass(String value) {
            this.strValue = value;
        }

        public TestClass(Double value) {
            this.doubleValue = value;
        }

        public TestClass(Supplier<String> value) {
            this.supplierValue = value;
        }

        public TestClass(Integer value, String str, Supplier<String> sup) {
            this.intValue = value;
            this.strValue = str;
            this.supplierValue = sup;
        }
    }
}
