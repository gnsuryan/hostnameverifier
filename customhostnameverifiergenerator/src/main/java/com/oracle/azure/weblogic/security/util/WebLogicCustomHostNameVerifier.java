package com.oracle.azure.weblogic.security.util;

import com.oracle.azure.weblogic.HostNameValues;
import weblogic.security.utils.SSLCertUtility;

public class WebLogicCustomHostNameVerifier implements weblogic.security.SSL.HostnameVerifier, HostNameValues
{
    public boolean verify(String urlHostname, javax.net.ssl.SSLSession session)
    {
        String commonName = SSLCertUtility.getCommonName(session);
        debug("commonName: "+commonName);
        debug("urlHostname: "+urlHostname);
        
        String hostNameMatchStartString = new StringBuilder(dnsLabelPrefix.toLowerCase()).append("0").toString();
        String hostNameMatchEndString = new StringBuilder(wlsDomainName.toLowerCase())
                                            .append(".")
                                            .append(azureResourceGroupRegion.toLowerCase())
                                            .append(".")
                                            .append(azureVMExternalDomainName.toLowerCase()).toString();
        
        if(commonName.equalsIgnoreCase(urlHostname))
        {
            debug("urlhostname matching certificate common name");
            return true;
        }
        else
        if(commonName.equalsIgnoreCase(adminInternalHostName))
        {
            debug("urlhostname matching certificate common name: "+adminInternalHostName+","+commonName);
            return true;            
        }
        else
        if(commonName.equalsIgnoreCase(adminExternalHostName))
        {
            debug("urlhostname matching certificate common name: "+adminExternalHostName+","+commonName);
            return true;            
        }
        else
        if(commonName.equalsIgnoreCase(adminDNSZoneName))
        {
            debug("adminDNSZoneName matching certificate common name: "+adminDNSZoneName+","+commonName);
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
        if(debugEnabled)
            System.out.println(debugStatement);
    }
}

