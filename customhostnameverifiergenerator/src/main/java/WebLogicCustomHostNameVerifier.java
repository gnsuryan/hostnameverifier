package com.oracle.azure.weblogic.security.util;

import com.oracle.azure.weblogic.HostNameValues;
import weblogic.security.utils.SSLCertUtility;

public class WebLogicCustomHostNameVerifier implements weblogic.security.SSL.HostnameVerifier
{
    public boolean verify(String urlHostname, javax.net.ssl.SSLSession session)
    {
        String commonName = SSLCertUtility.getCommonName(session);
        debug("commonName: "+commonName);
        debug("urlHostname: "+urlHostname);
        
        String hostNameMatchStartString = new StringBuilder(HostNameValues.dnsLabelPrefix.toLowerCase()).append("0").toString();
        String hostNameMatchEndString = new StringBuilder(HostNameValues.wlsDomainName.toLowerCase())
                                            .append(".")
                                            .append(HostNameValues.azureResourceGroupRegion.toLowerCase())
                                            .append(".")
                                            .append(HostNameValues.azureVMExternalDomainName.toLowerCase()).toString();
        
        if(commonName.equalsIgnoreCase(urlHostname))
        {
            debug("urlhostname matching certificate common name");
            return true;
        }
        else
        if(commonName.equalsIgnoreCase(HostNameValues.adminInternalHostName))
        {
            debug("urlhostname matching certificate common name: "+HostNameValues.adminInternalHostName+","+commonName);
            return true;            
        }
        else
        if(commonName.equalsIgnoreCase(HostNameValues.adminExternalHostName))
        {
            debug("urlhostname matching certificate common name: "+HostNameValues.adminExternalHostName+","+commonName);
            return true;            
        }
        else
        if(commonName.equalsIgnoreCase(HostNameValues.adminDNSZoneName))
        {
            debug("adminDNSZoneName matching certificate common name: "+HostNameValues.adminDNSZoneName+","+commonName);
            return true;            
        }
        else
        if(commonName.startsWith(hostNameMatchStartString) && commonName.endsWith(hostNameMatchEndString))
        {
            return true;
        }
        
        return false;
    }
    
    private void debug(String debugStatement)
    {
        if(HostNameValues.debugEnabled)
            System.out.println(debugStatement);
    }
}

