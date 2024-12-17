# Send Invoice to WD Lambda Function

This Lambda is used to create a json file with list of all Invoices and send it to S3 buckets for
WD.

*This Lambda is triggered by JAMS on a schedules rate basis (refer to Architecture Diagram in
this [page](https://wiki.finra.org/display/FinApps/RMM-+Architecture))*

## Deployment with Jenkins

The Lambdas are built and deployed to the various environments
using [Jenkins](https://builds4.aws.finra.org/job/RMCS/job/rmcs-lambdas/view/send-invoice-to-wd-lambda/)

## How to run it locally

Please get security token from [CloudPass](https://cloudpass.finra.org/cloudpass/#/cloudpass) with
the role `priv_aws_rmcs_dev_d`, and then add it to your IDE.
