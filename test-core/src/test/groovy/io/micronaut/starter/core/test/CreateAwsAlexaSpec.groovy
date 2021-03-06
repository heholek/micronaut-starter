package io.micronaut.starter.core.test

import io.micronaut.starter.application.ApplicationType
import io.micronaut.starter.test.CommandSpec
import io.micronaut.starter.options.BuildTool
import io.micronaut.starter.options.Language
import io.micronaut.starter.options.TestFramework
import spock.lang.Unroll

class CreateAwsAlexaSpec extends CommandSpec {

    @Override
    String getTempDirectoryPrefix() {
        return "test-awsalexa"
    }

    @Unroll
    void 'create-#applicationType with features aws-alexa #lang and #build and test framework: #testFramework'(ApplicationType applicationType,
                                                                                                               Language lang,
                                                                                                               BuildTool build,
                                                                                                               TestFramework testFramework) {
        given:
        List<String> features = ['aws-alexa']
        generateProject(lang, build, features, applicationType, testFramework)

        when:
        build == BuildTool.GRADLE ? executeGradleCommand('test') : executeMavenCommand("test")

        then:
        testOutputContains("BUILD SUCCESS")

        where:
        //TODO use default also when alexa gets included in the bom. [applicationType, lang, build, testFramework] << [[ApplicationType.DEFAULT, ApplicationType.FUNCTION], Language.values(), BuildTool.values(), TestFramework.values()].combinations()
        [applicationType, lang, build, testFramework] << [[ApplicationType.FUNCTION], Language.values(), BuildTool.values(), TestFramework.values()].combinations()
    }
}
