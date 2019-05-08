package com.example.ljbfinalproject;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> mItemNames = new ArrayList<>();
    private ArrayList<String> mItemDesc = new ArrayList<>();
    private ArrayList<Integer> mImages = new ArrayList<>();
    private ArrayList<Double> mPrices = new ArrayList<>();
    private ArrayList<String> mQuantity = new ArrayList<>();
    public static final String EXTRA_MESSAGE = "com.example.android.droidcafe.extra.MESSAGE";
    private static final String TAG = "Activity";
    private RecyclerViewAdapter adapter;
    private String order;
    private double price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        final RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new RecyclerViewAdapter(this, mItemNames,
                mImages, mItemDesc, mPrices, mQuantity);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fab.setOnDragListener(new DragListener());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
                //intent.putExtra(EXTRA_MESSAGE, adapter.getOrder());
                //intent.putExtra("sumPrice", adapter.getTotalPrice());
                intent.putExtra(EXTRA_MESSAGE, order);
                intent.putExtra("sumPrice", price);
                startActivity(intent);
            }
        });

        addItems();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addItems(){
        mItemNames.add("Ice Cream");
        mItemDesc.add("Ice cream sandwiches have chocolate wafers and vanilla filling.");
        mImages.add(R.drawable.ic_launcher_background);
        mPrices.add(0.99);
        mQuantity.add("");

        mItemNames.add("FroYos");
        mItemDesc.add("FroYo is premium self-serve frozen yogurt.");
        mImages.add(R.drawable.ic_launcher_background);
        mPrices.add(1.75);
        mQuantity.add("");

        mItemNames.add("Oreos");
        mItemDesc.add("Oreos are chocolate cookies with vanilla icing.");
        mImages.add(R.drawable.ic_launcher_background);
        mPrices.add(0.25);
        mQuantity.add("");

        mItemNames.add("Donuts");
        mItemDesc.add("Donuts are glazed and sprinkled with candy.");
        mImages.add(R.drawable.ic_launcher_background);
        mPrices.add(0.99);
        mQuantity.add("");

        mItemNames.add("KitKats");
        mItemDesc.add("KitKats are wafer sticks coated with chocolate.");
        mImages.add(R.drawable.ic_launcher_background);
        mPrices.add(0.75);
        mQuantity.add("");

        //initRecycler();
    }

    /*public void initRecycler(){
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mItemNames, mImages);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }*/

    public final class DragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            //Log.d(TAG, "onDrag: Dragging");
            final View view = (View) event.getLocalState();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_LOCATION: {
                    if (view == v)
                        return true;
                    Log.d(TAG, "thing");
                    //int index = calcIndex(event.getX(), event.getY());
                    //ItemHolder.removeView(view);
                    //gridLayout.addView(view, index);
                    break;
                }

                case DragEvent.ACTION_DROP: {
                    Log.d(TAG, "onDrag: Dropping");
                    //match();
                        order = adapter.getOrder();
                        price = adapter.getTotalPrice();
                    view.setVisibility(view.VISIBLE);
                    break;
                }
                case DragEvent.ACTION_DRAG_ENDED: {
                    Log.d(TAG, "onDrag: Drag Ending");

                    if (!event.getResult())
                        view.setVisibility(View.VISIBLE);
                    break;
                }
            }
            return true;
        }
    }

}