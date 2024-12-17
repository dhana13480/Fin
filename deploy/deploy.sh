#!/bin/bash

export AWS_ACCOUNT=$(python -c "import boto.utils; print boto.utils.get_instance_metadata()['iam']['info']['InstanceProfileArn'].split(':')[4]")
export TARGET_ENV=$(echo ${JOB_BASE_NAME} | awk -F- '{print $NF}')
echo "environment: ${TARGET_ENV}"

echo "Provision Action : ${PROVISION_ACTION}"

echo ${PARENT_BUILD_NUMBER}
export PROVISION_RMCSBUILDNUMBER=${PARENT_BUILD_NUMBER}

cd $WORKSPACE/release

echo "******** Running provision action: ${PROVISION_ACTION} ********"
provision lambda $TARGET_ENV $PROVISION_ACTION --file ${APP_GROOVY}

if [[ $? -ne 0 ]]; then
  echo "***************Failed to provision lambdas. Exiting...******************"
  exit 1
fi

case ${TARGET_ENV} in
dev)
  ACCOUNT="465257512377"
  API_ID="8m8shrtoc5"
  function_name=${LAMBDA_NAME}-DEV
  ;;
devint)
  ACCOUNT="465257512377"
  API_ID="1tbxcz90s0"
  function_name=${LAMBDA_NAME}-DEVINT
  ;;
dev02)
  ACCOUNT="465257512377"
  API_ID="j1g731nq76"
  function_name=${LAMBDA_NAME}-DEV02
  ;;
dev03)
  ACCOUNT="465257512377"
  API_ID="mg1sg0n6ae"
  function_name=${LAMBDA_NAME}-DEV03
  ;;
qa)
  ACCOUNT="142248000760"
  API_ID="191n9ui4ah"
  function_name=${LAMBDA_NAME}-QA
  ;;
qa02)
  ACCOUNT="142248000760"
  API_ID="b1aymrtrph"
  function_name=${LAMBDA_NAME}-QA02
  ;;
qa03)
  ACCOUNT="142248000760"
  API_ID="o1fr8nnzgf"
  function_name=${LAMBDA_NAME}-QA03
  ;;
qaint)
  ACCOUNT="142248000760"
  API_ID="nm1yri95p3"
  function_name=${LAMBDA_NAME}-QAINT
  ;;
qaint02)
  ACCOUNT="142248000760"
  API_ID="b39edjr7yd"
  function_name=${LAMBDA_NAME}-QAINT02
  ;;
uat)
  ACCOUNT="142248000760"
  API_ID="65huzchaa6"
  function_name=${LAMBDA_NAME}-UAT
  ;;
prod)
  ACCOUNT="510199193688"
  API_ID="hr019kpe46"
  function_name=${LAMBDA_NAME}-PROD
  ;;
esac

if [[ ${LAMBDA_NAME} == "RMCS-Receivable-Item-Lambda" || ${LAMBDA_NAME} == "RMCS-Get-Receivable-Lambda" ]]; then
  echo "starting to update the configuration for ${LAMBDA_NAME} function"
  echo "get version"
  version=$(aws lambda publish-version --function-name ${function_name} --region us-east-1 --query Version --output text)
  echo "version" ${version}
  if [[ ${PROVISION_ACTION} == "fresh" ]]; then
    echo "creating alias"
    aws lambda create-alias --function-name ${function_name} --description "alias for latest version of function" --function-version ${version} \
      --name "release" --region us-east-1

    echo "adding permissions to invoke lambda function "
    aws lambda add-permission --function-name ${function_name}:release --statement-id apigateway-get --action lambda:InvokeFunction \
      --principal apigateway.amazonaws.com \
      --source-arn "arn:aws:execute-api:us-east-1:$ACCOUNT:$API_ID/*" \
      --source-account ${ACCOUNT} \
      --region us-east-1

    echo "adding provision concurrency"
    aws lambda put-provisioned-concurrency-config --function-name ${function_name}:release \
      --qualifier release \
      --provisioned-concurrent-executions 20 \
      --region us-east-1

  elif [[ ${PROVISION_ACTION} == "stack-update" ]]; then
    echo "updating alias to latest version of lambda function "
    aws lambda update-alias --function-name ${function_name} --description "alias for latest version of function" --function-version $version \
      --name "release" --region us-east-1
    while true; do
      status=$(aws lambda get-provisioned-concurrency-config --function-name ${function_name} --qualifier release --region us-east-1 --query Status --output text)
      if [[ $status == "READY" ]]; then
        echo "provision concurrency update is completed"
        break
      fi
      echo "provision concurrency update is still in progress. Waiting..."
      sleep 20
    done
    echo "deleting old version..."
    aws lambda delete-function --function-name ${function_name} --qualifier $((version-1)) --region us-east-1
  fi
fi

