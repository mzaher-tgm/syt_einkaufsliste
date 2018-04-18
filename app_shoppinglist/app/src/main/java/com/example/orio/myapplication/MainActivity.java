package com.example.orio.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    Button button_add;
    Button button_update;
    Button button_delete;
    EditText eintrag;
    ListView view;

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<String> listKeys = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    private Boolean searchMode = false;
    private Boolean itemSelected = false;
    private int selectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        myRef = database.getReference("list");
        myRef.keepSynced(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_add = (Button) findViewById(R.id.add);
        button_update = (Button) findViewById(R.id.update);
        button_delete = (Button) findViewById(R.id.delete);
        eintrag = (EditText) findViewById(R.id.editText2);
        view = (ListView) findViewById(R.id.listview);


        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice,
                listItems);
        view.setAdapter(adapter);
        view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        view.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedPosition = position;
                        itemSelected = true;
                        button_delete.setEnabled(true);
                    }
                });

        ChildEventListener childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String d = dataSnapshot.child("description").getValue().toString();
                adapter.add(d);
                Toast.makeText(MainActivity.this, "list", Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "null-list", Toast.LENGTH_SHORT).show();
                listKeys.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(MainActivity.this, "child changed", Toast.LENGTH_SHORT).show();
                String value = dataSnapshot.child("description").getValue().toString();
                String key = dataSnapshot.getKey();
                int index = listKeys.indexOf(key);

                listItems.set(index, value);
                listKeys.set(index, value);

                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                int index = listKeys.indexOf(key);

                listItems.remove(index);
                listKeys.remove(index);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myRef.addChildEventListener(childListener);
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = myRef.push().getKey();
                String text = eintrag.getText().toString();

                myRef.child(key).child("description").setValue(text);
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Inserting Data", Toast.LENGTH_SHORT).show();
            }
        });
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setItemChecked(selectedPosition, false);
                String text = eintrag.getText().toString();
                myRef.child(listKeys.get(selectedPosition)).child("description").setValue(text);
            }
        });


        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setItemChecked(selectedPosition, false);
                myRef.child(listKeys.get(selectedPosition)).removeValue();
            }
        });

    }
}