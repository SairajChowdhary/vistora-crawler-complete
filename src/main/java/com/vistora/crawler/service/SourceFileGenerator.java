package com.vistora.crawler.service;
import com.vistora.crawler.dto.*;
import com.vistora.crawler.config.CrawlerConfig;
import org.springframework.stereotype.Component;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
@Component
public class SourceFileGenerator {
    public List<String> persistSources(DatabaseMetadataDTO db, CrawlerConfig cfg, String baseDir) throws Exception {
        Path generatedRoot = Paths.get(baseDir, "target", "generated-sources", "db");
        Path pkgDir = generatedRoot.resolve(cfg.packageName.replace('.', '/'));
        Files.createDirectories(pkgDir);
        List<String> written = new ArrayList<>();
        for (TableDTO t : db.tables) {
            String src = render(t, cfg);
            String cls = toClassName(t.tableName);
            Path f = pkgDir.resolve(cls + ".java");
            Files.write(f, src.getBytes());
            written.add(f.toString());
        }
        return written;
    }
    private String render(TableDTO t, CrawlerConfig cfg) {
        String pkg = "package " + cfg.packageName + ";

";
        StringBuilder sb = new StringBuilder();
        sb.append("public class ").append(toClassName(t.tableName)).append(" {\n");
        for (ColumnDTO c : t.columns) {
            String type = mapSqlTypeToJava(c.type, c.size);
            sb.append("    private ").append(simpleName(type)).append(" ").append(toFieldName(c.name)).append(";\n");
        }
        sb.append("\n    public ").append(toClassName(t.tableName)).append("() {}\n");
        sb.append("}\n");
        return pkg + sb.toString();
    }
    private String simpleName(String fq) {
        int i = fq.lastIndexOf('.');
        return i>=0?fq.substring(i+1):fq;
    }
    private String toClassName(String tbl) {
        String[] p = tbl.split("_");
        StringBuilder sb=new StringBuilder();
        for (String s: p) if(!s.isEmpty()) sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1).toLowerCase());
        return sb.toString();
    }
    private String toFieldName(String name) {
        String[] parts = name.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<parts.length;i++) {
            String p = parts[i].toLowerCase();
            if (i==0) sb.append(p); else sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
        }
        return sb.toString();
    }
    private String mapSqlTypeToJava(String sqlType, Integer size) {
        if (sqlType==null) return "java.lang.String";
        String t = sqlType.toLowerCase();
        if (t.contains("bigint")) return "java.lang.Long";
        if (t.contains("int")) return "java.lang.Integer";
        if (t.contains("decimal")||t.contains("numeric")) return "java.math.BigDecimal";
        if (t.contains("datetime")||t.contains("timestamp")) return "java.time.LocalDateTime";
        if (t.contains("date")) return "java.time.LocalDate";
        if (t.contains("time")) return "java.time.LocalTime";
        if (t.contains("tinyint") || t.contains("bit")) return "java.lang.Boolean";
        return "java.lang.String";
    }
}
