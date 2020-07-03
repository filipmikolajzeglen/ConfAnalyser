package com.company;

import java.io.*;

import java.util.HashMap;
import java.util.Properties;

public class ConfAnalyser {
    private boolean isAnalised = false;
    private String message;
    private int code = 0;

    public String getAnalyse() {
        if (!this.isAnalised) {
            this.analyse();
        }
        return this.message;
    }

    public boolean getState() {
        if (!this.isAnalised) {
            this.analyse();
        }
        return (this.code == 0) ? true : false;
    }

    private static Properties settings = null;

    public static String env(String variable) {
        if(settings == null) {
            settings = new Properties();
            try {
                settings.load(new FileInputStream("c:/SysTools/settings.config"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return settings.getProperty(variable);
    }

    public static HashMap<String, HashMap<String, String>> configuration() {
        HashMap<String, HashMap<String, String>> config = new HashMap<>();
        HashMap<String, String> hashmap = new HashMap<>();
        hashmap.put("secondary_printer", null);
        hashmap.put("secondary_printing_provider", null);
        hashmap.put("enable_message_broker", "1");
        config.put("client_props.properties", hashmap);
        return config;
    }

    void analyse() {
        HashMap<String, HashMap<String, String>> config = configuration();
        config.forEach((confname, hashmap) -> {
            try {
                Properties configFile = new Properties();
                configFile.load(new FileReader(env("PROPERTY_PATH") + confname));
                hashmap.forEach((key, value) -> {
                    if (configFile.getProperty(key) != value) {
                        this.code = 1;
                        this.message = this.message + "In File \"" + confname + "\" bad parameter value\t" + value + "\n";
                    } else if (configFile.getProperty(key) == null) {
                        this.code = 1;
                        this.message = this.message + "In File \"" + confname + "\" parameter doesn't exist\t" + value + "\n";
                    }
                });
            } catch (Exception e) {
                this.code = 1;
                this.message = this.message + "Analysis error \"" + confname + "\"" + "\n";
            }
        });
    }
}
