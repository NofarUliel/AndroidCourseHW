package com.example.addit.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.addit.Adapters.UsersAdapter;
import com.example.addit.Model.Invitation;
import com.example.addit.Model.User;
import com.example.addit.R;
import com.google.firebase.auth.FirebaseAuth;
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
public class SearchUserFragment extends Fragment {

    private DatabaseReference DB ;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private String userId, listId,managerId;
    private TextView msg;
    private Button invitation_btn;
    private List<String> membersList=new ArrayList<>();
    private List<Invitation> InvitationList=new ArrayList<>();

    public SearchUserFragment() {
        // Required empty public constructor
    }
    public SearchUserFragment(String listId,List<String> membersList,List<Invitation> InvitationList,String managerId) {
       this.listId=listId;
       this.membersList=membersList;
       this.InvitationList=InvitationList;
       this.managerId=managerId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_search_user, container, false);
        DB = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        searchView=view.findViewById(R.id.searchView);
        msg=view.findViewById(R.id.search_msg);
        recyclerView = view.findViewById(R.id.search_recyclerView);
        invitation_btn=view.findViewById(R.id.invitation_btn);

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        usersAdapter = new UsersAdapter();
        recyclerView.setAdapter(usersAdapter);

        getAllUsers();
        invitation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello\n" +
                        "your friend shares with you a shopping list.\n" +
                        "\n" +
                        "It seems like you haven't signed up for AddIt yet. To be able to see the shopping list on your phone please install AddIt app.\n" +
                        "\n" +
                        "\n" +
                        "Have fun shopping with AddIt!");
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, "nofarafeka@gmail.com");


                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                invitation_btn.setVisibility(View.INVISIBLE);
                if (!TextUtils.isEmpty(query.trim())) {
                    //the user type email address
                    if(Patterns.EMAIL_ADDRESS.matcher(query).matches())
                        searchUsersByEmail(query);
                    else //the user type name of user
                        searchUsersByName(query);
                } else {
                    //search text is empty display all users
                    getAllUsers();
                }
                return false;
            }



            @Override
            public boolean onQueryTextChange(String newText) {
                invitation_btn.setVisibility(View.INVISIBLE);
                if (!TextUtils.isEmpty(newText.trim())) {
                    //the user type email address
                    if(Patterns.EMAIL_ADDRESS.matcher(newText).matches())
                        searchUsersByEmail(newText);
                    else //the user type name of user
                        searchUsersByName(newText);
                } else {
                    //search text is empty display all users
                    getAllUsers();
                }
                return false;
            }


        });

        // Inflate the layout for this fragment
        return view;
    }

    private void searchUsersByName(final String s) {
        DB.child("Users").orderByChild("name").limitToLast(100)

                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<User> usersList = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            User user = ds.getValue(User.class);
                            if (!user.getId().equals(userId)) {
                                if(user.getName().toLowerCase().contains(s.toLowerCase())) {
                                    usersList.add(user);
                                }

                            }
                            usersAdapter = new UsersAdapter(usersList,listId,membersList,InvitationList,managerId,getContext());
                            usersAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(usersAdapter);
                        }
                        if(usersList.isEmpty()){
                            msg.setText("Enter the email of the user you want to share the list with");

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("USER FRAGMENT", "onCancelled: "+ databaseError.getMessage());

                    }
                });
    }
    private void searchUsersByEmail(final String s) {
        DB.child("Users").orderByChild("name").limitToLast(100)

                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<User> usersList = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            User user = ds.getValue(User.class);
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            if (!user.getId().equals(userId)) {
                                if(user.getEmail().toLowerCase().contains(s.toLowerCase())) {
                                    usersList.add(user);
                                }

                            }
                            usersAdapter = new UsersAdapter(usersList,listId,membersList,InvitationList,managerId,getContext());
                            usersAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(usersAdapter);
                        }
                        if(usersList.isEmpty()){
                            msg.setText("There is no account with this email you can invite him to join the app");
                            invitation_btn.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("USER FRAGMENT", "onCancelled: "+ databaseError.getMessage());

                    }
                });
    }

    private void getAllUsers(){
        DB.child("Users").orderByChild("name").limitToLast(100)

                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<User> usersList = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            User user = ds.getValue(User.class);
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            Log.d("%%%%%", "onDataChange: userid="+userId+"user="+user.getId());
                            if (!user.getId().equals(userId)) {
                                usersList.add(user);
                            }
                            usersAdapter = new UsersAdapter(usersList,listId,membersList,InvitationList,managerId,getContext());
                            recyclerView.setAdapter(usersAdapter);
                        }
                        if(usersList.isEmpty()){
                            msg.setText("No more users except you");
                        }
                        else{
                            msg.setText("Users:");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("USER FRAGMENT", "onCancelled: "+ databaseError.getMessage());

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
//        getAllUsers();
    }
}
