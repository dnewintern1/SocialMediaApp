package com.base.socialmedaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.sql.Array;
import java.util.ArrayList;

public class ViewPostActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private ListView PostlistView;
    private ArrayList<String> userNames;

    private ArrayAdapter adapter;

    private FirebaseAuth mFirebaseAuth;
    private ImageView sentPostImageView;
    private TextView sentPostTextview;
    private ArrayList<DataSnapshot> mDataSnapshots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        mFirebaseAuth =FirebaseAuth.getInstance();

        PostlistView = findViewById(R.id.PostlistView);
        userNames = new ArrayList<>();

     //   sentPostImageView = findViewById(R.id.imageView);
      //  sentPostTextview = findViewById(R.id.postTexxtView);
        adapter = new ArrayAdapter(this , android.R.layout.simple_list_item_1,userNames);
        PostlistView.setAdapter(adapter);

        mDataSnapshots = new ArrayList<>();


        PostlistView.setOnItemClickListener(this);
        PostlistView.setOnItemLongClickListener(this);


        FirebaseDatabase.getInstance().getReference().child("Twitter User").child(mFirebaseAuth.getCurrentUser().getUid()).child("recieved_posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                mDataSnapshots.add(snapshot);
                String  fromWhomUserName =(String) snapshot.child("fromWhom").getValue();
                userNames.add(fromWhomUserName);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        DataSnapshot mydataSnapshot = mDataSnapshots.get(i);

        String downLoadlink = (String)mydataSnapshot.child("imageLink").getValue();

        Picasso.get().load(downLoadlink).into(sentPostImageView);

        String desValue = mydataSnapshot.child("des").getValue(String.class);
        sentPostTextview.setText(desValue);

    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

        new AlertDialog.Builder(this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        return false;
    }
}