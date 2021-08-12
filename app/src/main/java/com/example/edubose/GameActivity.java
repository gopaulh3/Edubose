package com.example.edubose;

import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GameActivity extends AppCompatActivity {

    Button goButton, button0,button1,button2,button3,playAgainButton;
    TextView resultTextView,scoreTextView,sumTextView,timerTextView;
    ArrayList<Integer> answers = new ArrayList<>(4);
    LinkedHashSet<Integer> answers2 = new LinkedHashSet<>();
    int locationOfCorrectAnswer,score=0,numberOfQuestions=0,min=0,max=9,difficulty=0,whichMethod=0,a,b;
    GridLayout gridLayout;
    ConstraintLayout gameLayout;
    String[] strings;
    Random rand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initStuff();
    }

    private void initStuff() {
        sumTextView = findViewById(R.id.sumTextView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        resultTextView = findViewById(R.id.resultTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        timerTextView = findViewById(R.id.timerTextView);
        playAgainButton = findViewById(R.id.playAgainButton);
        gameLayout = findViewById(R.id.gameLayout);
        goButton = findViewById(R.id.goButton);
        gridLayout = findViewById(R.id.gridLayout2);
        goButton.setVisibility(View.VISIBLE);
        gameLayout.setVisibility(View.INVISIBLE);
        strings = getIntent().getStringArrayExtra("strings");

        if (strings[1].equals("Normal")) {
            difficulty = 1;
        } else if (strings[1].equals("Hard")) {
            difficulty = 2;
        }
    }

    public void playAgain(View view) {
        gridLayout.setVisibility(View.VISIBLE);
        score = 0;
        numberOfQuestions = 0;
        timerTextView.setText(strings[0]+"s");
        scoreTextView.setText(Integer.toString(score)+"/"+Integer.toString(numberOfQuestions));
        newQuestion();
        playAgainButton.setVisibility(View.INVISIBLE);
        resultTextView.setText("");

        new CountDownTimer(Integer.parseInt(strings[0])*1000,1000) {

            @Override
            public void onTick(long l) {
                timerTextView.setText(String.valueOf(l / 1000) + "s");
            }

            @Override
            public void onFinish() {
                playAgainButton.setVisibility(View.VISIBLE);
                gridLayout.setVisibility(View.INVISIBLE);
            }
        }.start();
    }

    public void chooseAnswer(View view) {
        if (Integer.toString(locationOfCorrectAnswer).equals(view.getTag().toString())) {
            score++;
        }
        numberOfQuestions++;
        double s = ((score*100) / (double) (numberOfQuestions+1));
        DecimalFormat df = new DecimalFormat("#.00");
        scoreTextView.setText(String.valueOf(df.format(s)));
        resultTextView.setText(Integer.toString(score)+"/"+Integer.toString(numberOfQuestions));
        newQuestion();
    }

    public void start(View view) {
        goButton.setVisibility(View.INVISIBLE);
        gameLayout.setVisibility(View.VISIBLE);
        playAgain(findViewById(R.id.timerTextView));
    }

    public void newQuestion() {
        rand = new Random();
        whichMethod = rand.nextInt(4);
        int correctAnswer = 0, x;
        locationOfCorrectAnswer = rand.nextInt(4);
        boolean bool = false;
        answers2.clear();
        answers.clear();

        switch (whichMethod) {
            case 0: correctAnswer = addition(difficulty); break;
            case 1: correctAnswer = subtraction(difficulty); break;
            case 2: correctAnswer = multiplication(difficulty); break;
            case 3: correctAnswer = division(difficulty); break;
            default: addition(difficulty);
        }

        while (answers2.size()!=4) {
            if (bool) {
                x = ((correctAnswer) + (rand.nextInt((5))));
            } else {
                x = ((correctAnswer) - (rand.nextInt((5))));
            }
            if (x!=(correctAnswer)) answers2.add(x);
            bool=!bool;
        }

        answers = new ArrayList<>(answers2);
        answers.set(locationOfCorrectAnswer,correctAnswer);

        button0.setText(String.valueOf(answers.get(0)));
        button1.setText(String.valueOf(answers.get(1)));
        button2.setText(String.valueOf(answers.get(2)));
        button3.setText(String.valueOf(answers.get(3)));
    }

    private int division(int difficulty) {
        if (difficulty==0) {
            min = 1;
            max = 30;
        } else if (difficulty==1) {
            min = 1;
            max = 50;
        } else {
            min = 1;
            max = 70;
        }

        do {
            a = ThreadLocalRandom.current().nextInt(min, max);
            b = ThreadLocalRandom.current().nextInt(min, max);
        } while (a%b!=0 && b%a!=0);

        if (a>=b) {
            sumTextView.setText(String.valueOf(a) + " / " + String.valueOf(b));
            return a/b;
        } else {
            sumTextView.setText(String.valueOf(b) + " / " + String.valueOf(a));
            return b/a;
        }
    }

    private int multiplication(int difficulty) {
        if (difficulty==0) {
            min = 0;
            max = 6;
        } else if (difficulty==1) {
            min = 6;
            max = 12;
        } else {
            min = 12;
            max = 18;
        }

        a = ThreadLocalRandom.current().nextInt(min, max);
        b = ThreadLocalRandom.current().nextInt(min, max);

        sumTextView.setText(Integer.toString(a) + " x " + Integer.toString(b));
        return a*b;
    }

    private int subtraction(int difficulty) {
        if (difficulty==0) {
            min = 0;
            max = 30;
        } else if (difficulty==1) {
            min = 20;
            max = 50;
        } else {
            min = 40;
            max = 70;
        }

        a = ThreadLocalRandom.current().nextInt(min, max);
        b = ThreadLocalRandom.current().nextInt(min, max);

        if (a>b) {
            sumTextView.setText(Integer.toString(a) + " - " + Integer.toString(b));
            return a-b;
        } else {
            sumTextView.setText(Integer.toString(b) + " - " + Integer.toString(a));
            return b-a;
        }
    }

    private int addition(int difficulty) {
        if (difficulty==0) {
            min = 0;
            max = 20;
        } else if (difficulty==1) {
            min = 20;
            max = 40;
        } else {
            min = 40;
            max = 60;
        }

        a = ThreadLocalRandom.current().nextInt(min, max);
        b = ThreadLocalRandom.current().nextInt(min, max);

        sumTextView.setText(Integer.toString(a) + " + " + Integer.toString(b));
        return a+b;
    }
}
