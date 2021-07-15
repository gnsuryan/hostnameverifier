package com.oracle.azure.weblogic.security.util;

import weblogic.security.SSL.HostnameVerifier;
import weblogic.security.utils.SSLCertUtility;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.security.cert.X509Certificate;

public class WebLogicAzureCustomHostNameVerifier implements weblogic.security.SSL.HostnameVerifier
{
	private static final boolean debugEnabled=${debugFlag};
	
	private static final String azureVMExternalDomainName="cloudapp.azure.com";
	
	private String adminInternalHostName="${adminInternalHostName}";
	private String adminExternalHostName="${adminExternalHostName}";
	private String adminDNSZoneName="${adminDNSZoneName}";
	private String dnsLabelPrefix="${dnsLabelPrefix}";
	private String wlsDomainName="${wlsDomainName}";
	private String azureResourceGroupRegion="${azureResourceGroupRegion}";
	 	

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
