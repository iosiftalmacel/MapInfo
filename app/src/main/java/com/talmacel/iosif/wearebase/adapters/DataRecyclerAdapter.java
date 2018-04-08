package com.talmacel.iosif.wearebase.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.talmacel.iosif.wearebase.R;
import com.talmacel.iosif.wearebase.activities.InfoActivity;
import com.talmacel.iosif.wearebase.models.BaseData;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Iosif on 17/03/2018.
 */

public class DataRecyclerAdapter extends RecyclerView.Adapter<DataRecyclerAdapter.ItemViewHolder> {
    private List<BaseData> mItems;
    private Drawable[] drawables;
    private Context mContext;

    public DataRecyclerAdapter(Context context, List<BaseData> items) {
        mItems = items;
        mContext = context;
        drawables = new Drawable[]{
                mContext.getResources().getDrawable(R.drawable.circle1),
                mContext.getResources().getDrawable(R.drawable.circle2),
                mContext.getResources().getDrawable(R.drawable.circle3),
                mContext.getResources().getDrawable(R.drawable.circle4),
                mContext.getResources().getDrawable(R.drawable.circle5),
        };
    }

    public void setItems(List<BaseData> items) {
        mItems = items;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.data_item, parent, false);

        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        BaseData currentData = mItems.get(position);
        holder.name.setText(currentData.name);
        holder.location.setText(currentData.loc.city.concat(", ").concat(currentData.loc.country));
        holder.numberImage.setImageDrawable(drawables[Math.max((int)currentData.rating - 1, 0)]);
        holder.numberText.setText(String.valueOf(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView location;
        ImageView numberImage;
        TextView numberText;
        Button button;
        ItemViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            location = v.findViewById(R.id.location);
            numberImage = v.findViewById(R.id.image_number);
            numberText = v.findViewById(R.id.text_number);
            button = v.findViewById(R.id.click_button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, InfoActivity.class);
                    intent.putExtra("info", mItems.get(getAdapterPosition()));
                    mContext.startActivity(intent);
                }
            });
        }
    }
}