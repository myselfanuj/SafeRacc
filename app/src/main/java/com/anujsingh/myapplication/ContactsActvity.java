package com.anujsingh.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ContactsActvity extends AppCompatActivity {

    EditText number;
    Button add,remove;
    TextView textViewNumbers;
    DatabaseHelper myDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_actvity);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myDb = new DatabaseHelper(this);
        innitial();
        viewAll();
        DeleteData();
    }

    private void innitial() {
        number=(EditText)findViewById(R.id.editTextNumber);
        add=(Button) findViewById(R.id.buttonAdd);
        remove=(Button) findViewById(R.id.burronRemove);

        textViewNumbers=(TextView)findViewById(R.id.textViewNumbers);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor res = myDb.getAllData();
                if(res.getCount()<8)
                {
                    boolean isInserted = myDb.insertData(number.getText().toString());
                    if(isInserted == true)
                    {
                        Toast.makeText(ContactsActvity.this,"Data Inserted",Toast.LENGTH_LONG).show();
                        viewAll();
                    }

                    else
                        Toast.makeText(ContactsActvity.this,"Data not Inserted",Toast.LENGTH_LONG).show();

                }
                else
                    Toast.makeText(ContactsActvity.this,"Contacts are full, remove any number to add new",Toast.LENGTH_LONG).show();

            }
        });
    }
    public void viewAll() {

        Cursor res = myDb.getAllData();
        if(res.getCount() == 0) {
            // show message
            textViewNumbers.setText("No Data found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append(res.getString(0)+": ");
            buffer.append(res.getString(1)+"\n");
        }
        textViewNumbers.setText(buffer.toString());

        // Show all data

    }
    public void DeleteData() {
        remove.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer deletedRows = myDb.deleteData(number.getText().toString());
                        if(deletedRows > 0)
                        {
                            Toast.makeText(ContactsActvity.this,"Data Deleted",Toast.LENGTH_LONG).show();
                            viewAll();
                        }

                        else
                            Toast.makeText(ContactsActvity.this,"Data not Deleted",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
