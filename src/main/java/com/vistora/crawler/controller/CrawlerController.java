package com.vistora.crawler.controller;
import com.vistora.crawler.service.SchemaCrawlerService;
import com.vistora.crawler.service.RuntimeModelGenerator;
import com.vistora.crawler.service.SourceFileGenerator;
import com.vistora.crawler.config.CrawlerConfig;
import com.vistora.crawler.dto.DatabaseMetadataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api")
public class CrawlerController {
    @Autowired SchemaCrawlerService crawler;
    @Autowired RuntimeModelGenerator generator;
    @Autowired SourceFileGenerator sourceFileGenerator;

    @GetMapping("/metadata")
    public DatabaseMetadataDTO metadata(@RequestParam(value="config", defaultValue="config/config.json") String configPath) throws Exception {
        CrawlerConfig cfg = CrawlerConfig.load(configPath);
        return crawler.crawl(cfg);
    }

    @PostMapping("/generate")
    public Map<String,Object> generate(@RequestParam(value="config", defaultValue="config/config.json") String configPath,
                                       @RequestParam(value="persist", defaultValue="false") boolean persist) throws Exception {
        CrawlerConfig cfg = CrawlerConfig.load(configPath);
        DatabaseMetadataDTO db = crawler.crawl(cfg);
        List<String> generated = new ArrayList<>();
        if (cfg.generateRuntimeClasses) generated = generator.generateModels(db, cfg);
        List<String> written = new ArrayList<>();
        if (persist) written = sourceFileGenerator.persistSources(db, cfg, System.getProperty("user.dir"));
        Map<String,Object> res = new HashMap<>();
        res.put("generatedClasses", generated);
        res.put("writtenJavaSources", written);
        res.put("tables", db.tables.size());
        return res;
    }
}
