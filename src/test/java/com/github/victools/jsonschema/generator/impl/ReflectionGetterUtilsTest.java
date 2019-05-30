/*
 * Copyright 2019 VicTools.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.victools.jsonschema.generator.impl;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.github.victools.jsonschema.generator.AbstractAnnotationAwareTest;
import java.util.Map;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for the {@link ReflectionUtils} class.
 */
@RunWith(JUnitParamsRunner.class)
public class ReflectionGetterUtilsTest extends AbstractAnnotationAwareTest {

    Object parametersForTestFindGetterForField() {
        return new String[][]{
            {"fieldWithoutGetter", null},
            {"fieldWithPrivateGetter", null},
            {"fieldWithPublicGetter", "getFieldWithPublicGetter"},
            {"fieldWithPublicBooleanGetter", "isFieldWithPublicBooleanGetter"}};
    }

    @Test
    @Parameters
    public void testFindGetterForField(String fieldName, String methodName) throws Exception {
        Map.Entry<AnnotatedField, BeanDescription> annotatedField = this.wrapField(TestClass.class.getDeclaredField(fieldName));
        AnnotatedMethod getter = ReflectionGetterUtils.findGetterForField(annotatedField.getKey(), annotatedField.getValue());

        if (methodName == null) {
            Assert.assertNull(getter);
        } else {
            Assert.assertNotNull(getter);
            Assert.assertEquals(methodName, getter.getName());
        }
    }

    @Test
    @Parameters({
        "fieldWithoutGetter, false",
        "fieldWithPrivateGetter, false",
        "fieldWithPublicGetter, true",
        "fieldWithPublicBooleanGetter, true"
    })
    public void testHasGetter(String fieldName, boolean expectedResult) throws Exception {
        Map.Entry<AnnotatedField, BeanDescription> annotatedField = this.wrapField(TestClass.class.getDeclaredField(fieldName));
        boolean result = ReflectionGetterUtils.hasGetter(annotatedField.getKey(), annotatedField.getValue());

        Assert.assertEquals(expectedResult, result);
    }

    Object parametersForTestFindFieldForGetter() {
        return new String[][]{
            {"getFieldWithPrivateGetter", null},
            {"getFieldWithPublicGetter", "fieldWithPublicGetter"},
            {"isFieldWithPublicBooleanGetter", "fieldWithPublicBooleanGetter"},
            {"getCalculatedValue", null},
            {"isBehavingSomehow", null},
            {"get", null},
            {"is", null},
            {"calculateSomething", null}};
    }

    @Test
    @Parameters
    public void testFindFieldForGetter(String methodName, String fieldName) throws Exception {
        Map.Entry<AnnotatedMethod, BeanDescription> annotatedMethod = this.wrapMethod(TestClass.class.getDeclaredMethod(methodName));
        AnnotatedField field = ReflectionGetterUtils.findFieldForGetter(annotatedMethod.getKey(), annotatedMethod.getValue());

        if (fieldName == null) {
            Assert.assertNull(field);
        } else {
            Assert.assertNotNull(field);
            Assert.assertEquals(fieldName, field.getName());
        }
    }

    @Test
    @Parameters({
        "getFieldWithPrivateGetter, false",
        "getFieldWithPublicGetter, true",
        "isFieldWithPublicBooleanGetter, true",
        "getCalculatedValue, false",
        "isBehavingSomehow, false",
        "get, false",
        "is, false",
        "calculateSomething, false"
    })
    public void testIsGetter(String methodName, boolean expectedResult) throws Exception {
        Map.Entry<AnnotatedMethod, BeanDescription> annotatedMethod = this.wrapMethod(TestClass.class.getDeclaredMethod(methodName));
        boolean result = ReflectionGetterUtils.isGetter(annotatedMethod.getKey(), annotatedMethod.getValue());

        Assert.assertEquals(expectedResult, result);
    }

    private static class TestClass {

        private String fieldWithoutGetter;
        private int fieldWithPrivateGetter;
        private long fieldWithPublicGetter;
        private boolean fieldWithPublicBooleanGetter;

        private int getFieldWithPrivateGetter() {
            return this.fieldWithPrivateGetter;
        }

        public long getFieldWithPublicGetter() {
            return this.fieldWithPublicGetter;
        }

        public boolean isFieldWithPublicBooleanGetter() {
            return this.fieldWithPublicBooleanGetter;
        }

        public double getCalculatedValue() {
            return 42.;
        }

        public boolean isBehavingSomehow() {
            return true;
        }

        public Object get() {
            return this;
        }

        public boolean is() {
            return false;
        }

        public int calculateSomething() {
            return 42;
        }
    }
}
