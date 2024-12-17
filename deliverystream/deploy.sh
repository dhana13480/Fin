#!/bin/bash

export AWS_ACCOUNT=`python -c "import boto.utils; print boto.utils.get_instance_metadata()['iam']['info']['InstanceProfileArn'].split(':')[4]"`
export TARGET_ENV=`echo ${JOB_BASE_NAME} | awk -F- '{print $NF}'`
echo "environment: ${TARGET_ENV}"

echo "Provision Action : ${PROVISION_ACTION}"

echo ${PARENT_BUILD_NUMBER}
export PROVISION_RMCSBUILDNUMBER=${PARENT_BUILD_NUMBER};

cd $WORKSPACE/release
echo ${PARENT_COMPONENT}
provision deliverystream $TARGET_ENV $PROVISION_ACTION
