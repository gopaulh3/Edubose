package com.example.edubose;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context mContext ;
    private List<String> names;
    private List<Integer> drawables;
    private Dialog myDialog;


    public RecyclerViewAdapter(Context mContext, List<String> names, List<Integer> drawables) {
        this.mContext = mContext;
        this.names = names;
        this.drawables = drawables;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardveiw_item_book,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.tv_book_title.setText(names.get(position));
        holder.img_book_thumbnail.setImageResource(drawables.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Entered", Toast.LENGTH_SHORT).show();
                myDialog = new Dialog(mContext);
                myDialog.setContentView(R.layout.question_options_dialog);
//                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                final TextView topic = myDialog.findViewById(R.id.dialog_name_id);

                topic.setText(names.get(position));

                final TextView questions_number = myDialog.findViewById(R.id.question_number);
                final ImageView question_minus = myDialog.findViewById(R.id.question_minus);
                final ImageView question_plus = myDialog.findViewById(R.id.question_plus);

                setQuestionStuff(questions_number,question_minus,question_plus);

                final TextView digits_number = myDialog.findViewById(R.id.digits_number);
                final ImageView digits_minus = myDialog.findViewById(R.id.digits_minus);
                final ImageView digits_plus = myDialog.findViewById(R.id.digits_plus);

                setDigitStuff(digits_number,digits_minus,digits_plus);

                final TextView formation_first_number = myDialog.findViewById(R.id.digit_first_number);
                final TextView formation_second_number = myDialog.findViewById(R.id.digit_second_number);
                final ImageView formation_first_minus = myDialog.findViewById(R.id.formation_first_minus);
                final ImageView formation_first_plus = myDialog.findViewById(R.id.formation_first_plus);
                final ImageView formation_second_minus = myDialog.findViewById(R.id.formation_second_minus);
                final ImageView formation_second_plus = myDialog.findViewById(R.id.formatino_second_plus);

                setFormationStuff(formation_first_number,formation_second_number,formation_first_minus,formation_first_plus,formation_second_minus,formation_second_plus);

                final Button dialog_confirm = myDialog.findViewById(R.id.dialog_confirm);

                dialog_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext,InviteToPlayActivity.class);
                        String[] strings = {questions_number.getText().toString(),digits_number.getText().toString(),formation_first_number.getText().toString(),
                        formation_second_number.getText().toString()};
                        intent.putExtra("Info",strings);
                        myDialog.cancel();
                        mContext.startActivity(intent);
                    }
                });

                myDialog.show();

            }
        });
    }

    private void setFormationStuff(final TextView fn, final TextView sn, ImageView fm , ImageView fp, ImageView sp, ImageView sm) {
        fm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = Integer.parseInt(fn.getText().toString());
                if (x>1) fn.setText(String.valueOf(--x));
            }
        });
        fp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = Integer.parseInt(fn.getText().toString());
                if (x<6) fn.setText(String.valueOf(++x));
            }
        });
        sm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = Integer.parseInt(sn.getText().toString());
                if (x>0) sn.setText(String.valueOf(--x));
            }
        });
        sp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = Integer.parseInt(sn.getText().toString());
                if (x<6) sn.setText(String.valueOf(++x));
            }
        });
    }

    private void setQuestionStuff(final TextView number, ImageView plus, ImageView minus) {

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = Integer.parseInt(number.getText().toString());
                if (x>30) number.setText(String.valueOf(--x));
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = Integer.parseInt(number.getText().toString());
                if (x<101) number.setText(String.valueOf(++x));
            }
        });
    }

    private void setDigitStuff(final TextView number, ImageView plus, ImageView minus) {
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number.getText().toString().equals("Normal")) {
                    number.setText("Easy");
                } else if (number.getText().toString().equals("Hard")) {
                    number.setText("Normal");
                }
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number.getText().toString().equals("Normal")) {
                    number.setText("Hard");
                } else if (number.getText().toString().equals("Easy")) {
                    number.setText("Normal");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_book_title;
        ImageView img_book_thumbnail;
        CardView cardView ;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_book_title = itemView.findViewById(R.id.book_title_id) ;
            img_book_thumbnail = itemView.findViewById(R.id.book_img_id);
            cardView = itemView.findViewById(R.id.cardview_id);



        }
    }


}
