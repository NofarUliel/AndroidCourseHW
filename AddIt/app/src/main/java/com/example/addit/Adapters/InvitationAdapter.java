package com.example.addit.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.addit.Model.Invitation;
import com.example.addit.Model.ShoppingListData;
import com.example.addit.Model.User;
import com.example.addit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;


public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.InvitationHolder> {
    private final String TAG="InvitationAdapter";
    private List<Invitation> invitationList;
    private Context context;
    private String user_id;

    public InvitationAdapter(Context context) {
        this.context=context;
        this.user_id= FirebaseAuth.getInstance().getCurrentUser().getUid();

    }
    public InvitationAdapter(List<Invitation> invitationList,Context context) {
        this.invitationList = invitationList;
        this.context=context;
        this.user_id= FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public InvitationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification, parent, false);
        return new InvitationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitationHolder holder, int position) {
        String senderID=invitationList.get(position).getSenderID();
        String listID=invitationList.get(position).getListID();
        String invitationID=invitationList.get(position).getId();
        String receiverID=invitationList.get(position).getReceiverID();
        boolean isAccepted=invitationList.get(position).isAccepted();
        holder.setList_id(listID);
        holder.setSender_id(senderID);
        holder.setReceiver_id(receiverID);
        holder.setAccepted(isAccepted);
        holder.setInvitation_id(invitationID);
        holder.setNotification_msg();

    }

    @Override
    public int getItemCount() {
        if(invitationList==null) return 0;
        return invitationList.size();
    }

    public class InvitationHolder extends RecyclerView.ViewHolder {

        private String invitation_id, sender_id,list_id,receiver_id;
        private boolean isAccepted;
        private ImageView sender_avatar;
        private TextView notification_msg;
        private  ConstraintLayout notification_card;
        private User sender;
        private ShoppingListData shoppingList;
        private DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();


        public void setSender_id(String sender_id) {
            this.sender_id = sender_id;
        }

        public void setList_id(String list_id) {
            this.list_id = list_id;
        }

        public void setAccepted(boolean accepted) {
           this.isAccepted = accepted;
        }
        public void setInvitation_id(String invitation_id) {
            this.invitation_id = invitation_id;
        }
        public void setReceiver_id(final String receiver_id) {
            this.receiver_id = receiver_id;
        }

        public InvitationHolder(@NonNull final View itemView) {

            super(itemView);
            notification_msg = itemView.findViewById(R.id.notification_msg);
            sender_avatar = itemView.findViewById(R.id.senderAvatar);
            notification_card = itemView.findViewById(R.id.notification_card);


            notification_card.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    notification_card.setBackgroundColor(Color.WHITE);
                    AlertDialog.Builder builder= new AlertDialog.Builder(context);
                    builder.setTitle(sender.getName() + " invited you to join "+shoppingList.getName()+" shopping list");

                    LinearLayout linearLayout=new LinearLayout(context);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setPadding(10,10,10,10);

                    builder.setView(linearLayout);
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dial, int which) {
                         List<String> members= shoppingList.getMembers();
                         members.add(user_id);
                         databaseReference.child("Shopping List").child(list_id).child("members").setValue(members)
                                 .addOnFailureListener(new OnFailureListener() {
                                     @Override
                                     public void onFailure(@NonNull Exception e) {
                                         Log.e(TAG, "onFailure: share list with user",e);
                                     }
                                 })
                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void aVoid) {
                                         FirebaseDatabase.getInstance().getReference("Invitation").child(invitation_id).child("accepted").setValue(true)
                                                 .addOnFailureListener(new OnFailureListener() {
                                                     @Override
                                                     public void onFailure(@NonNull Exception e) {
                                                         Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                     }
                                                 })
                                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                     @Override
                                                     public void onSuccess(Void aVoid) {
                                                         Toast.makeText(context, "added to list", Toast.LENGTH_SHORT).show();

                                                     }
                                                 });
                                     }
                                 });


                        }
                    });
                    builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dia, int which) {
                            dia.dismiss();
                            FirebaseDatabase.getInstance().getReference("Invitation").child(invitation_id).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(context, "Invitation deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                    });

                    builder.create().show();
                }
            });
        }

        public void setNotification_msg(){
            if(sender_id!=null) {
                SetCurrentUser();
                SetCurrentList();

            }
        }
        private void SetCurrentList(){
            databaseReference.child("Shopping List")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                ShoppingListData list=ds.getValue(ShoppingListData.class);
                                if(list.getId().equals(list_id)) {
                                    shoppingList =list;
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: ", databaseError.toException());
                        }
                    });

        }
        private void SetCurrentUser(){
            databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        User user=ds.getValue(User.class);
                        if(user.getId().equals(sender_id)){
                            sender=user;
                            notification_msg.setText("List Invitation");
                            try{
                                Picasso.get().load(sender.getImage()).fit().centerCrop().placeholder(R.drawable.ic_profile)
                                        .into(sender_avatar);
                            } catch (Exception e) {
                                Log.d(TAG, "onDataChange: failed to load item image");

                            }
                            break;

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: ", databaseError.toException());

                }
            });
        }

    }
}
