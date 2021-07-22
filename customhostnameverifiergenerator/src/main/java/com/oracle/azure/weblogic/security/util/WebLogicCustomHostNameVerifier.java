package com.oracle.azure.weblogic.security.util;

import weblogic.security.utils.SSLCertUtility;
import java.util.Properties;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;

public class WebLogicCustomHostNameVerifier implements weblogic.security.SSL.HostnameVerifier 
{

    private static String hostnamePropsFileLocation;
    public static Properties hostnameProps;

    public static boolean debugEnabled;
    public static String azureVMExternalDomainName;
    public static String adminInternalHostName;
    public static String adminExternalHostName;
    public static String adminDNSZoneName;
    public static String dnsLabelPrefix;
    public static String wlsDomainName;
    public static String azureResourceGroupRegion;
    
    static
    {
        try 
        {
            loadProperties();
        } catch (Exception e) 
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
       
    }

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
    
    private static void debug(String debugStatement)
    {
        if(debugEnabled)
            System.out.println(debugStatement);
    }

    private static void loadProperties() throws Exception
    {
        InputStream inputStream = null;
        try 
        {
            String hostnamePropsFileLocation = System.getProperty("hostname.props.location");
            File hostnamePropsFile = new File(hostnamePropsFileLocation);

            if (!hostnamePropsFile.exists())
            {
                throw new RuntimeException("hostname properties file not available at location "+hostnamePropsFileLocation);
            }

            hostnameProps = new Properties();
            inputStream = new FileInputStream(hostnamePropsFile);
            hostnameProps.load(inputStream);

            azureVMExternalDomainName=hostnameProps.getProperty("azureVMExternalDomainName","cloudapp.azure.com");
            adminInternalHostName=hostnameProps.getProperty("adminInternalHostName");
            adminExternalHostName=hostnameProps.getProperty("adminExternalHostName");
            adminDNSZoneName=hostnameProps.getProperty("adminDNSZoneName");
            dnsLabelPrefix=hostnameProps.getProperty("dnsLabelPrefix");
            wlsDomainName=hostnameProps.getProperty("wlsDomainName");
            azureResourceGroupRegion=hostnameProps.getProperty("azureResourceGroupRegion");    
            
            String debugEnabledStr=hostnameProps.getProperty("debugEnabled","false");
            debugEnabled = Boolean.parseBoolean(debugEnabledStr);
            debug("Loading hostname properties completed");
    
        } catch (Exception e) 
        {
            e.printStackTrace();
            throw e;
        }
        finally
        {
            try
            {
                if(inputStream != null)
                  inputStream.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}

