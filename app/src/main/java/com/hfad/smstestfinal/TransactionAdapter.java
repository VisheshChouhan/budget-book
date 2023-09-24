package com.hfad.smstestfinal;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TimeZone;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private ArrayList<ArrayList<String>> data;

    public TransactionAdapter(ArrayList<ArrayList<String>> dl)
    {
        this.data = dl;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private CardView cardView;
        public ViewHolder(CardView v)
        {
            super(v);
            cardView = v;
        }

    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_cardview, parent, false);




        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position) {
        CardView cv = holder.cardView;

        TextView status = cv.findViewById(R.id.Status);
        status.setText(data.get(position).get(2));

        TextView Date = cv.findViewById(R.id.date);

        java.sql.Date date = new Date(Long.parseLong(data.get(position).get(1)));
        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String formatted = format.format(date);
        Log.e("Changed Date", new SimpleDateFormat(formatted).toString());
        System.out.println(formatted);

        Date.setText(formatted);

        TextView amount = cv.findViewById(R.id.amount);
        amount.setText(data.get(position).get(0));

        TextView message = cv.findViewById(R.id.message);
        message.setText(data.get(position).get(3));

        TextView sender = cv.findViewById(R.id.SenderId);
        sender.setText("Sender Id: "+data.get(position).get(4));

        cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                cv.setCardBackgroundColor(Color.parseColor("#f0f0f0"));
                return false;
            }
        });


        holder.cardView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Restore the default background on release
                    //Drawable defaultDrawable = ContextCompat.getDrawable(context, R.drawable.default_background);
                    holder.cardView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                return false;
            }
        });




    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
