package com.example.android.inventoryappstage1.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ProductContract {
    private ProductContract(){}

    //the content authority
    public static final String CONTENT_AUTHORITY ="com.example.android.inventoryappstage1";

    //create the base of the URI to make contact from other apps possible

    public static final Uri BASE_CONTENT_URI = Uri.parse( "content://"+ CONTENT_AUTHORITY);

    public static final String PATH_PRODUCTS ="products";

    public static final class ProductEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath( BASE_CONTENT_URI, PATH_PRODUCTS);
            // MIME type for a list of products
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
            //MIME type for a single product
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String TABLE_NAME = "Products";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "Name";
        public static final String COLUMN_PRODUCT_PRICE = "Price";
        public static final String COLUMN_PRODUCT_QUANTITY = "Quantity";
        public static final String COLUMN_PRODUCT_SUPPLIERNAME= "SupplierName";
        public static final String COLUMN_PRODUCT_PHONENR = "SupplierPhoneNumber";
    }
}
