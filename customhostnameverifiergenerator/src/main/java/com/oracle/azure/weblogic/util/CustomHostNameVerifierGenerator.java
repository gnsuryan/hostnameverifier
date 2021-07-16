package com.oracle.azure.weblogic.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class CustomHostNameVerifierGenerator
{
    public static String TEMPLATE_NAME="CustomHostNameVerifierTemplate.java.template";
    public static final String HOSTNAME_INPUT_PROPS_FILE="target/hostnameverifier.properties";
    public static final String OUTPUT_HOSTNAME_VERIFIER_JAVA_FILE="target/WebLogicAzureCustomHostNameVerifier.java";

    private String adminInternalHostName;
    private String adminExternalHostName;
    private String adminDNSZoneName;
    private String dnsLabelPrefix;
    private String wlsDomainName;
    private String azureResourceGroupRegion;
    private String debugFlag;   
    
    public Map<String, String> buildData() throws IOException
    {
        Map<String, String> dataMap = new HashMap<String, String>();

        dataMap.put("adminInternalHostName", this.adminInternalHostName);
        dataMap.put("adminExternalHostName", this.adminExternalHostName);
        dataMap.put("adminDNSZoneName", this.adminDNSZoneName);
        dataMap.put("dnsLabelPrefix", this.dnsLabelPrefix);
        dataMap.put("wlsDomainName", this.wlsDomainName);
        dataMap.put("azureResourceGroupRegion", this.azureResourceGroupRegion);
        dataMap.put("debugFlag",this.debugFlag);

        Properties props = new Properties();
        props.putAll(dataMap);
        props.store(new FileOutputStream(HOSTNAME_INPUT_PROPS_FILE), null);

        return dataMap;
    }

    public void writeFile(String content)
    {
        Writer writer = null;
        try
        {
            writer = new FileWriter(new File(OUTPUT_HOSTNAME_VERIFIER_JAVA_FILE));
            writer.write(content);
            System.out.println("Success");

        } catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        } 
        finally
        {
            try
            {
                if(writer != null)
                    writer.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    public String processTemplate(Map<String,String> dataMap) throws IOException,java.net.URISyntaxException
    {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(TEMPLATE_NAME);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;

        // read bytes from the input stream and store them in the buffer
        while ((len = is.read(buffer)) != -1)
        {
            os.write(buffer, 0, len);
        }

        byte[] encoded = os.toByteArray();

        String templateFileContent = new String(encoded, Charset.forName("UTF-8"));

        Iterator<Entry<String, String>> it = dataMap.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<String, String> set = (Map.Entry<String, String>) it.next();
            String key=set.getKey();
            String value=(String) set.getValue();
            String stringToBeReplaced="${"+key+"}";
            templateFileContent=templateFileContent.replace(stringToBeReplaced, value);
        }

        return templateFileContent;

    }

    public static void main(String[] args)
    {
        try
        {
            CustomHostNameVerifierGenerator generator = new CustomHostNameVerifierGenerator();
            generator.readArguments(args);
            Map dataMap = generator.buildData();
            generator.writeFile(generator.processTemplate(dataMap));
        } catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void readArguments(String[] args)
    {
        if(args != null && args.length >= 6)
        {
            this.adminInternalHostName = args[0];
            this.adminExternalHostName = args[1];
            this.adminDNSZoneName = args[2];
            this.dnsLabelPrefix = args[3];
            this.wlsDomainName = args[4];
            this.azureResourceGroupRegion = args[5];
            this.debugFlag="false";
            
            if(args.length > 6)
            {
                this.debugFlag=args[6];
            }
        }
        else
        {
           usage();
        }
    }
    
    private static void usage()
    {
        System.out.println("Usage: java CustomHostNameVerifierGenerator <adminInternalHostName> <adminExternalHostName> <adminDNSZoneName> <dnsLabelPrefix> <wlsDomainName> <azureResourceGroupRegion> [<debugFlag>]");
        System.exit(1);
    }

}
