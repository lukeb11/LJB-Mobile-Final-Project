package com.example.ljbfinalproject;

import android.content.ClipData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemHolder> {

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImageDescs = new ArrayList<>();
    private ArrayList<Integer> mImages = new ArrayList<>();
    private ArrayList<Double> mPrices = new ArrayList<>();
    private ArrayList<String> mQuantity = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;
    private StringBuilder mOrder = new StringBuilder();
    private Double totalPrice = 0.0;
    private static final String TAG = "Activity";


    public RecyclerViewAdapter(Context context, ArrayList<String> imageNames,
                               ArrayList<Integer> images, ArrayList<String> imageDescs,
                               ArrayList<Double> prices, ArrayList<String> quantity){
        mImageNames = imageNames;
        mImageDescs = imageDescs;
        mImages = images;
        mPrices = prices;
        mQuantity = quantity;
        mContext = context;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.storefront_item,
                viewGroup, false);
        ItemHolder holder = new ItemHolder(view, new EditTextListener());
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, final int i) {
        itemHolder.itemImage.setImageResource(mImages.get(i));
        itemHolder.itemText.setText(mImageDescs.get(i));
        itemHolder.itemPrice.setText("$" + mPrices.get(i).toString());
        itemHolder.mEditText.newPosition(itemHolder.getAdapterPosition());
        itemHolder.quantityField.setText(mQuantity.get(itemHolder.getAdapterPosition()));
        itemHolder.mLayout.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(mContext, mImageNames.get(i), Toast.LENGTH_SHORT).show();
                //mOrder.append("\nYou have ordered " + mImageNames.get(i));
                //totalPrice += mPrices.get(i);
            }
        });
        itemHolder.mLayout.setOnLongClickListener(new LongClickListener());
    }

    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder{
        public final ImageView itemImage;
        public final TextView itemText;
        public final TextView itemPrice;
        public final EditText quantityField;
        public EditTextListener mEditText;

        final LinearLayout mLayout;

        public ItemHolder(@NonNull View itemView, EditTextListener mEditTextListener) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.cafe_image);
            itemText = itemView.findViewById(R.id.cafe_description);
            itemPrice = itemView.findViewById(R.id.cafe_price);
            quantityField = itemView.findViewById(R.id.quantity_box);
            mLayout = itemView.findViewById(R.id.cafe_layout);
            mEditText = mEditTextListener;
            quantityField.addTextChangedListener(mEditText);
        }

    }

    class EditTextListener implements TextWatcher{

        private int position;

        public void newPosition(int position){
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mQuantity.set(position, s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public String getOrder(){
        DecimalFormat priceFormat = new DecimalFormat("0.00");
        mOrder.setLength(0);
        for(int i = 0; i < mQuantity.size(); i++){
            if(mQuantity.get(i).compareTo("") == 0)
                mQuantity.set(i, "0");
            mOrder.append
                    ("\nYou have ordered " + mQuantity.get(i) + " " + mImageNames.get(i) + " - $" +
                            priceFormat.format(mPrices.get(i) * Integer.parseInt(mQuantity.get(i))));
        }
        return mOrder.toString();
    }

    public Double getTotalPrice(){
        totalPrice = 0.0;
        for(int i = 0; i < mQuantity.size(); i++){
            if(mQuantity.get(i).compareTo("") == 0)
                mQuantity.set(i, "0");
            totalPrice += mPrices.get(i) * Integer.parseInt(mQuantity.get(i));
        }
        return totalPrice;
    }

    private final class LongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick (View v) {
            Log.d(TAG, "onLongClick: Clicking");
            final ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder, v, 0);
            v.setVisibility(View.INVISIBLE);
            return true;
        }
    }

}
