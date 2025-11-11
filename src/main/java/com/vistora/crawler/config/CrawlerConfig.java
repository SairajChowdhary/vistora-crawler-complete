package com.vistora.crawler.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

public class CrawlerConfig {
    public String jdbcUrl;
    public String username;
    public String password;
    public String[] schemas;
    public boolean generateRuntimeClasses = true;
    public String packageName = "com.vistora.generated";

    public static CrawlerConfig load(String path) throws Exception {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(new File(path), CrawlerConfig.class);
    }
}
