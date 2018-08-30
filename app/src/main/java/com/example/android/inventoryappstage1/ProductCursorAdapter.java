package com.example.android.inventoryappstage1;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryappstage1.data.ProductContract;

import java.net.URISyntaxException;

import static android.content.Intent.getIntentOld;
import static com.example.android.inventoryappstage1.data.ProductProvider.LOG_TAG;

public  class ProductCursorAdapter extends CursorAdapter implements View.OnClickListener {
    public ProductCursorAdapter(Context context, Cursor c) {
        super( context, c, 0 );
    }

    //new list item No data set yet
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from( context ).inflate( R.layout.list_item, parent, false );
    }

    //binding the data to the list item. one current product is set to on text list item layout
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final int productQuantity;

        //name price quantity to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById( R.id.name );
        TextView priceTextView = (TextView) view.findViewById( R.id.price );
        final TextView quantityTextView = (TextView) view.findViewById( R.id.quantity );

        Button sellButton = (Button) view.findViewById( R.id.sell_button );

        //find the right columns that are interesting
        int nameColumnIndex = cursor.getColumnIndex( ProductContract.ProductEntry.COLUMN_PRODUCT_NAME );
        int priceColumnIndex = cursor.getColumnIndex( ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE );
        int quantityColumnIndex = cursor.getColumnIndex( ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY );

        //to be able to change the value of the quantity
        productQuantity = Integer.parseInt( cursor.getString( quantityColumnIndex ) );
        //to read the attributes from the current product
        String prodName = cursor.getString( nameColumnIndex );
        String prodPrice = cursor.getString( priceColumnIndex );
        final String prodQuantity = cursor.getString( quantityColumnIndex );

        //update the view with the current attributes
        nameTextView.setText( prodName );
        priceTextView.setText( prodPrice );
        quantityTextView.setText( Integer.toString( Integer.parseInt( prodQuantity ) ) );
        quantityTextView.setTag( cursor.getInt( cursor.getColumnIndex( ProductContract.ProductEntry._ID ) ) );


        sellButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int loacate = (int) quantityTextView.getTag();
                Uri mCurrentProductUri = ContentUris.withAppendedId( ProductContract.ProductEntry.CONTENT_URI, loacate );

                ContentValues change = new ContentValues();
                int updateProdQuantity;
                switch (v.getId()) {
                    //decreases quantity
                    case R.id.sell_button:
                        updateProdQuantity = productQuantity - 1;
                        if (updateProdQuantity < 0) {
                            Toast.makeText( v.getContext(), "no products here anymore", Toast.LENGTH_SHORT ).show();
                        } else {
                            change.put( ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, updateProdQuantity );
                            int changedProduct = v.getContext().getContentResolver().update( mCurrentProductUri, change, null, null );
                            //log message, that updating the quantity failed, when it failed
                            if (changedProduct == 0) {
                                Log.e( LOG_TAG, "updating quantity failed" );
                            }
                        }
                        break;
                }
            }
        } );
    }

    @Override
    public void onClick(View v) {
        // this view is not unused as I implemented a  View.OnClickListener in the class and without this
        // view the class needs to be declared abstract and cannot be instantiated afterwards.
        // It's becauase the onClick I wanted to use is inside the class I guess !?
    }
}
