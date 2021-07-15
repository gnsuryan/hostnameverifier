#!/bin/bash

function usage()
{
  echo "Usage: $0 <adminInternalHostName> <adminExternalHostName> <adminDnsZoneName> <dnsLabelPrefix> <wlsDomainName> <azureResourceGroupRegion> [<debugFlag>]"
  exit 1
}


function readArgs()
{
  
  if [ $# -lt 6 ];
  then
    echo "Error !! invalid arguments"
    usage
  fi

  adminInternalHostName="$1"
  adminExternalHostName="$2"
  adminDnsZoneName="$3"
  dnsLabelPrefix="$4"
  wlsDomainName="$5"
  azureResourceGroupRegion="$6"

  if [ $# -gt 6 ];
  then
   debugFlag="$7"
  else
   debugFlag="false"
  fi
 
}


SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

java -version > /dev/null 2>&1

if [ $? != 0 ];
then
  echo -e "Error !! This script requires java to be installed and available in the path for execution. \n Please install and configure JAVA in PATH variable and retry"
  exit 1
fi

mvn --version > /dev/null 2>&1

if [ $? != 0 ];
then
  echo -e "Error!! This script requires maven to be installed. Please install maven and retry"
  exit 1
fi

if [ -z $WL_HOME ];
then
  echo -e "Error !! WL_HOME is not set. \nPlease ensure that WebLogic Server is installed and WL_HOME variable is set to the WebLogic Home Directory"
  exit 1
fi

#main

readArgs "$@"

mvn clean dependency:copy-dependencies package

if [ $? != 0 ];
then
  echo "Packaging of customhostnameverifier generator failed. Please try again"
  exit 1
fi

java -jar $SCRIPT_DIR/target/customhostnameverifiergenerator-1.0.jar "$adminInternalHostName" "$adminExternalHostName" "$adminDnsZoneName" "$dnsLabelPrefix" "$wlsDomainName" "$azureResourceGroupRegion" "$debugFlag"


if [ $? != 0 ];
then
  echo "CustHostNameVerifierGenerator Failed !! Please check the error and retry."
  exit 1
else
  echo "CustHostNameVerifierGenerator Completed Successfully !!"
fi

PROJECT_NAME="wlscustomhostnameverifier"

PROJECT_BASE_DIR="${SCRIPT_DIR}/project"

mkdir -p $PROJECT_BASE_DIR
rm -rf $PROJECT_BASE_DIR/*

cd $PROJECT_BASE_DIR

echo "generating maven project for generating CustHostNameVerifier jar from generated source"
mvn  archetype:generate \
    -DgroupId=com.oracle.azure.weblogic.security.util \
    -DartifactId=$PROJECT_NAME \
    -DarchetypeArtifactId=maven-archetype-quickstart  \
    -DinteractiveMode=false

PROJECT_DIR="${PROJECT_BASE_DIR}/${PROJECT_NAME}"

SOURCE_DIR="${PROJECT_DIR}/src/main/java/com/oracle/azure/weblogic/security/util"
mkdir -p ${SOURCE_DIR}
cp -rf $SCRIPT_DIR/target/WebLogicAzureCustomHostNameVerifier.java ${SOURCE_DIR}
rm -rf ${SOURCEDIR}/App.java

cd $PROJECT_DIR 

#modify version to 1.0
mvn versions:set -DnewVersion=1.0

#modify maven pom.xml to include dependency on weblogic.jar to compile the CustomHostNameVerifier class

echo "modifying pom.xml to include weblogic.jar dependency"
sed -i 's/<dependencies>/<dependencies>\r\n<dependency>\r\n<groupId>weblogic<\/groupId>\r\n<artifactId>weblogicjar<\/artifactId>\r\n<version>1.0<\/version>\r\n<scope>system<\/scope>\r\n<systemPath>\${env.WL_HOME}\/server\/lib\/weblogic.jar<\/systemPath>\r\n<\/dependency>\r\n/g' pom.xml 

mvn -DskipTests -Dmaven.test.skip=true clean package


if [ $? != 0 ];
then
  echo "CustHostNameVerifier jar creation Failed !! Please check the error and retry."
  exit 1
else
  echo "CustHostNameVerifier jar created Successfully !!"
fi



