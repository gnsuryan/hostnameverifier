package com.oracle.azure.weblogic.security.test;

import com.oracle.azure.weblogic.HostNameValues;
import com.oracle.azure.weblogic.security.util.WebLogicCustomHostNameVerifier;


public class WebLogicCustomHostNameVerifierTest
{
    private static String adminInternalHostName;
    private static String adminExternalHostName;
    private static String adminDNSZoneName;
    private static String dnsLabelPrefix;
    private static String wlsDomainName;
    private static String azureResourceGroupRegion;
    private static String debugFlag;

    public static void main(String args[])
    {
        readArguments(args);
        runTest();
    }

    private static void readArguments(String[] args)
    {
        if(args != null && args.length >= 6)
        {
            adminInternalHostName = args[0];
            adminExternalHostName = args[1];
            adminDNSZoneName = args[2];
            dnsLabelPrefix = args[3];
            wlsDomainName = args[4];
            azureResourceGroupRegion = args[5];
            debugFlag="false";
            
            if(args.length > 6)
            {
                debugFlag=args[6];
            }
        }
        else
        {
           usage();
        }
    }

    private static void runTest()
    {
        boolean fail=false;
        
        if(! HostNameValues.adminInternalHostName.equals(adminInternalHostName))
        {
            System.out.println("Error !! adminInternalHostName not matching in HostNameValues.java: "+HostNameValues.adminInternalHostName+","+adminInternalHostName);
            fail=true;
        }

        if(! HostNameValues.adminExternalHostName.equals(adminExternalHostName))
        {
            System.out.println("Error !! adminExternalHostName not matching in HostNameValues.java "+HostNameValues.adminExternalHostName+","+adminExternalHostName);
            fail=true;
        }

        if(! HostNameValues.adminDNSZoneName.equals(adminDNSZoneName))
        {
            System.out.println("Error !! adminDNSZoneName not matching in HostNameValues.java "+HostNameValues.adminDNSZoneName+","+adminDNSZoneName);
            fail=true;
        }

        if(! HostNameValues.dnsLabelPrefix.equals(dnsLabelPrefix))
        {
            System.out.println("Error !! dnsLabelPrefix not matching in HostNameValues.java "+HostNameValues.dnsLabelPrefix+","+dnsLabelPrefix);
            fail=true;
        }

        if(! HostNameValues.wlsDomainName.equals(wlsDomainName))
        {
            System.out.println("Error !! wlsDomainName not matching in HostNameValues.java "+HostNameValues.wlsDomainName+","+wlsDomainName);
            fail=true;
        }

        if(! HostNameValues.azureResourceGroupRegion.equals(azureResourceGroupRegion))
        {
            System.out.println("Error !! azureResourceGroupRegion not matching in HostNameValues.java "+HostNameValues.azureResourceGroupRegion+","+azureResourceGroupRegion);
            fail=true;
        }

        if(fail)
        {
            System.out.println("WebLogicCustomHostNameVerifierTest Failed !!");
            System.exit(1);
        }

        System.out.println("WebLogicCustomHostNameVerifierTest Passed !!");
    }
        
    private static void usage()
    {
        System.out.println("Usage: java CustomHostNameVerifierGenerator <adminInternalHostName> <adminExternalHostName> <adminDNSZoneName> <dnsLabelPrefix> <wlsDomainName> <azureResourceGroupRegion> [<debugFlag>]");
        System.exit(1);
    }
}