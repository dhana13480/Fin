# Receivable To Invoice Lambda

This Lambda is used to validate Receivable item sent from Revenue Stream and transform it into
Invoice and update Database accordingly.

Refer this [wiki](https://wiki.finra.org/display/FinApps/Receivable+Items+Design) for validation
rules

*This Lambda is triggered by JAMS on a schedules rate basis (refer to Architecture Diagram in
this [page](https://wiki.finra.org/display/FinApps/RMM-+Architecture))*

## Deployment with Jenkins

The Lambdas are built and deployed to the various environments
using [Jenkins](https://builds4.aws.finra.org/job/RMCS/job/rmcs-lambdas/view/receivable-to-invoice-lambda/)