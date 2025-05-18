package com.example.moitoi;

public class AnswerItem {
    private int questionNumber;
    private String userAnswer;
    private String correctAnswer;
    private boolean isCorrect;

    public AnswerItem(int questionNumber, String userAnswer, String correctAnswer, boolean isCorrect) {
        this.questionNumber = questionNumber;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
        this.isCorrect = isCorrect;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
