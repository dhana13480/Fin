# Process Receivable Request Function

This Lambda is used to store Receivable item sent from Revenue Stream into Database.

Refer this [wiki](https://wiki.finra.org/display/FinApps/Receivable+Items+Design) for validation
rules.

Refer [wiki](https://wiki.finra.org/display/FinApps/Create+Receivable+Item+API) for api spec.

*This Lambda assumes Customer and Organization already exits in FCI and doesnot create a Customer or
Organization in FCI.*

## Deployment with Jenkins

The Lambdas are built and deployed to the various environments
using [Jenkins](https://builds4.aws.finra.org/job/RMCS/job/rmcs-lambdas/view/batchfile-process-receivable-lambda/)