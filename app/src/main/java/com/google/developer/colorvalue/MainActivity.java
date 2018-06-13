package com.google.developer.colorvalue;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;

import com.google.developer.colorvalue.data.CardAdapter;
import com.google.developer.colorvalue.data.CardProvider;
import com.google.developer.colorvalue.data.CardSQLite;
import com.google.developer.colorvalue.listners.RecyclerItemClickListener;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the card data loader */
    private static final int CARD_LOADER = 0;
    private static final String MY_PREFS_NAME = "MY_PREFS_NAME";

    private CardAdapter mCardAdapter;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                startActivity(intent);
            }
        });

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);

        mCardAdapter = new CardAdapter();
        recycler.setAdapter(mCardAdapter);
        recycler.setHasFixedSize(true);

        recycler.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recycler ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this, CardDetailsActivity.class);


                mCursor.moveToPosition(position);
                int id = mCursor.getInt(mCursor.getColumnIndex(CardProvider.Contract.Columns._ID));
                Uri currentCardUri = ContentUris.withAppendedId(CardProvider.Contract.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentCardUri);

                startActivity(intent);
                    }

                }));
        //Pre-Populate cards
//        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
//        editor.putBoolean(getString(R.string.shared_pref_already_populated_key), false);
//        editor.apply();
//        editor.commit();
//
//
//        SharedPreferences prefs = getSharedPref erences(MY_PREFS_NAME, MODE_PRIVATE);
//        Boolean flag = prefs.getBoolean(getString(R.string.shared_pref_already_populated_key), false);
//
//        if(flag)
//        {
//
//        }
//        else{
//            mCardSQLite = new CardSQLite(this);
//            mCardSQLite.getReadableDatabase();
//            mCardSQLite.
//            SharedPreferences.Editor editor2 = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
//            editor2.putBoolean(getString(R.string.shared_pref_already_populated_key), true);
//            editor2.apply();
//            editor2.commit();
//
//        }

        // Kick off the loader
        getLoaderManager().initLoader(CARD_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                CardProvider.Contract.Columns._ID,
                CardProvider.Contract.Columns.COLOR_NAME,
                CardProvider.Contract.Columns.COLOR_HEX };


        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                CardProvider.Contract.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        mCardAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCardAdapter.swapCursor(null);

    }
}