if [[ ${LAMBDA_NAME} == "RMCS-Ebill-Batch-Process-Lambda" ]];  then
  echo "starting to update the configuration for RMCS-Ebill-Batch-Process-Lambda function"
  aws lambda update-function-configuration --function-name ${function_name} --snap-start ApplyOn=PublishedVersions --region us-east-1
  echo "update-function-configuration snap-start completed successfully"
  aws lambda wait function-updated --function-name ${function_name} --region us-east-1
  version=$(aws lambda publish-version --function-name ${function_name} --region us-east-1 --query Version --output text)

  if [ $COMMAND == "fresh" ]; then
    aws lambda create-alias --function-name ${function_name} --description "alias for latest version of function" --function-version $version \
        --name "release" --region us-east-1
  elif [ $COMMAND == 'stack-update' ]; then
    aws lambda update-alias --function-name ${function_name} --description "alias for latest version of function" --function-version $version \
        --name "release" --region us-east-1
  fi
fi

if [[ ${PROVISION_ACTION} == "fresh" ]] && [[ ${LAMBDA_NAME} == "RMCS-Send-Invoice-To-WD-Lambda" || ${LAMBDA_NAME} == "RMCS-Receivable-To-Invoice-Lambda" || ${LAMBDA_NAME} == "RMCS-ICM-Report-Lambda" || ${LAMBDA_NAME} == "RMCS-Ebill-Batch-Process-Lambda"  || ${LAMBDA_NAME} == "RMCS-Ebill-Modeled-Schedule-Wrapper-Lambda" ]]; then
  echo "Applying JAMSinvocationDT policy"
  case ${TARGET_ENV} in
  "dev") aws lambda add-permission --region us-east-1 --function-name ${LAMBDA_NAME}-DEV --statement-id 'JAMSinvocationDT' --action 'lambda:InvokeFunction' --principal 'arn:aws:iam::754249303703:role/APP_JAMS' ;;
  "devint") aws lambda add-permission --region us-east-1 --function-name ${LAMBDA_NAME}-DEVINT --statement-id 'JAMSinvocationDT' --action 'lambda:InvokeFunction' --principal 'arn:aws:iam::754249303703:role/APP_JAMS' ;;
  "dev02") aws lambda add-permission --region us-east-1 --function-name ${LAMBDA_NAME}-DEV02 --statement-id 'JAMSinvocationDT' --action 'lambda:InvokeFunction' --principal 'arn:aws:iam::754249303703:role/APP_JAMS' ;;
  "dev03") aws lambda add-permission --region us-east-1 --function-name ${LAMBDA_NAME}-DEV03 --statement-id 'JAMSinvocationDT' --action 'lambda:InvokeFunction' --principal 'arn:aws:iam::754249303703:role/APP_JAMS' ;;
  "qa") aws lambda add-permission --region us-east-1 --function-name ${LAMBDA_NAME}-QA --statement-id 'JAMSinvocationDT' --action 'lambda:InvokeFunction' --principal 'arn:aws:iam::043811358421:role/APP_JAMS' ;;
  "qa02") aws lambda add-permission --region us-east-1 --function-name ${LAMBDA_NAME}-QA02 --statement-id 'JAMSinvocationDT' --action 'lambda:InvokeFunction' --principal 'arn:aws:iam::043811358421:role/APP_JAMS' ;;
  "qa03") aws lambda add-permission --region us-east-1 --function-name ${LAMBDA_NAME}-QA03 --statement-id 'JAMSinvocationDT' --action 'lambda:InvokeFunction' --principal 'arn:aws:iam::043811358421:role/APP_JAMS' ;;
  "qaint") aws lambda add-permission --region us-east-1 --function-name ${LAMBDA_NAME}-QAINT --statement-id 'JAMSinvocationDT' --action 'lambda:InvokeFunction' --principal 'arn:aws:iam::043811358421:role/APP_JAMS' ;;
  "qaint02") aws lambda add-permission --region us-east-1 --function-name ${LAMBDA_NAME}-QAINT02 --statement-id 'JAMSinvocationDT' --action 'lambda:InvokeFunction' --principal 'arn:aws:iam::043811358421:role/APP_JAMS' ;;
  "uat") aws lambda add-permission --region us-east-1 --function-name ${LAMBDA_NAME}-UAT --statement-id 'JAMSinvocationDT' --action 'lambda:InvokeFunction' --principal 'arn:aws:iam::043811358421:role/APP_JAMS' ;;
  "prod") aws lambda add-permission --region us-east-1 --function-name ${LAMBDA_NAME}-PROD --statement-id 'JAMSinvocationDT' --action 'lambda:InvokeFunction' --principal 'arn:aws:iam::570164370074:role/APP_JAMS' ;;
  esac
fi

if [[ $? -ne 0 ]]; then
  echo "***************Failed to apply JAMSinvocationDT policy. Exiting...******************"
  exit 1
fi
