package com.garbagespots.garbagespotsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PictureActivity extends AppCompatActivity {

    private FirebaseFirestore myStore;
    private String spotTitle = "";
    private String reporterID = "";
    private Button picAdder;
    private ImageView imageView;
    private Map<String, Object> picData;
    private Map<String, Object> picData2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PictureActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            },1);

        }
        Intent i = getIntent();
        spotTitle = i.getStringExtra("spot");
        reporterID = i.getStringExtra("reporter");
        picAdder = findViewById(R.id.button15);
        imageView = findViewById(R.id.imageView);
        myStore = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = myStore.collection("images").document(spotTitle);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String encodedString = document.getString("picture");
                        byte [] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
                        Bitmap bitImage = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                        imageView.setImageBitmap(bitImage);
                    }
                }
            }
        });
        picAdder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(PictureActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        imageView.setImageBitmap(selectedImage);
                        picData = new HashMap<>();
                        picData.put("poster", reporterID);
                        picData.put("date", new Timestamp(new Date()));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                        byte[] data1 = baos.toByteArray();
                        String imageString = Base64.encodeToString(data1, Base64.DEFAULT);
                        picData.put("picture", imageString);
                        myStore.collection("images").document(spotTitle).set(picData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(PictureActivity.this, "Added a picture.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                    break;
               case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                try {
                                    Bitmap selectedRollImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                                    picData2 = new HashMap<>();
                                    picData2.put("poster", reporterID);
                                    picData2.put("date", new Timestamp(new Date()));
                                    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                                    selectedRollImage.compress(Bitmap.CompressFormat.JPEG, 20, baos2);
                                    byte[] data2 = baos2.toByteArray();
                                    String imageString2 = Base64.encodeToString(data2, Base64.DEFAULT);
                                    picData2.put("picture", imageString2);
                                    myStore.collection("images").document(spotTitle).set(picData2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(PictureActivity.this, "Added a picture.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                    cursor.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                    break;
            }
        }
    }

    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void returnToMap(View v) {
        Intent i = new Intent(PictureActivity.this, InfoWindowActivity.class);
        i.putExtra("title", spotTitle);
        startActivity(i);
    }
}