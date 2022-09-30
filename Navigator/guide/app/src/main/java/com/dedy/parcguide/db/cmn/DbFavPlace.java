package com.dedy.parcguide.db.cmn;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by dedy ngueko on 12/3/2020.
 */
@DatabaseTable(tableName = "db_fav_place")
public class DbFavPlace {

    @DatabaseField(columnName = "id", canBeNull = false, generatedId = true, unique = true)
    private int id;

    @DatabaseField(columnName = "place_id")
    private String placeId;

    @DatabaseField(columnName = "place_json")
    private String placeJson;

    public int getId() {
        return id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceJson() {
        return placeJson;
    }

    public void setPlaceJson(String placeJson) {
        this.placeJson = placeJson;
    }
}
