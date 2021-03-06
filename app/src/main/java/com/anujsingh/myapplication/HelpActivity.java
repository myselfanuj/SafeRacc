package com.anujsingh.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anujsingh.myapplication.Model.UserModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.SEND_SMS;

public class HelpActivity extends AppCompatActivity {

    private static final String TAG = "ReportMenuActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    public UserModel userModel;
    Button send;
    TextView textView;
    public FirebaseDatabase database;
    CardView emergency,sendSMS;
    private FusedLocationProviderClient client;
    private LocationManager locationManager;
    private com.google.android.gms.location.LocationListener listener;
    PendingIntent sentPI;
    String SENT = "SMS_SENT";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    SmsManager sms;

    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        myDb = new DatabaseHelper(this);
//        mAuth = FirebaseAuth.getInstance();
//        user = FirebaseAuth.getInstance().getCurrentUser();
        if(checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }
        textView = findViewById(R.id.location);
//        database = FirebaseDatabase.getInstance();
        client = LocationServices.getFusedLocationProviderClient(this);
        sms = SmsManager.getDefault();
        sentPI = PendingIntent.getBroadcast(this, 0,new Intent(SENT), 0);
        cardView();
    }

    public boolean isServicesOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HelpActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Toast.makeText(this, "isServicesOK: Google Play Services is working", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Toast.makeText(this, "isServicesOK: an error occured but we can fix it", Toast.LENGTH_SHORT).show();
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HelpActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void cardView()
    {
        emergency=(CardView)findViewById(R.id.emergencyContacts);
        sendSMS=(CardView)findViewById(R.id.sendSMS);
//        on click
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emergencyContact();
            }
        });
        sendSMS.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (ActivityCompat.checkSelfPermission(HelpActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HelpActivity.this, SEND_SMS) != PackageManager.PERMISSION_GRANTED ) {

                    return false;
                }
                client.getLastLocation().addOnSuccessListener(HelpActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location location) {

                        if(location!= null){

                            final float lat= (float) location.getLatitude();
                            final float lang= (float) location.getLongitude();
                            String messageToSend = "Help Me!!"+"\n https://maps.google.com/?q="+lat+","+lang;
                            String number = "01739216256";

                            Cursor res = myDb.getAllData();
                            if(res.getCount() == 0) {
                                // show message
                                Toast.makeText(HelpActivity.this, "No Number Added", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            while (res.moveToNext()) {
                                Toast.makeText(HelpActivity.this, "SMS send : "+res.getString(1) , Toast.LENGTH_SHORT).show();
                                sms.sendTextMessage(res.getString(1), null, messageToSend, sentPI,null);
                            }

                        }

                    }
                });

                return false;
            }
        });

    }

    void signOut()
    {
      //  FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(HelpActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
        return;
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
        if (ContextCompat.checkSelfPermission(this,
                SEND_SMS)
                != PackageManager.PERMISSION_GRANTED)
        {

// Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    SEND_SMS))
            {

                Toast.makeText(getBaseContext(),
                        "App requires permission to send SMS",
                        Toast.LENGTH_SHORT).show();

            }

            else
            {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{SEND_SMS},
                        1);

            }
        }    }
    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                SEND_SMS);
        int locationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void emergencyContact()
    {
        Intent intentContacts = new Intent(HelpActivity.this, ContactsActvity.class);
        startActivity(intentContacts);
    }
}