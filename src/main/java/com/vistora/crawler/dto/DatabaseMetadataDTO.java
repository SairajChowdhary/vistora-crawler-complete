package com.vistora.crawler.dto;
import java.util.*;
public class DatabaseMetadataDTO {
    public String catalog;
    public String schema;
    public List<TableDTO> tables = new ArrayList<>();
}
