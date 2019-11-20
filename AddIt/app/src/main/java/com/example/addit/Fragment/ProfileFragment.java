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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.addit.Activity.MainActivity;
import com.example.addit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.example.addit.Validation.isNotEmpty;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG="ProfileFragment";
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    private Uri image_uri;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private StorageReference Folder;
    private TextView user_name, user_email;
    private CircularImageView avatar;
    private FloatingActionButton edit_profile_btn;
    private Button logout_btn;
    private String cameraPermissions[] = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String storagePermissions[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private ProgressDialog dialog;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Folder=getInstance().getReference("Users_Profile_Img");

        user_name = view.findViewById(R.id.user_name);
        user_email = view.findViewById(R.id.user_email);
        avatar = view.findViewById(R.id.avatar);
        edit_profile_btn = view.findViewById(R.id.edit);
        logout_btn = view.findViewById(R.id.logout_btn);

        dialog=new ProgressDialog(getActivity());



        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });


        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String options[] = {"Edit Profile Picture", "Edit Name"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Edit Profile");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //edit picture
                                ImageDialog();
                                break;
                            case 1:
                                //edit name
                                updateNameDialog();
                                break;
                        }
                    }


                });
                builder.create().show();
            }

        });


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    //get data
                    String name = ds.child("name").getValue().toString();
                    String email = ds.child("email").getValue().toString();
                    String image = ds.child("image").getValue().toString();

                    //set data
                    user_name.setText(name);
                    user_email.setText(email);

                    try {
                        //if image received
                        Picasso.get().load(image).into(avatar);
                    } catch (Exception e) {
                        //if have exception load the default image
                        Log.d(TAG, "onDataChange: failed to load profile image");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: onDataChange",databaseError.toException() );

            }
        });
    }

    public void ImageDialog() {
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
        String filePath="image"+user.getUid();
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
                    databaseReference.child(user.getUid()).updateChildren(results)
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
    private void updateNameDialog(){
        dialog.setMessage("Updating User Name...");

        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle("Update Name");

        LinearLayout linearLayout=new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);

        final EditText name=new EditText(getActivity());
        name.setHint("Enter name");
        linearLayout.addView(name);

        builder.setView(linearLayout);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dial, int which) {
                dialog.show();
                String updateName=name.getText().toString().trim();
                if(isNotEmpty(updateName,name)){
                    databaseReference.child(user.getUid()).child("name").setValue(updateName)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();

                                }
                            });
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dia, int which) {
                dia.dismiss();
                dialog.dismiss();

            }
        });

        builder.create().show();
   }

}


