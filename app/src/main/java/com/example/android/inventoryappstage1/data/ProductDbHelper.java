package com.example.android.inventoryappstage1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static java.sql.Types.INTEGER;

public class ProductDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = ProductDbHelper.class.getSimpleName();
    //name of database file
    private static final String DATABASE_NAME = "stock.db";
    //Database version
    private static final int DATABASE_VERSION = 1;
    //constructing a new instance of {@link ProductDbHelper}.
    public ProductDbHelper (Context context){super(context, DATABASE_NAME, null, DATABASE_VERSION);}
    //called when database is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase db){
        //creating a String containing the sql statement for creating a product table
        String SQL_CREATE_PRODUCT_TABLE = " CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " ("
                + ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIERNAME + " TEXT, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_PHONENR + " TEXT NOT NULL);";
        //execute the sql statement
        db.execSQL( SQL_CREATE_PRODUCT_TABLE );
    }
    //to call when database needs an upgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }
}
