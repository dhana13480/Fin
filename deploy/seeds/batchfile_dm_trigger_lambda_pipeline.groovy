import org.finra.appeng.f3.builders.*
import org.finra.appeng.f3.common.*


interface Constants {
    final String AGS = "RMCS"
    final String RELEASE = "v1.0"
    final List<String> EMAIL = ["DL-RMCS_Dev@finra.org"]
    final String BUILD_LABEL = "amznlinux2_ci:latest"
    final String PROVISION_LABEL = "provision:21.0.0"
    final String TEMPLATE_FOLDER = 'rmcs-lambdas'
    final Map ACCOUNT = [
            dev : "465257512377",
            devint : "465257512377",
            dev02 : "465257512377",
            dev03 : "465257512377",
            qa  : "142248000760",
            qa02  : "142248000760",
            qa03  : "142248000760",
            qaint : "142248000760",
            uat : "142248000760",
            prod: "510199193688"
    ]
    final Map CLUSTER = [
            dev : "DEV",
            devint : "DEV",
            dev02 : "DEV",
            dev03 : "DEV",
            qa  : "QA",
            qa02  : "QA",
            qa03  : "QA",
            qaint: "QA",
            uat: "QA",
            prod: "PROD"
    ]
    final String RMM_APPLICATION = "corpsysRMM"
    final String JIRA_PUBLISHER = "publishToJIRA"
}

class Lambda extends CommonImpl {

    // Component reusable declarations
    private static COMPONENT = "batchfile-dm-trigger-lambda"

    // Please verify these and update if necessary
    private static rootJob = Constants.AGS + '/' + Constants.TEMPLATE_FOLDER + '/' + COMPONENT + '/' + COMPONENT

    // Component Build/Start/Pipeline-Initial Job parameters
    BppView buildPipeline() {
        Logger.log("FJDSL : Creating pipeline view " + rootJob)
        BppView.newInstance()
                .withSelectedJob(rootJob)
                .withDisplayedBuilds(9)
                .withViewFolderLocation(Constants.AGS + '/' + Constants.TEMPLATE_FOLDER)
                .withStartJob(start())
    }

