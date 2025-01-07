package com.example.archunittest.archtest;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ArchTest {

    private final JavaClasses importedClasses = new ClassFileImporter()
        .importPackages("com.example"); // 프로젝트 루트 패키지

    @Test
    @DisplayName("application 영역은 [api, infrastructure, persistence] 영역을 참조할 수 없다.")
    void application_area_test() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..application..")

            // 참조 금지 패키지 지정
            .should().dependOnClassesThat().resideInAnyPackage(
                "com.example..api..",
                "com.example..infrastructure..",
                "com.example..persistence.."
            );

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("domain 영역은 [api, infrastructure, persistence, application] 영역을 참조할 수 없다.")
    void domain_area_test() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..domain..")

            // 참조 금지 패키지 지정
            .should().dependOnClassesThat().resideInAnyPackage(
                "com.example..api..",
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
            .that().resideInAPackage("..api.dto..")

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
    void annotations_should_not_contain_setter_in_name() {
        ArchRule rule = ArchRuleDefinition.noMethods()
            .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("Entity")
            .should().haveNameStartingWith("set");

        rule.check(importedClasses);
    }
}