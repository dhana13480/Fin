ags = "RMCS"
region = "east"
appStackVersion = "01"

tags {
    ags = "${ags}"
    costCenter = "RSM302"
    sdlc = "${sdlc}"
    component = "batchfile-slam-lambda"
}

lambdas {
    global_configuration {
        forceUpdate = false
        memory = "1024"
        timeout = "900"
        runtime = "java17"
        executionRole = "SVC_LAMBDA_RMCS_SR"
        subscriptionFilter {
            deliveryStreamName = "SPLUNK-LAMBDA-RMCS-HTTP"
            role = "SVC_CW_FINRA_LAMBDA_SPLUNK_SR"
        }
    }
}

def sdlcMap = [
        "dev" : [
                sdlcUpper    : "DEV",
                vpcName      : "Dev_East",
                subnetIDs    : "subnet-1d2e3435,subnet-0df4547a,subnet-41e42718",
                stagingBucket: "4652-5751-2377-application-dev-staging",
                accountID    : "465257512377"
        ],
        "devint" : [
                sdlcUpper    : "DEVINT",
                vpcName      : "Dev_East",
                subnetIDs    : "subnet-1d2e3435,subnet-0df4547a,subnet-41e42718",
                stagingBucket: "4652-5751-2377-application-dev-staging",
                accountID    : "465257512377"
        ],
        "dev02" : [
                sdlcUpper    : "DEV02",
                vpcName      : "Dev_East",
                subnetIDs    : "subnet-1d2e3435,subnet-0df4547a,subnet-41e42718",
                stagingBucket: "4652-5751-2377-application-dev-staging",
                esmpQueueSdlc: "D",
                accountID    : "465257512377"
        ],
        "dev03" : [
                sdlcUpper    : "DEV03",
                vpcName      : "Dev_East",
                subnetIDs    : "subnet-1d2e3435,subnet-0df4547a,subnet-41e42718",
                stagingBucket: "4652-5751-2377-application-dev-staging",
                esmpQueueSdlc: "D",
                accountID    : "465257512377"
        ],
        "qa"  : [
                sdlcUpper    : "QA",
                vpcName      : "Qa_East",
                subnetIDs    : "subnet-184da033,subnet-d4c765a3,subnet-3ee52867",
                stagingBucket: "1422-4800-0760-application-qa-staging",
                accountID    : "142248000760"
        ],
        "qa02"  : [
                sdlcUpper    : "QA02",
                vpcName      : "Qa_East",
                subnetIDs    : "subnet-184da033,subnet-d4c765a3,subnet-3ee52867",
                stagingBucket: "1422-4800-0760-application-qa-staging",
                accountID    : "142248000760"
        ],
        "qa03"  : [
                sdlcUpper    : "QA03",
                vpcName      : "Qa_East",
                subnetIDs    : "subnet-184da033,subnet-d4c765a3,subnet-3ee52867",
                stagingBucket: "1422-4800-0760-application-qa-staging",
                accountID    : "142248000760"
        ],
        "qaint"  : [
                sdlcUpper    : "QAINT",
                vpcName      : "Qa_East",
                subnetIDs    : "subnet-184da033,subnet-d4c765a3,subnet-3ee52867",
                stagingBucket: "1422-4800-0760-application-qa-staging",
                accountID    : "142248000760"
        ],
        "uat": [
                sdlcUpper    : "UAT",
                vpcName      : "Qa_East",
                subnetIDs    : "subnet-184da033,subnet-d4c765a3,subnet-3ee52867",
                stagingBucket: "1422-4800-0760-application-qa-staging",
                accountID    : "142248000760"
        ],
        "prod": [
                sdlcUpper    : "PROD",
                vpcName      : "Prod_East",
                subnetIDs    : "subnet-acee27f5,subnet-85cf61f2,subnet-755bb25e",
                stagingBucket: "5101-9919-3688-application-prod-staging",
                accountID    : "510199193688"
        ]
]

def lambda_template = { sdlc ->
    println "*********Template Values***************"
    def envMap = sdlcMap["${sdlc}"]
    println "envMap: " + envMap

    def subnetList = envMap["subnetIDs"].tokenize(',')
    println "subnetList:" + subnetList

    lambdas {
        lambda1 {
            staging {
                bucket = envMap["stagingBucket"]
                prefix = "RMCS/batchfile-slam-lambda"
                deployArchive = "rmcs-batchfile-slam-lambda-${rmcsbuildnumber}.jar"
                generateUniqueLocation = false
            }

            name = "RMCS-Batchfile-Slam-Lambda-${envMap["sdlcUpper"]}"
            handler = "org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest"

            vpcConfig {
                subnetId = subnetList
                securityGroupId = ["${lookup.sg("finra-outbound", envMap["vpcName"])}", "${lookup.sg("finra-support", envMap["vpcName"])}", "${lookup.sg("RMCS-internal", envMap["vpcName"])}"]
            }

            environment {
                variables = ["SPRING_PROFILES_ACTIVE": "${sdlc}"]
            }

        }

    }
}

environments {

    dev {
        lambda_template("dev")
        tags { sdlc = "dev" }
    }

    devint {
        lambda_template("devint")
        tags { sdlc = "devint" }
    }

    dev02 {
        lambda_template("dev02")
        tags { sdlc = "dev02" }
    }

    dev03 {
        lambda_template("dev03")
        tags { sdlc = "dev03" }
    }

    qa {
        lambda_template("qa")
        tags { sdlc = "qa" }
    }

    qa02 {
        lambda_template("qa02")
        tags { sdlc = "qa02" }
    }

    qa03 {
        lambda_template("qa03")
        tags { sdlc = "qa03" }
    }

    qaint {
        lambda_template("qaint")
        tags { sdlc = "qaint" }
    }

    uat {
        lambda_template("uat")
        tags { sdlc = "uat" }
    }

    prod {
        lambda_template("prod")
        tags { sdlc = "prod" }
    }

}

