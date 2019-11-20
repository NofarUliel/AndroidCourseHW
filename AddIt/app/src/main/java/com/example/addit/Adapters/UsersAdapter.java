package com.example.addit.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.addit.Fragment.MyListsFragment;
import com.example.addit.Fragment.UsersFragment;
import com.example.addit.Model.Invitation;
import com.example.addit.Model.Item;
import com.example.addit.Model.User;
import com.example.addit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserHolder> {
    private  final String TAG="UsersAdapter";
    private  final String SEND="send";
    private  final String NOT_SEND="not_send";
    private  final String MEMBER="member";
    private  final int STATUS_SEND=0;
    private  final int STATUS_NOT_SEND=-1;
    private  final int STATUS_MEMBER=1;
    private  String listId;
    private String managerId;
    private Context context;
    private List<User> userList;
    private List<String> membersList;
    private List<Invitation> invitationList;
    private String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();


    public UsersAdapter() { }
    public UsersAdapter( List<User> userList,String listId,List<String> membersList, List<Invitation> invitationList,String managerId,Context context)
    {
        this.userList = userList;
        this.listId=listId;
        this.membersList=membersList;
        this.invitationList=invitationList;
        this.managerId=managerId;
        this.context=context;
    }

    @NonNull
    @Override
    public UsersAdapter.UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.UserHolder holder, int position) {
        String name=userList.get(position).getName();
        String email=userList.get(position).getEmail();
        String image=userList.get(position).getImage();

        holder.name.setText(name);
        holder.email.setText(email);
        holder.position=position;
        holder.setId(userList.get(position).getId());
        try{
            Picasso.get().load(image).placeholder(R.drawable.ic_profile).into(holder.image);
        }
        catch (Exception e){
            Log.d(TAG, "onDataChange: failed to load user image");

        }

        for (String member:membersList) {
            if(member.equals(userList.get(position).getId())) {
                holder.add_user_btn.setImageResource(R.drawable.ic_check);
                holder.setStatus_invitation(MEMBER);

            }
        }
        for (Invitation i:invitationList) {
            if(!i.isAccepted()) {
                if (i.getReceiverID().equals(userList.get(position).getId())) {
                    holder.add_user_btn.setImageResource(R.drawable.ic_email);
                    holder.setStatus_invitation(SEND);

                }
            }
        }
    }



    @Override
    public int getItemCount() {
        if(userList==null)
        return 0;
        else
            return userList.size();
    }

    public class UserHolder extends RecyclerView.ViewHolder {
        private CircularImageView image;
        private TextView email,name,manager;
        private String id;
        private ImageView add_user_btn;
        private DatabaseReference DB;
        private int status_invitation=STATUS_NOT_SEND;
        private int position;


        public UserHolder(@NonNull View itemView) {
            super(itemView);
            DB=FirebaseDatabase.getInstance().getReference().child("Invitation");
            email=itemView.findViewById(R.id.userEmail);
            name=itemView.findViewById(R.id.userName);
            image=itemView.findViewById(R.id.userAvatar);
            add_user_btn=itemView.findViewById(R.id.add_user);
            manager=itemView.findViewById(R.id.manager);



            add_user_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (status_invitation == STATUS_NOT_SEND) {
                        String idInvitation = DB.push().getKey();
                        Invitation newInvitation = new Invitation(idInvitation, userId, id, false, listId);

                        if (invitationList.isEmpty()) {
                            invitationList = new ArrayList<>();
                            addMember(newInvitation, idInvitation);
                        } else {
                            for (Invitation invitation : invitationList) {
                                if (!invitation.equals(newInvitation))
                                    addMember(newInvitation, idInvitation);

                                else
                                    Log.d(TAG, "onClick: already in list");


                            }
                        }
                    }
                    else {
                        if (status_invitation == STATUS_MEMBER && !userId.equals(managerId)) {
                            removeMember();

                        }
                    }
                }
            });
        }
        public void setId(String id){this.id=id;
            if(id.equals(managerId))
                manager.setVisibility(View.VISIBLE);}
        public void setStatus_invitation(String status){
            switch (status) {
                case SEND:
                    this.status_invitation = STATUS_SEND;
                    break;
                case MEMBER:
                    this.status_invitation = STATUS_MEMBER;
                    break;
                case NOT_SEND:
                default:
                    this.status_invitation=STATUS_NOT_SEND;
            }

        }

        private void addMember( Invitation newInvitation, String idInvitation){
            invitationList.add(newInvitation);
            DB.child(idInvitation).setValue(newInvitation)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Invitation successfully sent!", Toast.LENGTH_SHORT).show();
                            add_user_btn.setImageResource(R.drawable.ic_email);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "onFailure: failed to added user", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        private void removeMember(){
            Log.d(TAG, "removeMember: posit="+position);
            membersList.remove(position);
           FirebaseDatabase.getInstance().getReference().child("Shopping List").child(listId).child("members").setValue(membersList)
                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "You have successfully left the shopping list!", Toast.LENGTH_LONG).show();

                            FragmentManager fragmentManager=((FragmentActivity) context).getSupportFragmentManager();
                            Fragment myFragment = new MyListsFragment();
                            fragmentManager.beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "You have failed to left the list", Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }
}

