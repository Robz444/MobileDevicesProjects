package com.example.robert.assignment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Robert on 04-Nov-2015.
 */
public class ProductDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_FILENAME = "product.db";
    public static final String TABLE_NAME = "Products";

    // don't forget to use the column name '_id' for your primary key
    public static final String CREATE_STATEMENT = "CREATE TABLE " + TABLE_NAME + "(" +
            "  _productId int primary key," +
            "  name varchar(100) not null," +
            "  description varchar(100) not null," +
            "  price decimal not null" +
            ")";

    public static final String DROP_STATEMENT = "DROP TABLE " + TABLE_NAME;

    public ProductDBHelper(Context context) {
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // the implementation below is adequate for the first version
        // however, if we change our table at all, we'd need to execute code to move the data
        // to the new table structure, then delete the old tables (renaming the new ones)

        // the current version destroys all existing data
        database.execSQL(DROP_STATEMENT);
        database.execSQL(CREATE_STATEMENT);
    }

    public Product createProduct(int pid, String name, String desc, float price) {
        // create the object
        Product product = new Product(pid,name,desc,price);

        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // insert the data into the database
        ContentValues values = new ContentValues();
        values.put("_productId", product.getProductId());
        values.put("name", product.getName());
        values.put("description", product.getDescription());
        values.put("price", product.getPrice());

        return product;
    }

    public Product getProduct(int id) {
        Product product = null;

        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // retrieve the contact from the database
        String[] columns = new String[] { "_productId", "name", "description", "price" };
        Cursor cursor = database.query(TABLE_NAME, columns, "_productId = ?", new String[] { "" + id }, "", "", "");
        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            int productId = cursor.getInt(0);
            String name = cursor.getString(1);
            String desc = cursor.getString(2);
            float mark = cursor.getFloat(3);
            product = new Product(productId, name, desc, mark);
        }

        Log.i("DatabaseAccess", "getProduct(" + id + "):  product: " + product);

        return product;
    }

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> products = new ArrayList<>();

        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // retrieve the contact from the database
        String[] columns = new String[] { "_productId", "name", "description", "price" };
        Cursor cursor = database.query(TABLE_NAME, columns, "", new String[]{}, "", "", "");
        cursor.moveToFirst();
        do {
            // collect the contact data, and place it into a contact object
            int productId = cursor.getInt(0);
            String name = cursor.getString(1);
            String desc = cursor.getString(2);
            float price = cursor.getFloat(3);

            Product product = new Product(productId, name, desc, price);
            // add the current contact to the list
            products.add(product);

            // advance to the next row in the results
            cursor.moveToNext();
        } while (!cursor.isAfterLast());

        Log.i("DatabaseAccess", "getAllContacts():  num: " + products.size());

        return products;
    }
    public boolean updateProduct(Product product) {
        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // update the data in the database
        ContentValues values = new ContentValues();
        values.put("_productId", product.getProductId());
        values.put("name", product.getName());
        values.put("description", product.getDescription());
        values.put("price", product.getPrice());
        int numRowsAffected = database.update(TABLE_NAME, values, "_productId = ?", new String[] { "" + product.getProductId() });

        Log.i("DatabaseAccess", "updateContact(" + product + "):  numRowsAffected: " + numRowsAffected);

        // verify that the contact was updated successfully
        return (numRowsAffected == 1);
    }

    public boolean deleteProduct(int id) {
        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // delete the contact
        int numRowsAffected = database.delete(TABLE_NAME, "_productId = ?", new String[] { "" + id });

        Log.i("DatabaseAccess", "deleteProduct(" + id + "):  numRowsAffected: " + numRowsAffected);

        // verify that the contact was deleted successfully
        return (numRowsAffected == 1);
    }

    public void deleteAllProducts() {
        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // delete the contact
        int numRowsAffected = database.delete(TABLE_NAME, "", new String[] {});

        Log.i("DatabaseAccess", "deleteAllProducts():  numRowsAffected: " + numRowsAffected);
    }
}