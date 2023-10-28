package com.base.socialmedaapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private EditText postEditext;
    private ImageView PostImageView;
    private Button btnPost;

    private FirebaseAuth auth;
    private  Bitmap bitmap;
    private  String imageIdentifier;
    private ArrayList<String> userName;
    private ArrayList<String> uids;
    private ArrayAdapter adapter;


    private ArrayList<DataSnapshot> posts;
    private String imageDownloadLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        auth = FirebaseAuth.getInstance();
        listView = findViewById(R.id.listView);

        listView.setOnItemClickListener(this);
        PostImageView = findViewById(R.id.PostImageView);
        postEditext = findViewById(R.id.postEditext);
        userName = new ArrayList<>();
        uids = new ArrayList<>();
        btnPost = findViewById(R.id.btnPost);

        adapter= new ArrayAdapter(this,android.R.layout.simple_list_item_1, userName);

        listView.setAdapter(adapter);




                btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                uploadImageToServer();


            }
        });

        PostImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                selectImage();

            }
        });
    }


    private void selectImage() {
        if (Build.VERSION.SDK_INT < 23) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1000);
        } if (Build.VERSION.SDK_INT >= 23)
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=  PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);

            } else {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1000);
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            selectImage();

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {
            Uri chosenImageData = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenImageData);
                PostImageView.setImageBitmap(bitmap);


            } catch (Exception e) {

                e.printStackTrace();
            }

        }

    }

    private void uploadImageToServer() {
        if (bitmap != null) {
            PostImageView.setDrawingCacheEnabled(true);
            PostImageView.buildDrawingCache();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            if (data.length > 0) {
                imageIdentifier = UUID.randomUUID() + ".png";

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child("myImages").child(imageIdentifier);

                UploadTask uploadTask = storageRef.putBytes(data);
                uploadTask.addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                    Toast.makeText(MainActivity.this, exception.toString(), Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(taskSnapshot -> {
                    // Task completed successfully
                    Toast.makeText(MainActivity.this, "Upload was successful", Toast.LENGTH_SHORT).show();

                    postEditext.setVisibility(View.VISIBLE);


                    FirebaseDatabase.getInstance().getReference().child("Twitter User").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            uids.add(snapshot.getKey());
                            String username = (String)snapshot.child("profileName").getValue();
                            userName.add(username);
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
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                imageDownloadLink = task.getResult().toString();
                            }
                        }
                    });
                });


            } else {
                // Handle the case when data is empty
                Toast.makeText(MainActivity.this, "Bitmap data is empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case when bitmap is null
            Toast.makeText(MainActivity.this, "Bitmap is null", Toast.LENGTH_SHORT).show();
        }

    }



    private void logout(){
        auth.signOut();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.postimage) {
            logout();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            Toast.makeText(MainActivity.this, "Successfully Logout", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        HashMap<String,String> dataMap = new HashMap<>();

        dataMap.put("fromWhom", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        dataMap.put("imageIdentifier",imageIdentifier);
        dataMap.put("imageLink", imageDownloadLink);
        dataMap.put("des", postEditext.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("Twitter User").child(uids.get(i)).child("recieved_posts").push().setValue(dataMap);

    }
}