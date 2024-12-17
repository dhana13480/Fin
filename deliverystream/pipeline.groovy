import org.finra.appeng.f3.builders.*
import org.finra.appeng.f3.common.*


interface Constants {
    final String AGS = "RMCS"
    final String RELEASE = "2023.01"
    final List<String> EMAIL = ["DL-RMCS_Dev@finra.org"]
    final String BUILD_LABEL = "amznlinux2_ci:latest"
    final String PROVISION_LABEL = "provision:21.0.0"
    final String TEMPLATE_FOLDER = 'rmcs-lambdas'
    final Map ACCOUNT = [
            dev : "465257512377",
            qa  : "142248000760",
            prod: "510199193688"
    ]
    final Map CLUSTER = [
            dev : "DEV",
            qa  : "QA",
            prod: "PROD"
    ]
}

class DeliveryPipeline extends CommonImpl {

    // Component reusable declarations
    private static COMPONENT = "delivery-stream"

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
                .withJobShell("\${WORKSPACE}/checkout/deliverystream/build.sh")
                .withAgs(Constants.AGS)
                .withRelease(Constants.RELEASE)
                .withEcsLabel(ECSLabel.newInstance()
                        .withImage(Constants.BUILD_LABEL)
                        .withCluster('DEV')
                        .withMemory("3.0"))
                .withInputChoiceParam([
                        InputChoiceParam.newInstance()
                                .withKey("PROVISION_ACTION")
                                .withValue(["stack-update", "fresh", "stack-delete"])
                                .withDescription("Deployment command")])
                .withDryRun(false)
                .withRepos([Repo.newInstance()
                                    .withUrl("ssh://git@bitbucket.finra.org:7999/rmcs/rmcs-lambda-function.git")
                                    .withSub_directory("checkout")
                                    .withDescription("Default Branch Selected:master")
                                    .withBranchVariable("GIT_BRANCH")
                                    .withDefaultValue("develop")])
                .withDownstream(deploy("dev"))
                .withDownstream(deploy("qa"))
                .withDownstream(deploy("prod"))
                .withScanLocations(ScanLocations.newInstance()
                        .withArchiveType(ArchiveType.GZIP)
                        .withOs(OS.LINUX)
                        .withLocations([
                                Location.newInstance().withDirLocation('.')
                                        .withArchiveName('workspace.tar.gz')]))

    }


    private JenkinsJob deploy(String sdlc) {
        JenkinsJob job = JenkinsJob.newInstance()
                .withName(Constants.AGS + '/' + Constants.TEMPLATE_FOLDER + '/' + COMPONENT + '/deployment_jobs/deploy-' + sdlc)
                .withEcsLabel(ECSLabel.newInstance()
                        .withImage(Constants.PROVISION_LABEL)
                        .withCluster("${Constants.CLUSTER[sdlc]}")
                        .withRole("arn:aws:iam::${Constants.ACCOUNT[sdlc]}:role/JENKINS_" + Constants.AGS)
                        .withMemory('3.0')
                )
                .withTemplate(Template.DEPLOY)
                .withJobShell('release/deploy.sh')
                .withS3CopyJob(rootJob)
                .withEmails(Constants.EMAIL)

        return job
    }

}

DeliveryPipeline.newInstance().createPipeline(this,DeliveryPipeline.newInstance().buildPipeline())
