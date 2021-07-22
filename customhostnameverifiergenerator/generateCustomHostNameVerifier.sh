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
  adminDNSZoneName="$3"
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

setProperty(){
  echo $1=$2 >> $3
}

function cleanup()
{
  echo "cleaning ..."

  rm -rf $OUTPUT_DIR/*
  rm -rf $CLASSES_DIR/*
  rm -rf $PROPS_FILE
}

function cleanupCompiledClasses()
{
  rm -rf $CLASSES_DIR/com
}

function initialize()
{
  echo "initializing ..."
  CLASSES_DIR="$SCRIPT_DIR/classes"
  mkdir -p "$CLASSES_DIR"

  OUTPUT_DIR="$SCRIPT_DIR/output"
  mkdir -p "$OUTPUT_DIR"

  PROPS_FILE="$SCRIPT_DIR/src/main/java/hostname.properties"
  touch $PROPS_FILE
}

function validate()
{
  java -version > /dev/null 2>&1

  if [ $? != 0 ];
  then
    echo -e "Error !! This script requires java to be installed and available in the path for execution. \n Please install and configure JAVA in PATH variable and retry"
    exit 1
  fi

  if [ -z $WL_HOME ];
  then
    echo -e "Error !! WL_HOME is not set. \nPlease ensure that WebLogic Server is installed and WL_HOME variable is set to the WebLogic Home Directory"
    exit 1
  fi
}

function setProps()
{
  setProperty debugEnabled ${debugFlag} $PROPS_FILE
  setProperty adminInternalHostName ${adminInternalHostName} $PROPS_FILE
  setProperty adminExternalHostName ${adminExternalHostName} $PROPS_FILE
  setProperty adminDNSZoneName ${adminDNSZoneName} $PROPS_FILE
  setProperty dnsLabelPrefix ${dnsLabelPrefix} $PROPS_FILE
  setProperty wlsDomainName ${wlsDomainName} $PROPS_FILE
  setProperty azureResourceGroupRegion ${azureResourceGroupRegion} $PROPS_FILE
  setProperty azureVMExternalDomainName "cloudapp.azure.com" $PROPS_FILE
}

function copyPropsToClassPath()
{
  cp -rf $PROPS_FILE $CLASSES_DIR/
}

function generateHostNameVerifierJar()
{
  cd $SCRIPT_DIR/src/main/java
  echo "Compiling WebLogicCustomHostNameVerifier.java "
  $JAVA_HOME/bin/javac -d $CLASSES_DIR -classpath $WL_HOME/server/lib/weblogic.jar:$CLASSES_DIR com/oracle/azure/weblogic/security/util/WebLogicCustomHostNameVerifier.java

  echo "generating weblogicustomhostnameverifier.jar"
  cd $CLASSES_DIR
  jar cf $OUTPUT_DIR/weblogicustomhostnameverifier.jar com/oracle/azure/weblogic/security/util/*.class

  if [ $? != 0 ];
  then
    echo "CustomHostNameVerifier jar creation Failed !! Please check the error and retry."
    exit 1
  else
    echo "CustomHostNameVerifier jar created Successfully !!"
  fi
}

function runHostNameVerifierTest()
{
  cd $SCRIPT_DIR/src/test/java

  cleanupCompiledClasses
  
  echo "compiling HostNameVerifierTest.java"
  $JAVA_HOME/bin/javac -d $CLASSES_DIR -classpath $WL_HOME/server/lib/weblogic.jar:$OUTPUT_DIR/weblogicustomhostnameverifier.jar com/oracle/azure/weblogic/security/test/WebLogicCustomHostNameVerifierTest.java

  echo "executing HostNameVerifierTest"
  $JAVA_HOME/bin/java -classpath $CLASSES_DIR:$WL_HOME/server/lib/weblogic.jar:$OUTPUT_DIR/weblogicustomhostnameverifier.jar com/oracle/azure/weblogic/security/test/WebLogicCustomHostNameVerifierTest "$@"

  if [ $? != 0 ];
  then
    echo "CustomHostNameVerifierTest Failed !! Please check the error and retry."
    exit 1
  else
    echo "CustomHostNameVerifierTest Passed Successfully !!"
  fi
}

#main

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

readArgs "$@"
validate

initialize
cleanup
setProps
copyPropsToClassPath
generateHostNameVerifierJar
runHostNameVerifierTest "$@"

