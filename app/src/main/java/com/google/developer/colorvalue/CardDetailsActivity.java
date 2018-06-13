package com.google.developer.colorvalue;

import android.app.IntentService;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.developer.colorvalue.data.CardProvider;
import com.google.developer.colorvalue.service.CardService;
import com.google.developer.colorvalue.ui.ColorView;

public class CardDetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_CARD_LOADER = 0;


    private Uri mCurrentCardUri;
    private TextView mColorNameTV;
    private ColorView mColorView;
    private TextView mColorValueTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        Intent intent = getIntent();
        mCurrentCardUri = intent.getData();


        mColorNameTV = (TextView)findViewById(R.id.colorNameView);
        mColorView = (ColorView)findViewById(R.id.colorView);
        mColorValueTV =(TextView)findViewById(R.id.colorValueTv);

        getLoaderManager().initLoader(EXISTING_CARD_LOADER, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            deleteCard();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteCard() {
        if (mCurrentCardUri != null) {
            //Start intent service
            Intent intent = new Intent();
//            intent.
            int rowsDeleted = getContentResolver().delete(mCurrentCardUri, null, null);
            //int rowsDeleted = CardService.
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.delete__failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.delete_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                CardProvider.Contract.Columns._ID,
                CardProvider.Contract.Columns.COLOR_NAME,
                CardProvider.Contract.Columns.COLOR_HEX };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentCardUri,
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(CardProvider.Contract.Columns.COLOR_NAME);
            int colorColumnIndex = cursor.getColumnIndex(CardProvider.Contract.Columns.COLOR_HEX );

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String color = cursor.getString(colorColumnIndex);

            // Update the views on the screen with the values from the database
            mColorNameTV.setText(name);
            mColorView.setColor(Color.parseColor(color));
            mColorValueTV.setText(color);


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mColorNameTV.setText("");
        mColorView.setColor(Color.WHITE);
        mColorValueTV.setText("");
    }
}