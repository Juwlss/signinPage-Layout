
package com.example.barter10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.barter10.Adapter.UploadListAdapter;
import com.example.barter10.Model.DetailHelperClass;
import com.example.barter10.Model.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class activityUpload extends AppCompatActivity{

    private static final int REQUEST_CODE_IMAGE = 101;
    private UploadListAdapter uploadListAdapter;
    RecyclerView recyclerView;
    ImageView imageView;
    Uri uploadImgdef;
    Button btnUpload;
    Button calendar;
    ProgressDialog progressDialog;
    Uri imageuri;
    ArrayList<String> urlStrings;
    ArrayList<Uri> itemList = new ArrayList<>();
    private int upload_count = 0;
    private String timer;
    private EditText uploadName, uploadLocation, uploadDetails, uploadCondition, uploadValue, uploadPreference;
    private DatabaseReference reference;
    private FirebaseDatabase rootNode;
    private String category1;
    private String category2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //new Image
        imageView = findViewById(R.id.image);
        recyclerView = findViewById(R.id.upload_list);
        uploadListAdapter = new UploadListAdapter(itemList);
        recyclerView.setLayoutManager(new GridLayoutManager(activityUpload.this, 2));
        recyclerView.setAdapter(uploadListAdapter);

        if(ContextCompat.checkSelfPermission(activityUpload.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activityUpload.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_IMAGE);

        }

        Button btnImg = findViewById(R.id.btn_img);
        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        //uploading phase
        btnUpload = findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });

        //calendar
        calendar = findViewById(R.id.btnCalendar);
        MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()))
                .build();

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "Tag_picker");
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        calendar.setText(materialDatePicker.getHeaderText());
                        timer = materialDatePicker.getHeaderText();
                        Toast.makeText(activityUpload.this, materialDatePicker.getHeaderText(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        //end calendar


        //spinner list of categories

        Spinner list1 = findViewById(R.id.listCategories1);
        Spinner list2 = findViewById(R.id.listCategories2);
        List<String> categories = new ArrayList<>();
        categories.add(0, "Select");
        categories.add("Technology");
        categories.add("Fashion");
        categories.add("Sports");

        //styling
        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categories);
        //
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //
        list1.setAdapter(dataAdapter);

        list1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Select")) {
                    //do nothing
                } else {
                    category1 = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method
            }
        });
        //styling
        ArrayAdapter<String> dataAdapter2;
        dataAdapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categories);
        //
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //
        list2.setAdapter(dataAdapter2);

        list2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Select")) {
                    //do nothing
                } else {
                    category2 = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method
            }
        });

        //end of spinner list categories

        //getting details in edit text
        uploadName = findViewById(R.id.txtItemName);
        uploadLocation = findViewById(R.id.txtLocation);
        uploadDetails = findViewById(R.id.txtDetails);
        uploadCondition = findViewById(R.id.txtCondition);
        uploadValue = findViewById(R.id.txtEstimatedValue);
        uploadPreference = findViewById(R.id.txtPreference);

        //setting upload button in default
        uploadImgdef = Uri.parse("android.resource://com.example.uploadpage/drawable/upload_button");

    }

    private void uploadImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK && null != data){

            if (data.getClipData() != null){
                int x = data.getClipData().getItemCount();
                for (int i=0; i<x; i++){
                    imageuri = data.getClipData().getItemAt(i).getUri();
                    itemList.add(imageuri);
                    uploadListAdapter.notifyDataSetChanged();
                }

            }else{
                imageuri = data.getData();
                itemList.add(imageuri);
                uploadListAdapter.notifyDataSetChanged();
                Toast.makeText(activityUpload.this, "Single", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(activityUpload.this, "Please pick image", Toast.LENGTH_SHORT).show();
        }



    }

    private void upload() {
        //image in firebase storage
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Post...");

        // generating key
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("PostItem");

        urlStrings = new ArrayList<>();
        String itemKey = reference.push().getKey();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("PostItem").child(itemKey);
        //getting values in edit text
        String prod_id =itemKey;
        String itemName = uploadName.getText().toString().trim();
        String itemLocation = uploadLocation.getText().toString().trim();
        String itemDetails = uploadDetails.getText().toString().trim();
        String itemCondition = uploadCondition.getText().toString().trim();
        String itemValue = uploadValue.getText().toString().trim();
        String itemPreference = uploadPreference.getText().toString().trim();
        String timeLimit = timer;
        String itemCategory = category1+category2;


        if (itemList.isEmpty() || timer == null || TextUtils.isEmpty(itemName) || TextUtils.isEmpty(itemDetails) || TextUtils.isEmpty(itemCondition) || TextUtils.isEmpty(itemValue) || TextUtils.isEmpty(itemPreference) || TextUtils.isEmpty(itemCategory)){
            Toast.makeText(activityUpload.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
        } else {

            for (upload_count=0; upload_count<itemList.size(); upload_count++){
                Uri IndividualImage = itemList.get(upload_count);
                StorageReference ImageName = storageReference.child(IndividualImage.getLastPathSegment());

                ImageName.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {
                               urlStrings.add(String.valueOf(uri));
                               if (urlStrings.size() == itemList.size()){
                                   storeLink(urlStrings);

                                   DetailHelperClass detailClass = new DetailHelperClass(prod_id,  itemName, itemDetails, itemCondition, itemValue, itemPreference, timeLimit, itemCategory);


                                   Upload upload = new Upload(uri.toString(),itemLocation, itemName, itemCondition);

                                   reference.child(prod_id).setValue(upload);//setting primary key

                                   Toast.makeText(activityUpload.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                   startActivity(new Intent(activityUpload.this, Home.class));
                               }
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(activityUpload.this,"Failed to Upload",Toast.LENGTH_SHORT).show();
                           }
                       });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(activityUpload.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }

    }

    private void storeLink(ArrayList<String> urlStrings) {
        HashMap<String, String> hashMap = new HashMap<>();

        for (int i = 0; i <urlStrings.size() ; i++) {
            hashMap.put("ImgLink"+i, urlStrings.get(i));

        }
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
//        databaseReference.push().setValue(hashMap)
//                .addOnCompleteListener(
//                        new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(activityUpload.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }
//                ).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(activityUpload.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
        progressDialog.dismiss();
        itemList.clear();
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.upload_image1:
//                ImagePicker.Companion.with(activityUpload.this)
//                        .crop()                    //Crop image(Optional), Check Customization for more option
//                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
//                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
//                        .start(1);
//
//                break;
//            case R.id.upload_image2:
//                ImagePicker.Companion.with(activityUpload.this)
//                        .crop()
//                        .compress(1024)
//                        .maxResultSize(1080, 1080)
//                        .start(2);
//                break;
//            case R.id.upload_image3:
//                ImagePicker.Companion.with(activityUpload.this)
//                        .crop()
//                        .compress(1024)
//                        .maxResultSize(1080, 1080)
//                        .start(3);
//                break;
//            case R.id.upload_image4:
//                ImagePicker.Companion.with(activityUpload.this)
//                        .crop()
//                        .compress(1024)
//                        .maxResultSize(1080, 1080)
//                        .start(4);
//                break;
//        }
//    }
}