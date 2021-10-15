package com.rctapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rctapp.R;
import com.rctapp.database.UserPreference;
import com.rctapp.models.ChatFireModel;
import com.rctapp.utils.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FirebaseMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int CHAT_ME = 100;
    private final int CHAT_YOU = 200;
    CharSequence timePassedString;
    UserPreference userPreference;
    Activity activity;
    private List<ChatFireModel> items = new ArrayList<>();

    private Context ctx;
    private FirebaseMessageAdapter.OnItemClickListener mOnItemClickListener;


    public interface OnItemClickListener {
        void onItemClick(View view, ChatFireModel obj, int position);
    }

    public void setOnItemClickListener(final FirebaseMessageAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FirebaseMessageAdapter(Context context, List<ChatFireModel> items, Activity activity, UserPreference userPreference) {
        this.items = items;
        this.ctx = context;
        this.userPreference = userPreference;
        this.activity = activity;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView text_content;
        public TextView text_time;
        public View lyt_parent;

        public ItemViewHolder(View v) {
            super(v);
            text_content = v.findViewById(R.id.text_content);
            text_time = v.findViewById(R.id.text_time);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v;
        if (viewType == CHAT_ME) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_me, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_you, parent, false);
        }
        vh = new FirebaseMessageAdapter.ItemViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof FirebaseMessageAdapter.ItemViewHolder) {
            final ChatFireModel m = items.get(position);
            FirebaseMessageAdapter.ItemViewHolder vItem = (FirebaseMessageAdapter.ItemViewHolder) holder;
            vItem.text_content.setText(m.getMessage());
            vItem.text_time.setText(Tools.getUnixToDate(m.getTime()));
            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, m, position);
                    }
                }
            });

        }
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        try {
            return this.items.get(position).getSender_id().equals(userPreference.getUserId()) ? CHAT_ME : CHAT_YOU;
        }catch (Exception e){
            e.printStackTrace();
            return CHAT_YOU;
        }
    }

    public void insertItem(ChatFireModel item) {
        this.items.add(item);
        notifyItemInserted(getItemCount());
    }

    public void setItems(List<ChatFireModel> items) {
        this.items = items;
    }

    private CharSequence formatTime(String dateTime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = df.parse(dateTime);
            long time = date.getTime();
            timePassedString = DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timePassedString;
    }

}
