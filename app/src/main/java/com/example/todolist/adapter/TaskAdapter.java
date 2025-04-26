package com.example.todolist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.model.Task;
import com.example.todolist.retrofit.Api;
import com.example.todolist.retrofit.RetrofitClient;
import com.example.todolist.utils.Utils;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private List<Task> taskList;
    private Context context;
    private CompositeDisposable compositeDisposable;
    private Api api;

    public TaskAdapter(List<Task> tasks, Context context) {
        this.context = context;
        this.taskList = tasks;
        this.compositeDisposable = new CompositeDisposable();
        this.api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
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
        holder.taskText.setText(task.getDescription());
        holder.taskHours.setText(task.getCreatedAt());

        holder.imageButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {

            }
        });
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
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        compositeDisposable.clear();
    }
    @Override
    public int getItemCount() {
        return taskList.size();
    }
    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView taskText, taskDay, taskHours;
        ImageButton imageButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            taskText = itemView.findViewById(R.id.taskText);
            taskDay = itemView.findViewById(R.id.taskDay);
            taskHours = itemView.findViewById(R.id.taskHour);
            imageButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
