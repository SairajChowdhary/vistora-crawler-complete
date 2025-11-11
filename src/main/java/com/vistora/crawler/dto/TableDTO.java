package com.vistora.crawler.dto;
import java.util.*;
public class TableDTO {
    public String tableName;
    public String tableType;
    public List<ColumnDTO> columns = new ArrayList<>();
    public List<String> primaryKeys = new ArrayList<>();
    public List<ForeignKeyDTO> foreignKeys = new ArrayList<>();
    public List<IndexDTO> indexes = new ArrayList<>();
}
