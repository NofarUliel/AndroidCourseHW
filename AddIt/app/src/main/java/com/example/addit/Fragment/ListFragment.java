package com.example.addit.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.addit.Adapters.ItemAdapter;
import com.example.addit.Model.Item;
import com.example.addit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.addit.Validation.isNotEmpty;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {
    private final String TAG="ListFragment";
    private String managerId,currentId;
    private View view;
    private RecyclerView recyclerView;
    private DatabaseReference DB;
    private ItemAdapter itemAdapter;
    private ImageButton add_item_btn;
    private ImageView add_member_btn;
    private String listId="list id",listName="list name";
    private TextView list_name_txt,msg,total_price;
    private LinearLayout member_layout;
    private FirebaseUser user;
    private double total=0;
    private CardView cardView;
    private ProgressDialog dialog;



    public ListFragment() {
        // Required empty public constructor
    }
    public  ListFragment(String listId, String listName,String managerId){

        this.listId=listId;
        this.listName=listName;
        this.managerId=managerId;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        dialog = new ProgressDialog(getContext());
        DB = FirebaseDatabase.getInstance().getReference().child("Shopping List").child(listId);
        user= FirebaseAuth.getInstance().getCurrentUser();
        currentId =user.getUid();
        msg=view.findViewById(R.id.item_msg);
        total_price=view.findViewById(R.id.total_price);
        list_name_txt=view.findViewById(R.id.list_item_name);
        cardView=view.findViewById(R.id.total);
        list_name_txt.setText(listName);

        recyclerView = view.findViewById(R.id.item_recyclerView);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        itemAdapter = new ItemAdapter(DB,this.getContext(),user);
        recyclerView.setAdapter(itemAdapter);
        add_item_btn = view.findViewById(R.id.add_item_btn);

        member_layout=view.findViewById(R.id.members_layout);
        add_member_btn=view.findViewById(R.id.add_member_btn);


        add_item_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());

                LayoutInflater inflater = LayoutInflater.from(getContext());
                View myView = inflater.inflate(R.layout.create_item, null);
                final AlertDialog dialog = myDialog.create();
                dialog.setView(myView);
                dialog.show();
                final EditText item_name = myView.findViewById(R.id.item_name);
                Button add_btn = myView.findViewById(R.id.add_item_btn);
                Button cancel_btn = myView.findViewById(R.id.cancel_item_btn);

                add_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String item_name_txt = item_name.getText().toString().trim();

                        if (!isNotEmpty(item_name_txt, item_name))
                            return;

                        String id = DB.push().getKey();
                        Item item = new Item(id,item_name_txt,false,1,0);
                        DB.child("Item").child(id).setValue(item);

                        dialog.dismiss();
                    }
                });

                cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


            }

        });


        add_member_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager=((FragmentActivity) v.getContext()).getSupportFragmentManager();
                Fragment myFragment = new UsersFragment(listId,managerId,currentId);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog.setMessage("Loading ...");
        dialog.show();

        DB.child("Item").orderByChild("mark")
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                total=0;
                List<Item> itemList=new ArrayList<>();
                dialog.dismiss();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Item item = ds.getValue(Item.class);
                    itemList.add(item);
                    if(!item.isMark()) {
                        total += (item.getPrice() * item.getAmount());
                    }

                    itemAdapter = new ItemAdapter(itemList,DB,getActivity(),user);
                    recyclerView.setAdapter(itemAdapter);
                    total_price.setText(total + "₪");
                }
                if (itemList.isEmpty()) {
                    msg.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                    cardView.setVisibility(View.INVISIBLE);

                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    msg.setVisibility(View.INVISIBLE);
                    cardView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ",databaseError.toException() );

            }
        });
    }
}