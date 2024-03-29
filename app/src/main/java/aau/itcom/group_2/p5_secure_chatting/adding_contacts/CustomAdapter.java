package aau.itcom.group_2.p5_secure_chatting.adding_contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import aau.itcom.group_2.p5_secure_chatting.R;

public class CustomAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<ContactRequest> list;
    private Context context;
    private AddContactActivity addContactActivity = new AddContactActivity();


    public CustomAdapter(ArrayList<ContactRequest> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.request_list, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position).getMessage());

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
        Button addBtn = (Button)view.findViewById(R.id.add_btn);


        /**
         * DELETION OF REQUEST
         */
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                addContactActivity.declineRequest(list.get(position));

                list.remove(position); //or some other task
                notifyDataSetChanged();
            }
        });

        /**
         * ACCEPTING REQUEST
         */
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                addContactActivity.acceptRequest(list.get(position));

                list.remove(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
