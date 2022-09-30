package com.dedy.parcguide.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dedy.parcguide.db.cmn.DbFavPlace;
import com.dedy.parcguide.db.cmn.DbNetwork;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by dedy ngueko on 12/3/2020.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String LOG_TAG = DatabaseHelper.class.getName();

    private static final String DATABASE_NAME = "cityguide.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<DbFavPlace, Long> mFavItemDao = null;
    private Dao<DbNetwork, Long> mDbNetworkDao = null;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, DbFavPlace.class);

            TableUtils.createTable(connectionSource, DbNetwork.class);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error creating database", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, DbFavPlace.class, true);
            TableUtils.createTable(connectionSource, DbFavPlace.class);

            TableUtils.dropTable(connectionSource, DbNetwork.class, true);
            TableUtils.createTable(connectionSource, DbNetwork.class);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error upgrading database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns Category Dao object
     *
     * @return
     * @throws SQLException
     */
    public Dao<DbFavPlace, Long> getFavItemDao() throws SQLException {
        if (mFavItemDao == null) {
            mFavItemDao = getDao(DbFavPlace.class);
        }

        return mFavItemDao;
    }

    public Dao<DbNetwork, Long> getNetworkDao() throws SQLException {
        if (mDbNetworkDao == null) {
            mDbNetworkDao = getDao(DbNetwork.class);
        }

        return mDbNetworkDao;
    }
}
