package org.task.manager

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchTest {

    @Test
    fun servicesAndRepositoriesShouldNotDependOnWebLayer() {

        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("org.task.manager")

        noClasses()
            .that()
                .resideInAnyPackage("org.task.manager.service..")
            .or()
                .resideInAnyPackage("org.task.manager.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..org.task.manager.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses)
    }
}
