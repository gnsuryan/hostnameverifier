package com.oracle.azure.weblogic;

public interface HostNameValues
{
    public static final String azureVMExternalDomainName="cloudapp.azure.com";
    
    public static final boolean debugEnabled=false;
    public final String adminInternalHostName="adminvm.internal.cloudapp.net";
    public final String adminExternalHostName="wls0-86247cf1d7-wlsd.eastus.cloudapp.azure.com";
    public final String adminDNSZoneName="admin.mycompany.com";
    public final String dnsLabelPrefix="wls";
    public final String wlsDomainName="wlsd";
    public final String azureResourceGroupRegion="eastus";

}

