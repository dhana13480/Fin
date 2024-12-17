#!/bin/bash -xev
export JAVA_HOME=${JDK_17_HOME}
export PATH=${JDK_17_HOME}/bin:$PATH

printf "############################\n"
printf "Java version:\n"
java -version
printf "Java path:\n"
whereis java
printf "Maven version:\n"
mvn -version
printf "PATH: \n$PATH"
printf "\n############################\n"


cp -f /tmp/clover.license ${WORKSPACE}/export/cm_share/apps/java/clover/clover-ant-2.4.0/lib/clover.license

mkdir ${WORKSPACE}/release/
cd ${WORKSPACE}/checkout/${LAMBDA_PATH}

mvn -s /tmp/settings.xml clean install -f assembly.xml -DskipTests

if [[ "$?" -ne 0 ]] ; then
  echo "Failed to compile and package the build"
  exit 1
fi

mvn -s /tmp/settings.xml clean clover:setup test clover:aggregate clover:clover

if [[ $? -ne 0 ]]; then
	echo "Failed to run unit tests. Exiting..."
	exit 1
fi

mkdir ${WORKSPACE}/checkout/target
cp -rf target/site/ ${WORKSPACE}/checkout/target/
cp -f target/${LAMBDA_PATH}*aws.jar ${WORKSPACE}/release/rmcs-${LAMBDA_PATH}-${BUILD_NUMBER}.jar
cd ${WORKSPACE}/checkout/
cp -f deploy/* ${WORKSPACE}/release/
cp -f deploy/provision/${APP_GROOVY} ${WORKSPACE}/release/
