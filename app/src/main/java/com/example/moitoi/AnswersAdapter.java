package com.example.moitoi;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.AnswerViewHolder> {

    private List<AnswerItem> answerItems;

    public AnswersAdapter(List<AnswerItem> answerItems) {
        this.answerItems = answerItems;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_answer, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        AnswerItem item = answerItems.get(position);
        
        holder.tvQuestionNumber.setText("Question " + item.getQuestionNumber());
        holder.tvUserAnswer.setText("Votre réponse: " + item.getUserAnswer());
        holder.tvCorrectAnswer.setText("Bonne réponse: " + item.getCorrectAnswer());
        
        // Change text color based on answer correctness
        int textColor = item.isCorrect() ? Color.GREEN : Color.RED;
        holder.tvUserAnswer.setTextColor(textColor);
    }

    @Override
    public int getItemCount() {
        return answerItems.size();
    }

    static class AnswerViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionNumber;
        TextView tvUserAnswer;
        TextView tvCorrectAnswer;

        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionNumber = itemView.findViewById(R.id.tvQuestionNumber);
            tvUserAnswer = itemView.findViewById(R.id.tvUserAnswer);
            tvCorrectAnswer = itemView.findViewById(R.id.tvCorrectAnswer);
        }
    }
}
