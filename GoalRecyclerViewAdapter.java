package com.example.justsavings.MAIN_ACTIVITY;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.justsavings.R;

import java.util.List;

public class GoalRecyclerViewAdapter extends RecyclerView.Adapter<GoalRecyclerViewAdapter.ViewHolder> {

    private List<Goal> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    GoalRecyclerViewAdapter(Context context, List<Goal> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.NameOfTheNote.setText(mData.get(position).name);
        holder.RequiredMoney.setText(mData.get(position).required_money+mData.get(position).currency_sign);
        holder.image.setImageResource(mData.get(position).url);
        holder.CollectedMoney.setText(mData.get(position).amount_collected+mData.get(position).currency_sign);

        int amount = Integer.parseInt(mData.get(position).amount_collected);
        int required = Integer.parseInt(mData.get(position).required_money);
        int procent = (int) (((double) amount / (double) required)*100);

        holder.progress.setText(String.valueOf(procent)+'%');
        holder.bar.setProgress(procent);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView NameOfTheNote;
        TextView RequiredMoney;
        TextView CollectedMoney;
        TextView progress;
        ImageView image;
        ProgressBar bar;


        ViewHolder(View itemView) {
            super(itemView);
            NameOfTheNote = itemView.findViewById(R.id.list_row_name);
            RequiredMoney = itemView.findViewById(R.id.list_row_required);
            CollectedMoney = itemView.findViewById(R.id.list_row_collected);
            progress = itemView.findViewById(R.id.list_row_progress);
            image = itemView.findViewById(R.id.list_row_image);
            bar = itemView.findViewById(R.id.list_row_progressBar);
            image.setImageResource(R.drawable.ic_launcher_background);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null) mClickListener.onLongClick(view, getAdapterPosition());
            return true;
        }

    }

    // convenience method for getting data at click position
    Goal getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onLongClick(View view, int position);
    }


}
