package com.oracle.azure.weblogic.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class CustomHostNameVerifierGenerator
{

    public static final String CUSTOM_HOSTNAME_VERIFIER_TEMPLATE="CustomHostNameVerifierTemplate.ftl";
    public static final String OUTPUT_FILE="target/WebLogicAzureCustomHostNameVerifier.java";
    public static final String HOSTNAME_INPUT_PROPS_FILE="hostnameverifier.properties";

    private String adminInternalHostName;
    private String adminExternalHostName;
    private String adminDNSZoneName;
    private String dnsLabelPrefix;
    private String wlsDomainName;
    private String azureResourceGroupRegion;
    private String debugFlag;   
    
    private Template template = null;
    Map<String, Object> dataMap = new HashMap<String, Object>();

    private void init()
    {
        Configuration cfg = new Configuration();
        try
        {
            cfg.setClassForTemplateLoading(this.getClass(), "/ftl");
            cfg.setDefaultEncoding("UTF-8");
            template = cfg.getTemplate(CUSTOM_HOSTNAME_VERIFIER_TEMPLATE);
        } 
        catch (IOException e)
        {
            System.out.println("Error occured while initializing Freemarker Template Configuration");
            e.printStackTrace();
            System.exit(1);
        }

    }

    public void buildData()
    {
        try
        {
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
            
        } catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void writeOutputFile()
    {
        Writer file = null;
        try
        {
            System.out.println("Starting Template processing ...");
            file = new FileWriter(new File(OUTPUT_FILE));
            template.process(dataMap, file);
            file.flush();
            System.out.println("Template processing successful.");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        } 
        finally
        {
            try
            {
                if(file != null)
                    file.close();
            } catch (IOException e)
            {
                e.printStackTrace();
                System.exit(1);
            }
        }

    }

    public static void main(String[] args)
    {
        CustomHostNameVerifierGenerator generator = new CustomHostNameVerifierGenerator();
        generator.readArguments(args);
        generator.init();
        generator.buildData();
        generator.writeOutputFile();
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
