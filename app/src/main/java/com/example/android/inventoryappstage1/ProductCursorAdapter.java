package com.example.android.inventoryappstage1;


import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryappstage1.data.ProductContract;

public class ProductCursorAdapter extends CursorAdapter {
    public  ProductCursorAdapter (Context context, Cursor c){
        super(context, c,0);
    }
    //new list item No data set yet
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return LayoutInflater.from( context ).inflate(R.layout.list_item,parent,false);
    }
    //binding the data to the list item. one current product is set to on text list item layout
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //name price quantity to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById( R.id.name );
        TextView priceTextView = (TextView)view.findViewById( R.id.price );
        TextView quantityTextView = (TextView)view.findViewById( R.id.quantity );
        Button sellButton = (Button)view.findViewById( R.id.sell_button );
        //find the right columns that are interesting
        int nameColumnIndex = cursor.getColumnIndex( ProductContract.ProductEntry.COLUMN_PRODUCT_NAME );
        int priceColumnIndex = cursor.getColumnIndex( ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE );
        int quantityColumnIndex = cursor.getColumnIndex( ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY );
        //to read the attributes from the current product
        String prodName = cursor.getString( nameColumnIndex );
        String prodPrice = cursor.getString( priceColumnIndex );
        String prodQuantity = cursor.getString( quantityColumnIndex );
        //update the view with the current attributes
        nameTextView.setText( prodName );
        priceTextView.setText( prodPrice );
        quantityTextView.setText(prodQuantity );


        // click listener to open Detailactivity
/**
        detailsButton.requestFocus();
        detailsButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( ProductCursorAdapter.this, EditorActivity.class );
                ProductCursorAdapter.this.startActivity(intent);
            } */
/**
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent( ProductCursorAdapter.this, EditorActivity.class );
                Uri currentProductUri = ContentUris.withAppendedId( ProductContract.ProductEntry.CONTENT_URI, id );
                intent.setData( currentProductUri );
                startActivity( intent );
            }
        } );*/


    }
}