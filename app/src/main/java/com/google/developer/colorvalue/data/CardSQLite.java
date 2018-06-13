package com.google.developer.colorvalue.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.developer.colorvalue.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Helper class to manage database
 */
public class CardSQLite extends SQLiteOpenHelper {

    private static final String TAG = CardSQLite.class.getName();
    private static final String DB_NAME = "colorvalue.db";
    private static final int DB_VERSION = 1;

    private Resources mResources;
    private Context mContext;
    public CardSQLite(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mResources = context.getResources();
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create and fill the database
        String SQL_CREATE_CARD_TABLE = "CREATE TABLE " + CardProvider.Contract.TABLE_NAME + " ("
                + CardProvider.Contract.Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CardProvider.Contract.Columns.COLOR_NAME + " TEXT NOT NULL, "
                + CardProvider.Contract.Columns.COLOR_HEX + " TEXT NOT NULL );";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_CARD_TABLE);
        //addDemoCards(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static String getColumnString(Cursor cursor, String name) {
        return cursor.getString(cursor.getColumnIndex(name));
    }

    public static int getColumnInt(Cursor cursor, String name) {
        return cursor.getInt(cursor.getColumnIndex(name));
    }

    /**
     * save demo cards into database
     */
    private void addDemoCards(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            try {
                readCardsFromResources(db);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Unable to pre-fill database", e);
        }
    }

    /**
     * load demo color cards from {@link raw/colorcards.json}
     */
    private void readCardsFromResources(SQLiteDatabase db) throws IOException, JSONException {
        StringBuilder builder = new StringBuilder();
        InputStream in = mResources.openRawResource(R.raw.colorcards);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        //Parse resource into key/values
        JSONObject root = new JSONObject(builder.toString());
        JSONArray array = root.getJSONArray("cards");

        for(int i=0;i<array.length();i++)
        {
            JSONObject jsonObject = array.getJSONObject(i);
            ContentValues values = new ContentValues(2);
            String hex =  jsonObject.getString("hex");
            String name =  jsonObject.getString("name");
            values.put(CardProvider.Contract.Columns.COLOR_HEX, jsonObject.getString("hex"));
            values.put(CardProvider.Contract.Columns.COLOR_NAME, jsonObject.getString("name"));
            db.execSQL("insert into cards (hex,name)" + "values("+hex+","+name+ "\"" + ") ;");
//            db.execSQL("INSERT INTO "+CardProvider.Contract.TABLE_NAME+
//            "");

            mContext.getContentResolver().insert(CardProvider.Contract.CONTENT_URI, values);
        }
    }

}
