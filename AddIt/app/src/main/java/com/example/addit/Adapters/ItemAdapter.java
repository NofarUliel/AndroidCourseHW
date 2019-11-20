package com.example.addit.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import com.example.addit.Fragment.EditItemFragment;
import com.example.addit.Model.Item;
import com.example.addit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.addit.Validation.isNotEmpty;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder>{
    private final String TAG="ItemAdapter";
    private List<Item> itemList;
    private DatabaseReference DB;
    private Context context;
    private FirebaseUser user;



    public ItemAdapter( DatabaseReference DB,Context context,FirebaseUser user){
        this.DB=DB;
        this.context=context;
        this.user=user;

    }
    public ItemAdapter(List<Item> itemList,DatabaseReference DB,Context context,FirebaseUser user) {
        this.DB=DB;
        this.itemList=itemList;
        this.context=context;
        this.user=user;

    }

    @NonNull
    @Override
    public ItemAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        Log.d("ADAPTER CREATE", "onCreateViewHolder: create");
        return new ItemAdapter.ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ItemHolder holder, int position) {
        String name=itemList.get(position).getName();
        String image=itemList.get(position).getImage();
        boolean isMark=itemList.get(position).isMark();
        double price=itemList.get(position).getPrice();
        int amount=itemList.get(position).getAmount();

        holder.current_item=itemList.get(position);
        holder.item_name.setText(name);
        holder.checkBox.setChecked(isMark);
        holder.price_txt.setText(price+"₪");
        holder.amount_txt.setText(amount+"");
        holder.setAmount(amount);
        holder.setPrice(price);
        holder.isChecked(isMark);
        holder.setId(itemList.get(position).getId());

        try{

            Picasso.get().load(image).placeholder(R.drawable.ic_cart).into(holder.image);
        }
        catch (Exception e){
            Log.d(TAG, "onDataChange: failed to load item image");
        }

    }

    @Override
    public int getItemCount() {
        if(itemList==null) return 0;
        return itemList.size();
    }


    public class ItemHolder extends RecyclerView.ViewHolder {

        public Item current_item;
        private String id;
        private  TextView item_name,amount_txt,price_txt;
        private CheckBox checkBox;
        private  ImageView image;
        private CardView item_card;
        private  ImageButton menu_btn;
        private int amount=1;
        private double price=0;



        public ItemHolder(@NonNull final View itemView) {

            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            checkBox = itemView.findViewById(R.id.checkBox);
            image = itemView.findViewById(R.id.item_img);
            item_card = itemView.findViewById(R.id.item_card);
            menu_btn = itemView.findViewById(R.id.item_menu);
            amount_txt = itemView.findViewById(R.id.item_amount);
            price_txt = itemView.findViewById(R.id.item_price);

            menu_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(menu_btn);
                }
            });


            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = item_name.getText().toString().trim();
                    final Item item = new Item(id, name, checkBox.isChecked(),amount,price,current_item.getImage());
                    DB.child("Item").child(id).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onClick: id data=" + item.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure:add item ",e );
                        }
                    });

                }
            });

        }
        private void showPopupMenu(View view) {
            // inflate menu
            PopupMenu popup = new PopupMenu(view.getContext(),view );
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.item_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new MyMenuItemClickListener(itemView,current_item,DB));
            popup.show();
        }

        public void setId(String id){this.id=id;}
        public void isChecked( boolean isMark){
            if(isMark){
                item_card.setBackgroundColor(000000);
            }

        }

        public void setPrice(double price) {
        this.price=price;
        }
        public void setAmount(int amount) {
            this.amount=amount;
        }
    }

    public class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        TextView list_name;
        DatabaseReference DB;
        Item item;

        public MyMenuItemClickListener(View itemView,Item item, DatabaseReference DB) {
            list_name = itemView.findViewById(R.id.name);
            this.DB = DB;
            this.item=item;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.edit_item:
                    FragmentManager fragmentManager=((FragmentActivity) context).getSupportFragmentManager();
                    Fragment myFragment = new EditItemFragment(item,DB,user);
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();
                    return true;


                case R.id.delete_item:
                    deleteItem();
                    return true;

            }

            Log.d("FALSE", "onMenuItemClick: ");
            return false;
        }


        private void deleteItem() {
            DB.child("Item").child(item.getId()).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Item deleted!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        private void editName(){

            AlertDialog.Builder builder= new AlertDialog.Builder(context);
            builder.setTitle("Edit item name");

            LinearLayout linearLayout=new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setPadding(10,10,10,10);

            final EditText item_name=new EditText(context);
            item_name.setHint("Item name");

            linearLayout.addView(item_name);
            builder.setView(linearLayout);

            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dial, int which) {
                    String item_name_txt = item_name.getText().toString().trim();

                    //check validation
                    if (!isNotEmpty(item_name_txt, item_name))
                        return;
                    DB.child("Item").child(item.getId()).child("name").setValue(item_name_txt)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Updated item name!", Toast.LENGTH_SHORT).show();
                        }
                    });

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
    }
}
