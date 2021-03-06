package io.micronaut.starter.feature.kotlin

import io.micronaut.starter.BeanContextSpec
import io.micronaut.starter.application.ApplicationType
import io.micronaut.starter.feature.Category
import io.micronaut.starter.feature.LanguageSpecificFeature
import io.micronaut.starter.options.Language
import spock.lang.Shared
import spock.lang.Subject
import spock.lang.Unroll
import io.micronaut.starter.feature.build.gradle.templates.buildGradle
import io.micronaut.starter.feature.build.maven.templates.pom

class KtorSpec extends BeanContextSpec {

    @Subject
    @Shared
    Ktor ktor = beanContext.getBean(Ktor)

    void "ktor belongs to Logging category"() {
        expect:
        Category.LANGUAGES == ktor.category
    }

    void "ktor requires kotlin"() {
        expect:
        ktor instanceof LanguageSpecificFeature
        ktor.getRequiredLanguage() == Language.KOTLIN
    }

    void "ktor is visible"() {
        expect:
        ktor.visible
    }

    void "ktor title and description are different"() {
        expect:
        ktor.getTitle()
        ktor.getDescription()
        ktor.getTitle() != ktor.getDescription()
    }

    @Unroll
    void "feature ktor does not support type: #applicationType"(ApplicationType applicationType) {
        expect:
        !ktor.supports(applicationType)

        where:
        applicationType << (ApplicationType.values().toList() - ApplicationType.DEFAULT)
    }

    @Unroll
    void "feature ktor supports #applicationType"(ApplicationType applicationType) {
        expect:
        ktor.supports(applicationType)

        where:
        applicationType << [ApplicationType.DEFAULT]
    }

    @Unroll
    void 'dependency is included with maven and feature ktor for language=#language'(Language language) {
        when:
        String template = pom.template(ApplicationType.DEFAULT, buildProject(), getFeatures(['ktor'], language), []).render().toString()

        then:
        template.contains("""
    <dependency>
      <groupId>io.micronaut.kotlin</groupId>
      <artifactId>micronaut-ktor</artifactId>
      <scope>compile</scope>
    </dependency>
""")
        !template.contains('<artifactId>micronaut-http-server-netty</artifactId>')

        where:
        language << supportedLanguages()
    }

    @Unroll
    void 'exception for maven and feature ktor for language=#language'(Language language) {
        when:
        pom.template(ApplicationType.DEFAULT, buildProject(), getFeatures(['ktor'], language), []).render().toString()

        then:
        IllegalArgumentException e = thrown()
        e.message.contains("The selected features are incompatible")

        where:
        language << (Language.values().toList() - supportedLanguages())
    }

    @Unroll
    void 'dependency is included with gradle and feature ktor for language=#language'(Language language) {
        when:
        String template = buildGradle.template(ApplicationType.DEFAULT, buildProject(), getFeatures(['ktor'], language)).render().toString()

        then:
        template.contains('implementation("io.micronaut.kotlin:micronaut-ktor")')
        !template.contains('implementation "io.micronaut:micronaut-http-server-netty"')

        where:
        language << supportedLanguages()
    }

    @Unroll
    void 'exception with gradle and feature ktor for language=#language'(Language language) {
        when:
        buildGradle.template(ApplicationType.DEFAULT, buildProject(), getFeatures(['ktor'], language)).render().toString()

        then:
        IllegalArgumentException e = thrown()
        e.message.contains("The selected features are incompatible")

        where:
        language << (Language.values().toList() - supportedLanguages())
    }

    private List<Language> supportedLanguages() {
        [Language.KOTLIN]
    }
}
