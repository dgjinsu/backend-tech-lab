package com.example.archunittest.archtest;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.constructors;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaConstructor;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ArchTest {

    private final JavaClasses importedClasses = new ClassFileImporter()
        .importPackages("com.example"); // 프로젝트 루트 패키지

    @Test
    @DisplayName("application 영역은 [infrastructure, persistence] 영역을 참조할 수 없다.")
    void application_area_test() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..application..")

            // 참조 금지 패키지 지정
            .should().dependOnClassesThat().resideInAnyPackage(
                "com.example..infrastructure..",
                "com.example..persistence.."
            );

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("domain 영역은 [infrastructure, persistence, application] 영역을 참조할 수 없다.")
    void domain_area_test() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..domain..")

            // 참조 금지 패키지 지정
            .should().dependOnClassesThat().resideInAnyPackage(
                "com.example..infrastructure..",
                "com.example..persistence..",
                "com.example..application.."
            );

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("controller.dto 의 클래스명은 [Request, Response] 로 끝나야 한다.")
    void controller_dto_suffix_test() {
        ArchRule rule = classes()
            .that().resideInAPackage("..infrastructure.controller.dto..")

            // 클래스 명 규칙
            .should().haveSimpleNameEndingWith("Request")
            .orShould().haveSimpleNameEndingWith("Response");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("application.dto 의 클래스명은 [Command, Query] 로 끝나야 한다.")
    void application_dto_suffix_test() {
        ArchRule rule = classes()
            .that().resideInAPackage("..application.dto..")

            // 클래스 명 규칙
            .should().haveSimpleNameEndingWith("Command")
            .orShould().haveSimpleNameEndingWith("Query");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Entity 에는 @setter 가 있으면 안 된다.")
    void entity_should_not_have_setter() {
        ArchRule rule = noMethods()
            .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("Entity")
            .should().haveNameStartingWith("set");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Entity 클래스의 기본 생성자는 반드시 protected여야 한다.")
    void entity_default_constructor_should_be_protected() {
        ArchCondition<JavaConstructor> defaultConstructorCondition = new ArchCondition<>(
            "have protected modifier") {
            @Override
            public void check(JavaConstructor constructor, ConditionEvents events) {
                int modifiers = constructor.reflect().getModifiers();

                if (!Modifier.isProtected(modifiers)) {
                    String message = String.format(
                        "Class %s의 기본 생성자는 protected가 아님.",
                        constructor.getOwner().getFullName()
                    );
                    events.add(SimpleConditionEvent.violated(constructor, message));
                }
            }
        };

        ArchRule rule = constructors()
            .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("Entity")
            .and().haveRawParameterTypes(new Class<?>[0]) // 기본 생성자 (파라미터 없음) 명시
            .should(defaultConstructorCondition);

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Entity 클래스에는 전체 생성자가 존재하면 안 된다.")
    void entity_allArgsConstructor_should_not_exist() {
        ArchCondition<JavaConstructor> noAllArgsConstructorCondition = new ArchCondition<>(
            "should not have all-args constructor") {
            @Override
            public void check(JavaConstructor constructor, ConditionEvents events) {
                Constructor<?> reflectedConstructor = constructor.reflect();
                Class<?> clazz = constructor.getOwner().reflect();

                // 전체 생성자 여부 확인 (생성자의 파라미터 수가 클래스의 필드 수와 동일한지 확인)
                boolean isAllArgsConstructor =
                    reflectedConstructor.getParameterCount() == clazz.getDeclaredFields().length;

                if (isAllArgsConstructor) {
                    String message = String.format(
                        "Class %s에는 전체 생성자가 존재: %s",
                        clazz.getName(),
                        reflectedConstructor
                    );
                    events.add(SimpleConditionEvent.violated(constructor, message));
                }
            }
        };

        // ArchRule 정의
        ArchRule rule = constructors()
            .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("Entity")
            .should(noAllArgsConstructorCondition);

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("DTO 는 records 로 생성해야 한다.")
    void dto_classes_should_be_records() {
        ArchRule rule = classes()
            .that().haveSimpleNameEndingWith("Request")
            .or().haveSimpleNameEndingWith("Response")
            .or().haveSimpleNameEndingWith("Command")
            .or().haveSimpleNameEndingWith("Query")
            .should().beRecords();

        rule.check(importedClasses);
    }
}