package com.example.addit.Fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.addit.Adapters.ShoppingListAdapter;
import com.example.addit.Model.ShoppingListData;
import com.example.addit.Model.User;
import com.example.addit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.addit.Validation.isNotEmpty;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyListsFragment extends Fragment {


    private final String TAG="MyListFragment";
    private View view;
    private RecyclerView recyclerView;
    private DatabaseReference DB;
    private ShoppingListAdapter shoppingAdapter;
    private ImageButton plus_btn;
    private TextView msg;
    private User currentUser;
    private String userId;
    private ProgressDialog dialog;



    public MyListsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_lists, container, false);
        DB = FirebaseDatabase.getInstance().getReference().child("Shopping List");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView = view.findViewById(R.id.shopping_recycler_view);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);


        shoppingAdapter = new ShoppingListAdapter(DB);
        recyclerView.setAdapter(shoppingAdapter);
        plus_btn = view.findViewById(R.id.add_list_btn);
        msg=view.findViewById(R.id.my_list_msg);
        dialog = new ProgressDialog(getContext());


        // Add new shopping list



        plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View myView = inflater.inflate(R.layout.create_list, null);
                final AlertDialog dialog = myDialog.create();
                dialog.setView(myView);

                final EditText list_name = myView.findViewById(R.id.list_name);
                final EditText note = myView.findViewById(R.id.note);
                Button add_btn = myView.findViewById(R.id.add_btn);
                final Button cancel_btn = myView.findViewById(R.id.cancel_btn);

                add_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String list_name_txt = list_name.getText().toString().trim();
                        String note_txt = note.getText().toString().trim();

                        if (!isNotEmpty(list_name_txt, list_name) || !isNotEmpty(note_txt, note))
                            return;

                        String id = DB.push().getKey();
                        Log.d("ADD LIST", "onClick: id=" + id);
                        String date = DateFormat.getDateInstance().format(new Date());
                        ShoppingListData listData = new ShoppingListData(id, list_name_txt, note_txt, date,userId);
                        List<String> members=listData.getMembers();
                        members.add(userId);
                        listData.setMembers(members);
                        DB.child(id).setValue(listData);
                        dialog.dismiss();

                    }
                });

                cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });



                dialog.show();
            }
        });



        // Inflate the layout for this fragment
        return view;
    }


    private void GetUserShoppingLists(){
        Query query1 = DB.orderByChild("date");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ShoppingListData> shoppingLists=new ArrayList<>();
                dialog.dismiss();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ShoppingListData list = ds.getValue(ShoppingListData.class);
//                    Log.d(TAG, "onDataChange: "+list.getMembers()+"current="+currentUser+"result="+list.getMembers().contains(userId));
                    if(list.getMembers().contains(userId))
                        shoppingLists.add(list);

                    shoppingAdapter = new ShoppingListAdapter(DB, shoppingLists);
                    recyclerView.setAdapter(shoppingAdapter);
                }
                if(shoppingLists.isEmpty()) {
                    msg.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
                else {
                    msg.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ",databaseError.toException() );
                dialog.dismiss();

            }
        });


    }
    @Override
    public void onStart() {
        super.onStart();
        dialog.setMessage("Loading ...");
        dialog.show();
        GetUserShoppingLists();
    }
}




