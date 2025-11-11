package com.vistora.crawler;
import com.vistora.crawler.config.CrawlerConfig;
import com.vistora.crawler.dto.DatabaseMetadataDTO;
import com.vistora.crawler.service.SchemaCrawlerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CrawlerIntegrationTest {
    @Autowired SchemaCrawlerService crawlerService;
    @Test void loadsMetadata() throws Exception {
        CrawlerConfig cfg = new CrawlerConfig();
        cfg.jdbcUrl = "jdbc:mysql://localhost:3306/vistora_demo?useSSL=false&serverTimezone=UTC";
        cfg.username = "root";
        cfg.password = "password";
        DatabaseMetadataDTO dto = crawlerService.crawl(cfg);
        assertNotNull(dto);
    }
}