    // Pipeline Initial Job
    JenkinsJob start() {
        JenkinsJob.newInstance()
                .withName(rootJob)
                .withComponent(COMPONENT)
                .withTemplate(Template.BUILD)
                .withJobShell("\${WORKSPACE}/checkout/deploy/build.sh")
                .withAgs(Constants.AGS)
                .withRelease(Constants.RELEASE)
                .withEcsLabel(ECSLabel.newInstance()
                        .withImage(Constants.BUILD_LABEL)
                        .withCluster('DEV')
                        .withMemory("3.0"))
                .withEmails(Constants.EMAIL)
                .withJunitPublisher([JUnitPublisher.newInstance()
                                             .withHealthScaleFactor(1.5)
                                             .withPattern("checkout/*/target/surefire-reports/*.xml")])
                .withInputChoiceParam([
                        InputChoiceParam.newInstance()
                                .withKey("PROVISION_ACTION")
                                .withValue(["fresh", "stack-update", "stack-delete"])
                                .withDescription("Deployment command")])
                .withAdditionalEnvVars([
                        AdditionalEnvVars.newInstance().withKey("LAMBDA_PATH").withValue("batchfile-dm-trigger-lambda"),
                        AdditionalEnvVars.newInstance().withKey("APP_GROOVY").withValue("batchfile_dm_trigger_lambda_provision.groovy"),
                ])
                .withDryRun(false)
                .withRepos([Repo.newInstance()
                                    .withUrl("ssh://git@bitbucket.finra.org:7999/rmcs/rmcs-lambda-function.git")
                                    .withSub_directory("checkout")
                                    .withDescription("Default Branch Selected:master")
                                    .withBranchVariable("GIT_BRANCH")
                                    .withDefaultValue("develop")])
                .withAutoTriggerDownstream(generateScanJob())
                .withDownstream(generateProdScan())
                .withDownstream(deploy("dev"))
                .withDownstream(deploy("devint"))
                .withDownstream(deploy("dev02"))
                .withDownstream(deploy("dev03"))
                .withDownstream(deploy("qa"))
                .withDownstream(deploy("qa02"))
                .withDownstream(deploy("qa03"))
                .withDownstream(deploy("qaint"))
                .withDownstream(deploy("uat"))
                .withScanLocations(ScanLocations.newInstance()
                        .withArchiveType(ArchiveType.GZIP)
                        .withOs(OS.LINUX)
                        .withLocations([
                                Location.newInstance().withDirLocation('.')
                                        .withArchiveName('workspace.tar.gz')]))
                .withHtmlPublisher([
                        HTMLPublisher.newInstance()
                                .withReportFiles("abc.html")
                                .withReportName("files")
                                .withReport("checkout/build/reports/unit_tests/test"),
                        HTMLPublisher.newInstance()
                                .withReportFiles("def.html")
                                .withReportName("files")
                                .withReport("checkout/build/reports/unit_tests/test2")])
                .withClover(Clover.newInstance()
                        .withReportDir("checkout/target/site/clover")
                        .withCloverReportFileName("clover.xml"))
                .withSonarQube([
                        SonarQube.newInstance()
                                .withJdk("JDK 11-amazon-corretto")
                                .withProperties("sonar.projectKey=org.finra."+ Constants.AGS.toLowerCase() + ":" + COMPONENT + " \n" +
                                        "sonar.projectName=" + Constants.AGS + "-" + COMPONENT + " \n" +
                                        "sonar.projectVersion=1.0 \n" +
                                        "sonar.sourceEncoding=UTF-8 \n" +
                                        "sonar.binaries=\${WORKSPACE}/checkout/" + COMPONENT + "/target/classes \n" +
                                        "sonar.tests= \${WORKSPACE}/checkout/" + COMPONENT + "/src/test/java \n" +
                                        "sonar.junit.reportsPath=\${WORKSPACE}/checkout/" + COMPONENT + "/target/surefire-reports \n" +
                                        "sonar.java.coveragePlugin=jacoco \n" +
                                        "sonar.host.url=https://sonarqube2.finra.org/ \n" +
                                        "sonar.coverage.jacoco.xmlReportPaths=\${WORKSPACE}/checkout/" + COMPONENT + "/target/site/jacoco/jacoco.xml \n" +
                                        "sonar.jacoco.xmlReportPaths=\${WORKSPACE}/checkout/" + COMPONENT + "/target/site/jacoco/jacoco.xml \n" +
                                        "sonar.sources=\${WORKSPACE}/checkout/" + COMPONENT + "/src/main/java \n" +
                                        "sonar.language=java \n" +
                                        "sonar.login=\${SVC_JENKINS_SQ_D} \n" +
                                        "sonar.java.binaries=\${WORKSPACE}/checkout/" + COMPONENT + "/target/classes")
                ])
    }

    JenkinsJob generateScanJob() {
        JenkinsJob.newInstance()
                .withName(Constants.AGS + '/' + Constants.TEMPLATE_FOLDER + '/' + COMPONENT + '/scan-jobs/bdh_scan')
                .withTemplate(Template.SCAN)
                .withS3CopyJob(rootJob)
                .withSourcePattern("workspace.tar.gz")
                .withJobShell("-c 'tar -xzvf workspace.tar.gz; rm -rf workspace.tar.gz'")
                .withBdhScan(BdhScan.newInstance()
                        .withPropertyFileLocation('${WORKSPACE}/release/bdhub.properties'))
    }

    JenkinsJob generateProdScan() {
        JenkinsJob.newInstance()
                .withName(Constants.AGS + '/' + Constants.TEMPLATE_FOLDER + '/' + COMPONENT + '/scan-jobs/prod_bdh_scan')
                .withTemplate(Template.SCAN)
                .withS3CopyJob(rootJob)
                .withDownstream(deploy("prod"))
                .withSourcePattern("workspace.tar.gz")
                .withJobShell("-c 'tar -xzvf workspace.tar.gz; rm -rf workspace.tar.gz'")
        //Marks this is a PROD-RC scan in blackduck.
                .withAdditionalEnvVars([AdditionalEnvVars.newInstance().withValue("PROD-RC")
                                                .withKey("GIT_BRANCH")])
                .withBdhScan(BdhScan.newInstance()
                        .withPropertyFileLocation('${WORKSPACE}/release/bdhub.properties'))
    }

