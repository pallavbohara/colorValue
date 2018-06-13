package com.google.developer.colorvalue.data;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.google.developer.colorvalue.R;
import com.google.developer.colorvalue.listners.MyOnClickListener;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private Cursor mCursor;


    private final View.OnClickListener mOnClickListener = new MyOnClickListener();



    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        view.setOnClickListener(mOnClickListener);
        return new ViewHolder(view);
//        return new ViewHolder(LayoutInflater.from(mContext)
//                .inflate(R.layout.list_item, parent, false));
    }

    @Override
    @TargetApi(Build.VERSION_CODES.N)
    public void onBindViewHolder(CardAdapter.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        mCursor.moveToPosition(position);
        holder.setData(mCursor);
    }

    @Override
    public int getItemCount()
    {
        if(mCursor==null)
            return  0;
        return mCursor.getCount();
    }

    /**
     * Return a {@link Card} represented by this item in the adapter.
     * Method is used to run machine tests.
     *
     * @param position Cursor item position
     * @return A new {@link Card}
     */
    public Card getItem(int position) {
        if (mCursor.moveToPosition(position)) {
            return new Card(mCursor);
        }
        return null;
    }

    /**
     * @param data update cursor
     */
    public void swapCursor(Cursor data) {
        mCursor = data;
        notifyDataSetChanged();
    }
    @Override
    public long getItemId(int position)
    {
        mCursor.moveToPosition(position);
        return mCursor.getInt(mCursor.getColumnIndex(CardProvider.Contract.Columns._ID));
    }
    /**
     * An Recycler item view
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.color_name);
            cardView = (CardView) itemView.findViewById(R.id.card);
        }
        public void setData(Cursor c) {
            name.setText(c.getString(c.getColumnIndex(CardProvider.Contract.Columns.COLOR_NAME)));
            String color = c.getString(c.getColumnIndex(CardProvider.Contract.Columns.COLOR_HEX));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if(Color.luminance(Color.parseColor(color))<0.5)
                {
                    name.setTextColor(Color.WHITE);
                }
                else{
                    name.setTextColor(Color.BLACK);
                }
            }
            cardView.setCardBackgroundColor(Color.parseColor(color));
        }
    }
}
