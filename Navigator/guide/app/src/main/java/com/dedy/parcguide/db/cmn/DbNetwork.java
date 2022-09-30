package com.dedy.parcguide.db.cmn;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by dedy ngueko on 12/3/2020.
 */
@DatabaseTable(tableName = "db_network")
public class DbNetwork {

    @DatabaseField(columnName = "id", canBeNull = false, generatedId = true, unique = true)
    private int id;

    @DatabaseField(columnName = "key", unique = true)
    private String key;

    @DatabaseField(columnName = "value")
    private String value;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
