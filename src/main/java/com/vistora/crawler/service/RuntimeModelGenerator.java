package com.vistora.crawler.service;
import com.vistora.crawler.dto.*;
import com.vistora.crawler.config.CrawlerConfig;
import javassist.*;
import org.springframework.stereotype.Component;
import java.util.*;
@Component
public class RuntimeModelGenerator {
    public List<String> generateModels(DatabaseMetadataDTO db, CrawlerConfig cfg) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        List<String> generated = new ArrayList<>();
        for (TableDTO t : db.tables) {
            String className = cfg.packageName + "." + toClassName(t.tableName);
            CtClass cc = pool.makeClass(className);
            CtConstructor cons = new CtConstructor(new CtClass[]{}, cc);
            cons.setBody("{}");
            cc.addConstructor(cons);
            for (ColumnDTO col : t.columns) {
                String fieldType = mapSqlTypeToJava(col.type, col.size);
                CtField f = new CtField(resolveCtClass(fieldType), toFieldName(col.name), cc);
                f.setModifiers(Modifier.PRIVATE);
                cc.addField(f);
                cc.addMethod(CtNewMethod.getter("get" + capitalize(toFieldName(col.name)), f));
                cc.addMethod(CtNewMethod.setter("set" + capitalize(toFieldName(col.name)), f));
            }
            for (ForeignKeyDTO fk : t.foreignKeys) {
                String refClass = cfg.packageName + "." + toClassName(fk.pkTable);
                CtField rf = new CtField(pool.get(refClass), toFieldName(fk.fkColumn) + "Ref", cc);
                rf.setModifiers(Modifier.PRIVATE);
                cc.addField(rf);
                cc.addMethod(CtNewMethod.getter("get" + capitalize(toFieldName(fk.fkColumn)) + "Ref", rf));
                cc.addMethod(CtNewMethod.setter("set" + capitalize(toFieldName(fk.fkColumn)) + "Ref", rf));
            }
            cc.toClass();
            generated.add(className);
        }
        return generated;
    }
    private String toClassName(String table) {
        String[] parts = table.split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) sb.append(capitalize(p.toLowerCase()));
        return sb.toString();
    }
    private String toFieldName(String name) {
        String[] parts = name.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<parts.length;i++) {
            String p = parts[i].toLowerCase();
            if (i==0) sb.append(p);
            else sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
        }
        return sb.toString();
    }
    private String capitalize(String s) {
        if (s==null||s.length()==0) return s;
        return s.substring(0,1).toUpperCase()+s.substring(1);
    }
    private CtClass resolveCtClass(String type) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        switch(type) {
            case "int": return CtClass.intType;
            case "long": return CtClass.longType;
            case "double": return CtClass.doubleType;
            case "boolean": return CtClass.booleanType;
            default: return pool.get("java.lang.String");
        }
    }
    private String mapSqlTypeToJava(String sqlType, Integer size) {
        if (sqlType==null) return "java.lang.String";
        String t = sqlType.toLowerCase();
        if (t.contains("bigint")) return "long";
        if (t.contains("int")) return "int";
        if (t.contains("decimal")||t.contains("numeric")) return "java.math.BigDecimal";
        if (t.contains("datetime")||t.contains("timestamp")) return "java.time.LocalDateTime";
        if (t.contains("date")) return "java.time.LocalDate";
        if (t.contains("time")) return "java.time.LocalTime";
        if (t.contains("tinyint") || t.contains("bit")) return "boolean";
        return "java.lang.String";
    }
}
