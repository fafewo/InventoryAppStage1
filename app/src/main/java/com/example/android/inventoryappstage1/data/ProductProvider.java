package com.example.android.inventoryappstage1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class ProductProvider extends ContentProvider {
    //for log messages
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();
    private ProductDbHelper mDbHelper;
    //initializing provider and database helper object
    @Override
    public boolean onCreate (){
        mDbHelper = new ProductDbHelper( getContext() );
        return true;
    }
    //perform the query for the URI
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        // for getting a readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        //for holding the query result a cursor
        Cursor cursor;

        //does the URI matcher match the URI to specific code?
         int match = sUriMatcher.match(uri);
         switch (match){
             case PRODUCTS:
                 cursor = database.query( ProductContract.ProductEntry.TABLE_NAME,projection,selection,selectionArgs, null, null, sortOrder );
                 break;
             case PRODUCT_ID:
                 selection = ProductContract.ProductEntry._ID + "=?";
                 selectionArgs = new String[]{String.valueOf( ContentUris.parseId( uri ))};

                 //performing a query on the products table where Id = 3. It returns a cursor containing that row of the table
                 cursor = database.query(ProductContract.ProductEntry.TABLE_NAME,projection,selection,selectionArgs, null,null, sortOrder);
                 break;
             default:
                 throw  new IllegalArgumentException( "Cannot query unknown URI" + uri );
         }
         //notification URI to see for what content the cursor was created for
         cursor.setNotificationUri( getContext().getContentResolver(), uri );
         return cursor;
    }
    //new data into the content provider with given content values
    @Override
    public Uri insert (Uri uri, ContentValues contentValues){
        final  int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                return insertProduct(uri, contentValues);
                default:
                    throw  new IllegalArgumentException( "no insert suppoert for"+ uri );
        }
    }
    private Uri insertProduct(Uri uri, ContentValues values){
        //check name is not null and price and quantity, phone nr., and supplier name
        String name = values.getAsString( ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null){
            throw new IllegalArgumentException( "Product needs to be named" );
        }
        Integer price = values.getAsInteger( ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price != null && price<0 ) {
            throw new IllegalArgumentException(" negative not allowed price of the product needs to be defined ");
        }
        Integer quantity = values.getAsInteger( ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY );
        if ( quantity == null || quantity < 0 ){
            throw new IllegalArgumentException( "the quantity needs to be greater 0" );
        }
        String supname = values.getAsString( ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIERNAME );
        if (supname == null){
            throw new IllegalArgumentException( "the name of the supplier needs to be entered " );
        }
        String phone = values.getAsString( ProductContract.ProductEntry.COLUMN_PRODUCT_PHONENR );
        if (phone == null){
            throw new IllegalArgumentException( "phonenr needs to be entered" );
        }
        //to get the writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        //insert new producct with with given values
        long id = database.insert( ProductContract.ProductEntry.TABLE_NAME, null,values );
        if (id ==-1){
            Log.e(LOG_TAG, "insert row fail for " +uri);
            return null;
        }
        //when data for product uri has changed notify the listeners
        getContext().getContentResolver().notifyChange( uri, null );
        //return the new uri with the id appended at the end
        return ContentUris.withAppendedId( uri, id );
    }
    //updating  the data at the given selection and selection argument with the new contetn values
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    //update the products in the database with the given content values
    private int updateProduct (Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey( ProductContract.ProductEntry.COLUMN_PRODUCT_NAME )){
            String name = values.getAsString( ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null){
                throw new IllegalArgumentException( "Product needs to be named" );
            }
        }
        if (values.containsKey( ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE )) {
            Integer price = values.getAsInteger( ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE );
            if (price != null && price < 0) {
                throw new IllegalArgumentException( "the price of the product needs to be defined not negative" );
            }
        }
        if (values.containsKey( ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY )) {
            Integer quantity = values.getAsInteger( ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY );
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException( "the quantity needs to be greater 0" );
            }
        }
        if (values.containsKey( ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIERNAME )) {
            String supname = values.getAsString( ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIERNAME );
            if (supname == null) {
                throw new IllegalArgumentException( "the name of the supplier needs to be entered " );
            }
        }
        if (values.containsKey( ProductContract.ProductEntry.COLUMN_PRODUCT_PHONENR )) {
            Integer phone = values.getAsInteger( ProductContract.ProductEntry.COLUMN_PRODUCT_PHONENR );
            if (phone == null) {
                throw new IllegalArgumentException( "phonenr needs to be entered" );
            }
        }
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
    }
        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update( ProductContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }
    //delete the data at teh given selection
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Track the number of rows that were deleted
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Delete all rows matching the selection and selection args
                rowsDeleted = database.delete( ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete( ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }
    //returning MIME type of data for the contetn uri
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
    //product table uri matcher code for the content URI
    private static final int PRODUCTS = 100;
    //single product uri matcher code for the content Uri
    private static final int PRODUCT_ID = 101;
    //uri matcher object for matching a content URI to a corresponding code
    private static final UriMatcher sUriMatcher = new UriMatcher( UriMatcher.NO_MATCH );
    //static initializer is run the first time after something is called from this class
    static {
        sUriMatcher.addURI( ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS );
        sUriMatcher.addURI( ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID );
    }
}
