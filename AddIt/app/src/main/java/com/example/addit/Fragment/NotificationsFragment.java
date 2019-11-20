package com.example.addit.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.addit.Adapters.InvitationAdapter;
import com.example.addit.Model.Invitation;
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
public class NotificationsFragment extends Fragment {

    private final String TAG="NotificationsFragment";
    private RecyclerView recyclerView;
    private DatabaseReference DB;
    private InvitationAdapter invitationAdapter;
    private TextView msg;
    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_notifications, container, false);
        DB=FirebaseDatabase.getInstance().getReference("Invitation");
        recyclerView = view.findViewById(R.id.notification_recyclerView);
        msg=view.findViewById(R.id.notification_msg);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        invitationAdapter = new InvitationAdapter(getActivity());
        recyclerView.setAdapter(invitationAdapter);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        final String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Invitation> invitationList=new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Invitation i = ds.getValue(Invitation.class);
                    if(i.getReceiverID().equals(userId)&&i.isAccepted()==false)
                        invitationList.add(i);
                    invitationAdapter = new InvitationAdapter(invitationList,getActivity());
                    recyclerView.setAdapter(invitationAdapter);
                }
                if(invitationList.isEmpty())
                    msg.setVisibility(View.VISIBLE);
                else
                    msg.setVisibility(View.INVISIBLE);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: add invitation", databaseError.toException());
            }
        });
    }
}
