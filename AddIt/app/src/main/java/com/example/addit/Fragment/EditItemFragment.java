package com.example.addit.Fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.addit.Model.Item;
import com.example.addit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.example.addit.Validation.isNotEmpty;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditItemFragment extends Fragment {

    private static final String TAG="EditItemFragment";
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    private Item item;
    private EditText amount_txt,price_txt,name_txt;
    private CircularImageView img;
    private DatabaseReference DB;
    private Button update_btn;
    private FirebaseUser user;
    private String cameraPermissions[] = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String storagePermissions[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private ProgressDialog dialog;
    private  StorageReference  Folder;
    private Uri image_uri;



    public EditItemFragment() {
        // Required empty public constructor
    }

    public EditItemFragment(Item item,DatabaseReference DB,FirebaseUser user) {
       this.item=item;
       this.DB=DB;
       this.user=user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_edit_item, container, false);
        amount_txt=view.findViewById(R.id.amount);
        price_txt=view.findViewById(R.id.price);
        name_txt=view.findViewById(R.id.itemName);
        img=view.findViewById(R.id.item_photo);
        update_btn=view.findViewById(R.id.update_item_btn);
        if(DB!=null&&item!=null) {
            DB.child("Item")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Item i = ds.getValue(Item.class);
                                if (i.getId().equals(item.getId())) {
                                    //set data
                                    name_txt.setText(i.getName());
                                    amount_txt.setText(i.getAmount() + "");
                                    price_txt.setText(i.getPrice() + "");
                                    try {
                                        //if image received
                                        Picasso.get().load(i.getImage()).into(img);
                                    } catch (Exception e) {
                                        //if have exception load the default image
                                        Log.d(TAG, "onDataChange: failed to load item image");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: ", databaseError.toException());

                        }
                    });
        }
        img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                      Folder= FirebaseStorage.getInstance().getReference("Items_Img");
                      ImageDialog();

                }
            });
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updateName=name_txt.getText().toString().trim();
                if(!isNotEmpty(updateName,name_txt)){
                    return;
                }
                String amount_str=amount_txt.getText().toString().trim();
                if(!isNotEmpty(amount_str,amount_txt)){
                    return;
                }
                String price_str=price_txt.getText().toString().trim();
                if(!isNotEmpty(price_str,price_txt)){
                    return;
                }
                int updateAmount=Integer.parseInt(amount_str);
                double updatePrice=Double.parseDouble(price_str);

                item.setAmount(updateAmount);
                item.setPrice(updatePrice);
                item.setName(updateName);

                DB.child("Item").child(item.getId()).setValue(item)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Updated item!!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        return view;
    }

    public void ImageDialog(){
        dialog=new ProgressDialog(getActivity());
        dialog.setMessage("Updating Profile Photo...");
        dialog.show();
        String options[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick image from");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        //take photo from camera
                        if(!checkCameraPermission()){
                            requestCameraPermission();
                        }
                        else{
                            takePhotoFromCamera();
                        }
                        break;
                    case 1:
                        //take photo from gallery
                        if(!checkStoragePermission()){
                            requestStoragePermission();
                        }
                        else{
                            takePhotoFromGallery();
                        }
                        break;
                }
            }
        });
        builder.create().show();
    }


    public boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    public void requestStoragePermission() {
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);

    }

    public boolean checkCameraPermission() {

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result1 && result2;
    }

    public void requestCameraPermission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        takePhotoFromCamera();
                    } else {
                        Toast.makeText(getActivity(), "Please enable camera & storage permission", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        takePhotoFromGallery();
                    } else {
                        Toast.makeText(getActivity(), "Please enable storage permission", Toast.LENGTH_LONG).show();
                    }
                }
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //this method will be called after picking image from camera or gallery
        if(resultCode==RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri=data.getData();
                uploadProfilePhoto(image_uri);
            }
            if(requestCode==IMAGE_PICK_CAMERA_CODE){
                uploadProfilePhoto(image_uri);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePhoto(Uri uri) {
        String filePath="image_"+item.getId();
        StorageReference storageRef=Folder.child(filePath);
        storageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                Uri downloadUri=uriTask.getResult();
                if(uriTask.isSuccessful()){
                    HashMap<String ,Object> results=new HashMap<>();
                    results.put("image",downloadUri.toString());
                    DB.child("Item").child(item.getId()).updateChildren(results)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity(), "Image Updated...", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //error adding url in database
                                    Toast.makeText(getActivity(), "Error Updating Image...", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();

                                }
                            });

                }
                else{
                    //error
                    Toast.makeText(getActivity(), "Some error occured", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();

            }
        });

    }

    private void takePhotoFromCamera() {
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Photo");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Photo Description");
        image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);
    }

    private void takePhotoFromGallery() {
        Intent galleryIntent=new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);

    }

}