    private JenkinsJob deploy(String sdlc) {
        JenkinsJob job = JenkinsJob.newInstance()
                .withName(Constants.AGS + '/' + Constants.TEMPLATE_FOLDER + '/' + COMPONENT + '/deployment_jobs/deploy-' + sdlc)
                .withEcsLabel(ECSLabel.newInstance()
                        .withImage('570164370074.dkr.ecr.us-east-1.amazonaws.com/jenkins/' + Constants.PROVISION_LABEL)
                        .withCluster("${Constants.CLUSTER[sdlc]}")
                        .withRole("arn:aws:iam::${Constants.ACCOUNT[sdlc]}:role/JENKINS_" + Constants.AGS)
                        .withMemory('3.0')
                )
                .withAdditionalEnvVars([
                        AdditionalEnvVars.newInstance().withKey("APP_GROOVY").withValue("batchfile_dm_trigger_lambda_provision.groovy"),
                        AdditionalEnvVars.newInstance().withKey("LAMBDA_NAME").withValue("RMCS-Batchfile-DM-Trigger-Lambda")

                ])
                .withTemplate(Template.DEPLOY)
                .withJobShell('release/deploy.sh')
                .withS3CopyJob(rootJob)
        switch(sdlc) {
            case 'qa':
            case 'qaint':
                job.withDownstream(corpSysBatchfileDMQA())
                break
        }

        return job
    }

    private JenkinsJob corpSysBatchfileDMQA() {
        JenkinsJob.newInstance()
                .withName(Constants.AGS + '/' + Constants.TEMPLATE_FOLDER + '/' + COMPONENT + '/deployment_jobs/' + Constants.RMM_APPLICATION)
                .withComponent(COMPONENT)
                .withTemplate(Template.BUILD)
                .withJobShell("\${WORKSPACE}/checkout/deploy/execute-rmm-tests.sh")
                .withEcsLabel(ECSLabel.newInstance()
                        .withImage(Constants.BUILD_LABEL)
                        .withRole('JENKINS_' + Constants.AGS + '_TEST')
                        .withCluster('QA')
                        .withMemory("6.0")
                        .withContainerMountPoints([
                                ECSLabel.ContainerMountPoint.newInstance()
                                        .withName('dockersock')
                                        .withSourcePath('/var/run/docker.sock')
                                        .withContainerPath('/var/run/docker.sock'),
                                ECSLabel.ContainerMountPoint.newInstance()
                                        .withName('docker')
                                        .withSourcePath('/usr/bin/docker')
                                        .withContainerPath('/usr/bin/docker'),
                                ECSLabel.ContainerMountPoint.newInstance()
                                        .withName('libdevmapper')
                                        .withSourcePath('/lib64/libdevmapper.so.1.02')
                                        .withContainerPath('/lib64/libdevmapper.so.1.02'),
                                ECSLabel.ContainerMountPoint.newInstance()
                                        .withName('libudev')
                                        .withSourcePath('/lib64/libudev.so.0')
                                        .withContainerPath('/lib64/libudev.so.0')
                        ])
                )
                .withInputChoiceParam([
                        InputChoiceParam.newInstance()
                                .withKey("ENV")
                                .withValue(["QA, QA_INT"])
                                .withDescription("Environment in which tests will be executed for all applications"),
                        InputChoiceParam.newInstance()
                                .withKey("TESTTYPE")
                                .withValue(["BATCHFILETRIGGERDM"])
                                .withDescription("Type of tests to be executed for all applications")
                ])
                .withDryRun(false)
                .withRepos([Repo.newInstance()
                                    .withUrl("ssh://git@bitbucket.finra.org:7999/fss/corpsysurs.git")
                                    .withSub_directory("checkout")
                                    .withDescription("Branch from which tests will be executed")
                                    .withBranchVariable("GIT_BRANCH")
                                    .withDefaultValue("master")])
                .withAdditionalEnvVars(
                        [AdditionalEnvVars.newInstance()
                                 .withKey("JDK_17_HOME")
                                 .withValue("/usr/lib/jvm/java-11-amazon-corretto"),
                         AdditionalEnvVars.newInstance()
                                 .withKey("JAVA_HOME")
                                 .withValue("\${JDK_17_HOME}"),
                         AdditionalEnvVars.newInstance()
                                 .withKey("M2_HOME")
                                 .withValue("/apps/maven/latest"),
                         AdditionalEnvVars.newInstance()
                                 .withKey("PATH")
                                 .withValue("\${GRADLE_HOME}/bin:\${M2_HOME}/bin:\${JAVA_HOME}/bin:\${PATH}"),
                         AdditionalEnvVars.newInstance()
                                 .withKey("ENABLE_PUBLISHING")
                                 .withValue("false")
                        ])
                .withCucumber(Cucumber.newInstance().withFileIncludePattern('cucumber.json').withJsonReportDirectory("\${WORKSPACE}/checkout/CorpSysRMM/CorpSysRMMFunctional/target"))
                .withGatling(Gatling.newInstance().withTrackGatlingLoadSimulation(true))
                .withDownstream(publishTestResultsToJIRA())
    }

