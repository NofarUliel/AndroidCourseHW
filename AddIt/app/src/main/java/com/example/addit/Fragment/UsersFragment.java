package com.example.addit.Fragment;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.addit.Adapters.UsersAdapter;
import com.example.addit.Model.Invitation;
import com.example.addit.Model.User;
import com.example.addit.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {
    private final String TAG="UsersFragment";
    private String listId,managerID;
    private View view;
    private RecyclerView recyclerView;
    private DatabaseReference DB;
    private FloatingActionButton search_btn;
    private UsersAdapter usersAdapter;
    private TextView msg;
    private List<String> membersList;
    private List<Invitation> InvitationList;
    private String userId;
    private ProgressDialog dialog;

    public UsersFragment() {
        // Required empty public constructor
    }
    public UsersFragment(String listId,String managerID,String userId) {
        this.listId=listId;
        this.managerID=managerID;
        this.userId=userId;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_users, container, false);
        dialog = new ProgressDialog(getContext());
        DB = FirebaseDatabase.getInstance().getReference();

        msg=view.findViewById(R.id.msg);
        recyclerView = view.findViewById(R.id.user_recyclerView);
        search_btn=view.findViewById(R.id.search_btn);

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        usersAdapter = new UsersAdapter();
        recyclerView.setAdapter(usersAdapter);


        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager= getActivity().getSupportFragmentManager();
                Fragment myFragment = new SearchUserFragment(listId,membersList,InvitationList,managerID);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();
            }
        });

        return view;
    }


    private void setUserInvitations(){
         DB.child("Invitation").orderByChild("listID").equalTo(listId)
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                InvitationList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Invitation i= ds.getValue(Invitation.class);
                    if(i.getSenderID().equals(userId)&&i.isAccepted()==false){
                        InvitationList.add(i);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ",databaseError.toException() );

            }
        });
    }




    private void getMembersList(){

       DB.child("Shopping List").child(listId).child("members")
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                membersList.clear();
                final List<User> member_users=new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                     final String userID = ds.getValue(String.class);
                    membersList.add(userID);
                    DB.child("Users").
                            addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                       User user = ds.getValue(User.class);
                                        if(user.getId().equals(userID)) {
                                            member_users.add(user);
                                            usersAdapter = new UsersAdapter(member_users,listId,membersList,InvitationList,managerID,getContext());
                                            recyclerView.setAdapter(usersAdapter);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e(TAG, "onCancelled: getMembersList", databaseError.toException());

                                }
                            });
                }
                dialog.dismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ",databaseError.toException() );
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        dialog.setMessage("Loading ...");
        dialog.show();
        membersList=new ArrayList<>();
        InvitationList=new ArrayList<>();
        setUserInvitations();
        getMembersList();

    }
}