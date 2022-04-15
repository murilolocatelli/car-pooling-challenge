package com.cabify.carpooling.test.pojo;

import com.cabify.carpooling.test.BaseUnitTest;
import com.cabify.carpooling.util.Constants;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.filters.FilterBasedOnInheritance;
import com.openpojo.reflection.impl.PojoClassFactory;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

public class PojoTest extends BaseUnitTest {

    @Test
    public void testMethodsDto() {

        final List<PojoClass> pojoClasses = PojoClassFactory.getPojoClassesRecursively(
            Constants.PACKAGE_DTO, new FilterBasedOnInheritance(Serializable.class));

        this.invokeMethods(pojoClasses);
    }

    @Test
    public void testMethodsModel() {

        final List<PojoClass> pojoClasses = PojoClassFactory.getPojoClassesRecursively(
            Constants.PACKAGE_MODEL, new FilterBasedOnInheritance(Serializable.class));

        this.invokeMethods(pojoClasses);
    }

    private void invokeMethods(final List<PojoClass> pojoClasses) {
        pojoClasses.forEach(pojoClass -> {
            final Class<?> clazz = pojoClass.getClazz();

            try {
                this.invokeToString(clazz);
                this.invokeEquals(clazz);
                this.invokeHashCode(clazz);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void invokeToString(final Class<?> testClass) throws Exception {
        final Method method = testClass.getDeclaredMethod("toString");

        method.invoke(testClass.getDeclaredConstructor().newInstance());
    }

    private void invokeEquals(final Class<?> testClass) throws Exception {
        final Method method = testClass.getDeclaredMethod("equals", Object.class);
        final Object instance = testClass.getDeclaredConstructor().newInstance();
        final Object otherInstance = testClass.getDeclaredConstructor().newInstance();

        method.invoke(instance, otherInstance);
    }

    private void invokeHashCode(final Class<?> testClass) throws Exception {
        final Method method = testClass.getDeclaredMethod("hashCode");

        method.invoke(testClass.getDeclaredConstructor().newInstance());
    }

}
