package com.example.addit.Adapters;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.addit.Fragment.ListFragment;
import com.example.addit.Model.Invitation;
import com.example.addit.Model.ShoppingListData;
import com.example.addit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListHolder>{
    private final String TAG ="ShoppingListAdapter";
    private DatabaseReference DB;
    private List<ShoppingListData> shoppingList;
    private String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
    private View view;
    private Context context;

    public ShoppingListAdapter(DatabaseReference DB, Context context){
        this.DB=DB;
        this.shoppingList = new ArrayList<>();
        this.context=context;
    }
    public ShoppingListAdapter(DatabaseReference DB,List<ShoppingListData> shoppingList,Context context) {
        this.DB = DB;
       this.shoppingList=shoppingList;
        this.context=context;

    }


    @Override
    public ShoppingListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list, parent, false);
        Log.d("ADAPTER CREATE", "onCreateViewHolder: create");
        return new ShoppingListHolder(view,DB);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListHolder holder, int position) {

        String name=shoppingList.get(position).getName();
        String note=shoppingList.get(position).getNote();
        String date=shoppingList.get(position).getDate();
        String manager=shoppingList.get(position).getManager();
        List<String> membersOfLists=shoppingList.get(position).getMembers();

        holder.list_name.setText(name);
        holder.list_note.setText(note);
        holder.list_date.setText(date);
        holder.setManagerId(manager);
        holder.setId(shoppingList.get(position).getId());
        holder.setNameList(name);
        holder.setMembers(membersOfLists);

    }

    @Override
    public int getItemCount() {
        if(shoppingList==null) return 0;
        return shoppingList.size();
    }


    public class ShoppingListHolder extends RecyclerView.ViewHolder {

        private TextView list_name, list_note,list_date;
        private ImageButton mImageButton,delete_btn;
        private DatabaseReference DB;
        private CardView cardView;
        private String name_list,id,managerId;
        private List<String> members;


        public ShoppingListHolder(@NonNull final View itemView, final DatabaseReference DB) {

            super(itemView);
            this.DB=DB;
            list_name = itemView.findViewById(R.id.name);
            list_note = itemView.findViewById(R.id.note);
            list_date = itemView.findViewById(R.id.date);
            delete_btn = itemView.findViewById(R.id.delete_btn);
            mImageButton = itemView.findViewById(R.id.imageButton);
            cardView=itemView.findViewById(R.id.card_view);


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager=((FragmentActivity) itemView.getContext()).getSupportFragmentManager();
                    Fragment myFragment = new ListFragment(id,name_list,managerId);
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();

                }

            });
            mImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(mImageButton);
                }
            });
            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog dialog=new ProgressDialog(context);
                    dialog.setMessage("Delete shopping list");

                    AlertDialog.Builder builder= new AlertDialog.Builder(context);
                    builder.setTitle("Are you sure you want to delete this shopping list?");

                    LinearLayout linearLayout=new LinearLayout(context);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setPadding(10,10,10,10);
                    builder.setView(linearLayout);
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dia, int which) {
                            dia.dismiss();
                            dialog.dismiss();

                        }
                    });
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dial, int which) {
                            dialog.show();

                            DB.child(id).removeValue()
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(context, "Successfully deleted shopping list !", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
                            FirebaseDatabase.getInstance().getReference().child("Invitation")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                Invitation invitation = ds.getValue(Invitation.class);
                                                if (invitation.getListID().equals(id))
                                                    ds.getRef().removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.e(TAG, "onCancelled: ", databaseError.toException());

                                        }
                                    });
                        }
                    });


                    builder.create().show();
                }

            });


        }

        private void showPopupMenu(View view) {
            // inflate menu
            PopupMenu popup = new PopupMenu(view.getContext(),view );
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.list_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new MyMenuItemClickListener(itemView,id,DB));
            popup.show();
        }

        public void setId(String id){
            this.id=id;
        }

        public void setNameList(String name) {
          this.name_list=name;
        }
        public void setMembers(List<String> list){
            this.members=list;
        }
        public void setManagerId(String managerId) {
            this.managerId = managerId;
            if(managerId.equals(userId)){
                delete_btn.setVisibility(View.VISIBLE);
            }
        }


    }


   public class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

      private TextView list_name,update_name, list_note,update_note,list_date;
      private String id;
      private DatabaseReference DB;
      private ImageButton delete_btn;
      private  Button cancel_btn,update_btn;
        public MyMenuItemClickListener(View itemView,String id,  DatabaseReference DB) {
            list_name = itemView.findViewById(R.id.name);
            list_note = itemView.findViewById(R.id.note);
            update_name = itemView.findViewById(R.id.update_name);
            update_note = itemView.findViewById(R.id.update_note);
            list_date = itemView.findViewById(R.id.date);
            cancel_btn = itemView.findViewById(R.id.cancel_btn);
            update_btn=itemView.findViewById(R.id.update_btn);
            delete_btn = itemView.findViewById(R.id.delete_btn);
            this.id=id;
            this.DB=DB;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.edit_name:
                   EditName();
                    return true;

                case R.id.edit_note:
                    EditNote();
                    return true;

            }

            Log.d("FALSE", "onMenuItemClick: ");
            return false;
        }

       public  void EditName(){

           list_name.setVisibility(View.INVISIBLE);
           update_name.setText(list_name.getText().toString());
           update_name.setVisibility(View.VISIBLE);
           delete_btn.setVisibility(View.INVISIBLE);
           update_btn.setVisibility(View.VISIBLE);
           cancel_btn.setVisibility(View.VISIBLE);


           update_btn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   String new_name=update_name.getText().toString();
                   update_name.setVisibility(View.INVISIBLE);
                   list_name.setVisibility(View.VISIBLE);
                   delete_btn.setVisibility(View.VISIBLE);
                   update_btn.setVisibility(View.INVISIBLE);
                   cancel_btn.setVisibility(View.INVISIBLE);
                   //update database
                   DB.child(id).child("name").setValue(new_name);
               }
           });

           cancel_btn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   update_btn.setVisibility(View.INVISIBLE);
                   cancel_btn.setVisibility(View.INVISIBLE);
                   update_name.setVisibility(View.INVISIBLE);
                   list_name.setVisibility(View.VISIBLE);
                   delete_btn.setVisibility(View.VISIBLE);

               }
           });



       }

       public void EditNote(){

           list_note.setVisibility(View.INVISIBLE);
           update_note.setText(list_note.getText().toString());
           update_note.setVisibility(View.VISIBLE);
           delete_btn.setVisibility(View.INVISIBLE);
           update_btn.setVisibility(View.VISIBLE);
           cancel_btn.setVisibility(View.VISIBLE);
           update_btn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   String new_note=update_note.getText().toString();


                   update_note.setVisibility(View.INVISIBLE);
                   list_note.setVisibility(View.VISIBLE);
                   delete_btn.setVisibility(View.VISIBLE);
                   update_btn.setVisibility(View.INVISIBLE);
                   cancel_btn.setVisibility(View.INVISIBLE);
//                  update database
                   DB.child(id).child("note").setValue(new_note);
               }
           });

           cancel_btn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   delete_btn.setVisibility(View.VISIBLE);
                   update_btn.setVisibility(View.INVISIBLE);
                   cancel_btn.setVisibility(View.INVISIBLE);
               }
           });

       }

    }
}