    private JenkinsJob publishTestResultsToJIRA() {
        JenkinsJob.newInstance()
                .withName(Constants.AGS + '/' + Constants.TEMPLATE_FOLDER + '/' + COMPONENT + '/deployment_jobs/' + Constants.JIRA_PUBLISHER)
                .withTemplate(Template.DEPLOY)
                .withJobShell("\${WORKSPACE}/release/publish-to-jira.sh")
                .withEcsLabel(ECSLabel.newInstance()
                        .withImage(Constants.BUILD_LABEL)
                        .withRole('JENKINS_' + Constants.AGS + '_TEST')
                        .withCluster('QA')
                        .withMemory("2.0"))
                .withCredentialBinding(
                        CredentialBinding.newInstance()
                                .withUsername("APRO_USER")
                                .withPassword("APRO_PASSWORD")
                                .withCredential("4feb1062-373b-4ba5-ad68-762986bef4fc"))
                .withRepos([Repo.newInstance()
                                    .withUrl("ssh://git@bitbucket.finra.org:7999/fss/corpsysurs.git")
                                    .withSub_directory("checkout")
                                    .withDescription("Branch from which tests will be executed")
                                    .withBranchVariable("GIT_BRANCH")
                                    .withDefaultValue("master")])
                .withAdditionalEnvVars(
                        [AdditionalEnvVars.newInstance()
                                 .withKey("JDK_17_HOME")
                                 .withValue("/usr/lib/jvm/java-11-amazon-corretto"),
                         AdditionalEnvVars.newInstance()
                                 .withKey("JAVA_HOME")
                                 .withValue("\${JDK_17_HOME}"),
                         AdditionalEnvVars.newInstance()
                                 .withKey("M2_HOME")
                                 .withValue("/apps/maven/latest"),
                         AdditionalEnvVars.newInstance()
                                 .withKey("PATH")
                                 .withValue("\${GRADLE_HOME}/bin:\${M2_HOME}/bin:\${JAVA_HOME}/bin:\${PATH}"),
                         AdditionalEnvVars.newInstance()
                                 .withKey("ENABLE_PUBLISHING")
                                 .withValue("true")
                        ])
                .withS3CopyJob(Constants.AGS + '/' + Constants.TEMPLATE_FOLDER + '/' + COMPONENT + '/deployment_jobs/\${PARENT_COMPONENT}')
    }
}

Lambda.newInstance().createPipeline(this, Lambda.newInstance().buildPipeline())
