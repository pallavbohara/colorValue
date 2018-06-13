package com.google.developer.colorvalue.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.google.developer.colorvalue.data.CardProvider.Contract.TABLE_NAME;

public class CardProvider extends ContentProvider {

    public static final String LOG_TAG = CardProvider.class.getSimpleName();
    /** Matcher identifier for all cards */
    private static final int CARD = 100;
    /** Matcher identifier for one card */
    private static final int CARD_WITH_ID = 102;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // content://com.google.developer.colorvalue/cards
        sUriMatcher.addURI(CardProvider.Contract.CONTENT_AUTHORITY,
                TABLE_NAME, CARD);
        // content://com.google.developer.colorvalue/cards/#
        sUriMatcher.addURI(CardProvider.Contract.CONTENT_AUTHORITY,
                TABLE_NAME + "/#", CARD_WITH_ID);
    }

    private CardSQLite mCardSQLite;

    @Override
    public boolean onCreate() {
        mCardSQLite = new CardSQLite(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
            @Nullable String selection, @Nullable String[] selectionArgs,
            @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mCardSQLite.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CARD:
                cursor = database.query(Contract.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CARD_WITH_ID:
                selection = Contract.Columns._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };


                cursor = database.query(Contract.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CARD:
                return insertCard(uri, values);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertCard(Uri uri, ContentValues values) {

        // Get writeable database
        SQLiteDatabase database = mCardSQLite.getWritableDatabase();


        long id = database.insert(Contract.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
            @Nullable String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mCardSQLite.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CARD:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(Contract.TABLE_NAME, selection, selectionArgs);
                break;
            case CARD_WITH_ID:
                // Delete a single row given by the ID in the URI
                selection = Contract.Columns._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(Contract.TABLE_NAME, selection, selectionArgs);
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


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CARD:
                return updateCard(uri, contentValues, selection, selectionArgs);
            case CARD_WITH_ID:
                selection = Contract.Columns._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateCard(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    public int updateCard(Uri uri, ContentValues values, String selection,
                          String[] selectionArgs) {

        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mCardSQLite.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(Contract.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Database contract
     */
    public static class Contract {
        public static final String TABLE_NAME = "cards";
        public static final String CONTENT_AUTHORITY = "com.google.developer.colorvalue";
        public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
                .authority(CONTENT_AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        public static final class Columns implements BaseColumns {
            public static final String COLOR_HEX = "hex";
            public static final String COLOR_NAME = "name";
        }
    }

}
