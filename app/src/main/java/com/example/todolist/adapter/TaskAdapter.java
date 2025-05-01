package com.example.todolist.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.model.Task;
import com.example.todolist.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private List<Task> taskList;
    private Context context;

    public TaskAdapter(List<Task> tasks, Context context) {
        this.context = context;
        this.taskList = tasks;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskDes.setText(task.getDescription());
        holder.taskTitle.setText(task.getTitle());
        holder.taskStatus.setText(task.getStatus());
        if(task.getStatus().equals("unfinished")){
            holder.itemView.setBackgroundResource(R.drawable.item_nodone);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.item_done);
        }
        Date date = task.getDueDateTime().toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(date);
        holder.taskTime.setText(formattedDate);

    }


    public void removeItem(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
    }

    public void updateList(List<Task> newList) {
        taskList = newList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }
    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView taskDes, taskTime, taskStatus, taskTitle;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            taskDes = itemView.findViewById(R.id.taskDes);
            taskTime = itemView.findViewById(R.id.taskTime);
            taskStatus = itemView.findViewById(R.id.taskStatus);
            taskTitle = itemView.findViewById(R.id.title);
        }
    }
}
