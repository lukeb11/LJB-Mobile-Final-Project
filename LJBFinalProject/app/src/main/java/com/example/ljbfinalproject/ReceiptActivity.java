package com.example.ljbfinalproject;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class ReceiptActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOGUE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        Intent intent = getIntent();
        String message = "Order: " +
                intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Double totalPrice = intent.getDoubleExtra("sumPrice", 0);
        DecimalFormat priceFormat = new DecimalFormat("#.00");
        TextView textView = findViewById(R.id.order_textview);
        TextView priceText = findViewById(R.id.order_price);
        textView.setText(message);
        priceText.setText("Total: $" + priceFormat.format(totalPrice).toString());

        if(checkServices())
            init();

    }

    public void onRadioButtonClicked(View view){
        boolean clicked = ((RadioButton) view).isChecked();
        switch (view.getId()){
            case R.id.sameday:
                if(clicked)
                    displayToast(getString(R.string.same_day));
                break;
            case R.id.nextday:
                if(clicked)
                    displayToast(getString(R.string.next_day));
                break;
            case R.id.pickup:
                if(clicked)
                    displayToast(getString(R.string.pick_up));
                break;
            default:
                break;
        }
    }

    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_SHORT).show();
    }

    private void init(){
        Log.d(TAG, "Services functional.  Initializing.");
        Button btnMap = findViewById(R.id.MapButton);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReceiptActivity.this, MapActivity.class);
                //startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });
    }

    public boolean checkServices()
    {
        Log.d(TAG, "checkServices: Checking services");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ReceiptActivity.this);
        if(available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "checkServices: Connection is SUCCESSFUL");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "checkServices: Connection NOT SUCCESSFUL.  Error solvable.");
            Dialog dialog = GoogleApiAvailability.getInstance().
                    getErrorDialog(ReceiptActivity.this, available, ERROR_DIALOGUE_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(ReceiptActivity.this, "Map Unavailable", Toast.LENGTH_SHORT);
        }
        return false;

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
                String result = data.getStringExtra("address");
                EditText resultField = findViewById(R.id.address_text);
                resultField.setText(result);
        }
    }

}

