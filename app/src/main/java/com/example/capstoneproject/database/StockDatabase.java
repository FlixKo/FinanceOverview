package com.example.capstoneproject.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.capstoneproject.model.Stock;

@Database(entities = {Stock.class},version = 2,exportSchema = false)
public abstract class StockDatabase extends RoomDatabase {
    private static final String LOG_TAG = StockDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "stock";
    private static StockDatabase sInstance;

    public static StockDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        StockDatabase.class, StockDatabase.DATABASE_NAME)
                        //.allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .addMigrations(MIGRATION_1_2)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE stock "
                    + " ADD COLUMN numberShares REAL DEFAULT 0 not null");
        }
    };
    public abstract StockDao stockDao();
}
