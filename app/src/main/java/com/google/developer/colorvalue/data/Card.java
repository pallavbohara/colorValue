package com.google.developer.colorvalue.data;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;

/**
 * Hold card item values
 */
public class Card implements Parcelable {

    private int mID;
    public String mColor;
    public String mName;

    public Card(Cursor data) {
        mID = CardSQLite.getColumnInt(data, CardProvider.Contract.Columns._ID);
        mColor = CardSQLite.getColumnString(data, CardProvider.Contract.Columns.COLOR_HEX);
        mName = CardSQLite.getColumnString(data, CardProvider.Contract.Columns.COLOR_NAME);
    }

    public String getName() {
        return mName;
    }

    public String getHex() {
        return mColor;
    }

    @ColorInt
    public int getColorInt() {
        return Color.parseColor(mColor);
    }

    public int getID() {
        return mID;
    }

    public Uri getUri() {
        return ContentUris.withAppendedId(CardProvider.Contract.CONTENT_URI, mID);
    }

    protected Card(Parcel in) {
        mID = in.readInt();
        mColor = in.readString();
        mName = in.readString();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mID);
        dest.writeString(mColor);
        dest.writeString(mName);
    }
}
