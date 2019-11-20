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
            @SuppressLint("ResourceType")
            @Override
            public void onClick(final View view) {

                AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
                builder.setTitle("Add List");

                LinearLayout linearLayout=new LinearLayout(getActivity());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(10,10,10,10);

                final EditText list_name=new EditText(getActivity());
                list_name.setHint("List name");
                list_name.setId(0);
                linearLayout.addView(list_name);
                final EditText note=new EditText(getActivity());
                note.setHint("Note");
                linearLayout.addView(note);
                builder.setView(linearLayout);

                builder.setPositiveButton("Add List", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dial, int which) {
                        String list_name_txt = list_name.getText().toString().trim();
                        String note_txt = note.getText().toString().trim();
                        //check validation
                        if (!(isNotEmpty(list_name_txt, list_name) || isNotEmpty(note_txt, note)))
                            return;


                        String id = DB.push().getKey();
                        Log.d("ADD LIST", "onClick: id=" + id);
                        String date = DateFormat.getDateInstance().format(new Date());
                        ShoppingListData listData = new ShoppingListData(id, list_name_txt, note_txt, date,userId);
                        List<String> members=listData.getMembers();
                        members.add(userId);
                        listData.setMembers(members);
                        DB.child(id).setValue(listData);

                    }
                });
                builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
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
                    Log.d(TAG, "onDataChange: "+list.getMembers()+"current="+currentUser+"result="+list.getMembers().contains(userId));
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




