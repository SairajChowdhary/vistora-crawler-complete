package com.vistora.crawler.service;
import com.vistora.crawler.dto.*;
import com.vistora.crawler.config.CrawlerConfig;
import org.springframework.stereotype.Service;
import java.sql.*;
public class Dummy {}
@Service
public class SchemaCrawlerService {
    public DatabaseMetadataDTO crawl(CrawlerConfig cfg) throws Exception {
        try (Connection conn = DriverManager.getConnection(cfg.jdbcUrl, cfg.username, cfg.password)) {
            DatabaseMetaData md = conn.getMetaData();
            DatabaseMetadataDTO dbdto = new DatabaseMetadataDTO();
            dbdto.catalog = conn.getCatalog();
            dbdto.schema = conn.getSchema();
            String[] types = {"TABLE", "VIEW"};
            ResultSet tablesRs = md.getTables(null, null, "%", types);
            while (tablesRs.next()) {
                TableDTO table = new TableDTO();
                table.tableName = tablesRs.getString("TABLE_NAME");
                table.tableType = tablesRs.getString("TABLE_TYPE");
                ResultSet cols = md.getColumns(null, null, table.tableName, "%");
                while (cols.next()) {
                    ColumnDTO c = new ColumnDTO();
                    c.name = cols.getString("COLUMN_NAME");
                    c.type = cols.getString("TYPE_NAME");
                    int size = cols.getInt("COLUMN_SIZE");
                    c.size = cols.wasNull() ? null : size;
                    c.nullable = "YES".equalsIgnoreCase(cols.getString("IS_NULLABLE"));
                    c.remarks = cols.getString("REMARKS");
                    table.columns.add(c);
                }
                cols.close();
                ResultSet pk = md.getPrimaryKeys(null, null, table.tableName);
                while (pk.next()) table.primaryKeys.add(pk.getString("COLUMN_NAME"));
                pk.close();
                ResultSet fk = md.getImportedKeys(null, null, table.tableName);
                while (fk.next()) {
                    ForeignKeyDTO fkDto = new ForeignKeyDTO();
                    fkDto.fkName = fk.getString("FK_NAME");
                    fkDto.fkColumn = fk.getString("FKCOLUMN_NAME");
                    fkDto.pkTable = fk.getString("PKTABLE_NAME");
                    fkDto.pkColumn = fk.getString("PKCOLUMN_NAME");
                    table.foreignKeys.add(fkDto);
                }
                fk.close();
                ResultSet idx = md.getIndexInfo(null, null, table.tableName, false, false);
                while (idx.next()) {
                    IndexDTO index = new IndexDTO();
                    index.indexName = idx.getString("INDEX_NAME");
                    index.nonUnique = idx.getBoolean("NON_UNIQUE");
                    index.columnName = idx.getString("COLUMN_NAME");
                    index.ordinalPosition = idx.getShort("ORDINAL_POSITION");
                    table.indexes.add(index);
                }
                idx.close();
                dbdto.tables.add(table);
            }
            tablesRs.close();
            return dbdto;
        }
    }
}
