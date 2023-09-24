package com.hfad.smstestfinal;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link nonFinalMessages#newInstance} factory method to
 * create an instance of this fragment.
 */
public class nonFinalMessages extends Fragment {


    static GoogleSignInOptions gso;
    static GoogleSignInClient gsc;
    FirebaseDatabase database;
    static DatabaseReference reference;
    int index;
    static String user;
    static long DatabaseDate = 0;
    static long maxDate = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public nonFinalMessages() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment nonFinalMessages.
     */
    // TODO: Rename and change types and number of parameters
    public static nonFinalMessages newInstance(String param1, String param2) {
        nonFinalMessages fragment = new nonFinalMessages();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_non_final_messages, container, false);


        /*******************************************************************/
        Log.d("CHECK","1");
        Cursor cursor = getActivity().getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        //TextView message = findViewById(R.id.messageView);

        Log.d("CHECK","2");
        ArrayList<SmsDto> arrayList = new ArrayList<>();

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            String msgData = "";
            do {

                        /*for(int idx=0;idx<cursor.getColumnCount();idx++)
                        {
                            msgData += idx + "-> " + cursor.getColumnName(idx) + ":" + cursor.getString(idx)+"\n";
                        }*/
                SmsDto temp = new SmsDto();
                temp.setBody(cursor.getString(12));
                temp.setParsed("0");
                temp.setTransactionType("0");
                temp.setSenderId(cursor.getString(2));
                temp.setDate(cursor.getString(5));
                arrayList.add(temp);



                // use msgData
            } while (cursor.moveToNext());
            //message.setText(msgData);
            Log.d("CHECK","L");
        } else {
            // empty box, no SMS
        }



        Log.d("Start", "Finally");
        ArrayList<ArrayList<String>> ans = parsevalues(arrayList);



        TextView messageCount = view.findViewById(R.id.message_count);
        messageCount.setText("Total messages : "+ans.size());

        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        TransactionAdapter adapter = new TransactionAdapter(ans);
        //message.setText(ans.toString());
        /*************************************************************************/
        // Items touch helper
        ItemTouchHelper.SimpleCallback itemTouchHolder = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                ans.remove(viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();


            }
        };

        /**********************************************************************************/



        recyclerView.setAdapter(adapter);

        //adding touch helper
        new ItemTouchHelper(itemTouchHolder).attachToRecyclerView(recyclerView);

        GridLayoutManager gridLayout = new GridLayoutManager(getActivity().getApplicationContext(), 1);
        recyclerView.setLayoutManager(gridLayout);



        Log.d("finish", "Done");
        Log.d("Answer", ans.toString());


        /******************************************************************/

        // Send sms to firebase

        user = "NO USER";

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(getActivity(), gso);

        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(getActivity());
        if(acc!=null)
        {
            user = acc.getEmail();
            user = user.substring(0, user.indexOf("@"));
        }

        FloatingActionButton button = view.findViewById(R.id.sendtoDatabase);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(user);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                index = snapshot.child("index").getValue(Integer.class);
                DatabaseDate = snapshot.child("date").getValue(Long.class);

                ArrayList<ArrayList<String>> removed = new ArrayList<>();
                for(ArrayList<String> e : ans)
                {
                    if( Long.parseLong(e.get(1)) <= DatabaseDate)
                    {
                        removed.add(e);

                    }

                }
                for(ArrayList<String> e : removed)
                {

                        ans.remove(e);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long maxDate =  DatabaseDate;


                for(ArrayList<String> e : ans)
                {
                    if( Long.parseLong(e.get(1)) > DatabaseDate)
                    {
                        reference.child(Integer.toString(index)).setValue(e);
                        index++;
                        if (Long.parseLong(e.get(1)) > maxDate)
                        {
                            maxDate = Long.parseLong(e.get(1));
                        }

                    }

                }
                reference.child("index").setValue(index);
                reference.child("date").setValue(maxDate);


            }
        });


        return view;

    }



    public void readMessages()
    {
        Log.d("CHECK","1");
        Cursor cursor = getActivity().getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        //TextView message = findViewById(R.id.messageView);

        Log.d("CHECK","2");
        ArrayList<SmsDto> arrayList = new ArrayList<>();

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            String msgData = "";
            do {

                        /*for(int idx=0;idx<cursor.getColumnCount();idx++)
                        {
                            msgData += idx + "-> " + cursor.getColumnName(idx) + ":" + cursor.getString(idx)+"\n";
                        }*/
                SmsDto temp = new SmsDto();
                temp.setBody(cursor.getString(12));
                temp.setParsed("0");
                temp.setTransactionType("0");
                temp.setSenderId(cursor.getString(2));
                temp.setDate(cursor.getString(5));
                arrayList.add(temp);



                // use msgData
            } while (cursor.moveToNext());
            //message.setText(msgData);
            Log.d("CHECK","L");
        } else {
            // empty box, no SMS
        }

        Log.d("Start", "Finally");
        ArrayList<ArrayList<String>> ans = parsevalues(arrayList);
        //message.setText(ans.toString());

        RecyclerView recyclerView = getActivity().findViewById(R.id.recycler);
        TransactionAdapter adapter = new TransactionAdapter(ans);

        recyclerView.setAdapter(adapter);

        GridLayoutManager gridLayout = new GridLayoutManager(getActivity().getApplicationContext(), 1);
        recyclerView.setLayoutManager(gridLayout);



        Log.d("finish", "Done");
        Log.d("Answer", ans.toString());

    }



    private static ArrayList<ArrayList<String>> parsevalues(ArrayList<SmsDto> body_val) {
        ArrayList<SmsDto> resSms = new ArrayList<>();
        //Array list To show in text box
        ArrayList<ArrayList<String>> inTheTextBox= new ArrayList<>();





        for (int i = 0; i < body_val.size(); i++) {
            SmsDto smsDto = body_val.get(i);
            Pattern regEx
                    //= Pattern.compile("\"(?=.*[Aa]ccount.*|.*[Aa]/[Cc].*|.*[Aa][Cc][Cc][Tt].*|.*[Cc][Aa][Rr][Dd].*)(?=.*[Cc]redit.*|.*[Dd]ebit.*)(?=.*[Ii][Nn][Rr].*|.*[Rr][Ss].*)\"");
                    = Pattern.compile("(?:inr|Rs)+[\\s]*[0-9+[\\,]*+[0-9]*]+[\\.]*[0-9]+");


            // Find instance of pattern matches
            Matcher m = regEx.matcher(smsDto.getBody());
            if (m.find()) {
                try {
                    Log.e("amount_value= ", "" + m.group(0) + " : "+ smsDto.getBody());
                    String amount = (m.group(0).replaceAll("inr", ""));
                    amount = amount.replaceAll("rs", "");
                    amount = amount.replaceAll("Rs", "");
                    amount = amount.replaceAll("INR", "");
                    amount = amount.replaceAll("inr", "");
                    amount = amount.replaceAll(" ", "");
                    amount = amount.replaceAll(",", "");
                    smsDto.setAmount(Double.valueOf(amount));
                    if (
                            (smsDto.getBody().contains("debited") ||
                                    smsDto.getBody().contains("Debited") ||
                                    smsDto.getBody().contains("purchasing") ||
                                    smsDto.getBody().contains("Purchasing") ||
                                    smsDto.getBody().contains("Purchase") ||
                                    smsDto.getBody().contains("purchase") ||
                                    smsDto.getBody().contains("payment") ||
                                    smsDto.getBody().contains("Payment") ||
                                    smsDto.getBody().contains("dr") ||
                                    smsDto.getBody().contains("Dr"))
                                    && !smsDto.getSenderId().contains("AIRTEL")


                    ) {
                        smsDto.setTransactionType("1");

                    } else if (
                            (smsDto.getBody().contains("credited") ||
                                    smsDto.getBody().contains("Credited") ||

                                    smsDto.getBody().contains("Cr") ||
                                    smsDto.getBody().contains("cr"))
                                    && !smsDto.getSenderId().contains("AIRTEL")

                    )
                    {
                        smsDto.setTransactionType("2");
                    }
                    smsDto.setParsed("1");
                    Log.e("matchedValue= ", "" + amount);

                    if(
                            (smsDto.getTransactionType().equals("1") ||
                            smsDto.getTransactionType().equals("2") )

                    )
                    {
                        ArrayList<String> finalData = new ArrayList<>();
                        finalData.add(amount);
                        //finalData.add(smsDto.getSenderId());
                        Log.e("Sender ID", smsDto.getSenderId());

                        // Adding date


                        /*Date date = new Date(Long.parseLong(smsDto.getDate()));
                        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                        String formatted = format.format(date);
                        Log.e("Changed Date", new SimpleDateFormat(formatted).toString());
                        System.out.println(formatted);*/
                        String tempDate = smsDto.getDate();
                        finalData.add(tempDate);


                        if(smsDto.getTransactionType().equals("1"))
                        {
                            finalData.add("Debited");
                        }
                        else if(smsDto.getTransactionType().equals("2"))
                        {
                            finalData.add("Credited");
                        }
                        finalData.add(smsDto.getBody());
                        finalData.add(smsDto.getSenderId());
                        inTheTextBox.add(finalData);
                        Log.d("Finally", " "+finalData);
                        Log.d("Final Data List", inTheTextBox.toString());
                    }
                    if (!Character.isDigit(smsDto.getSenderId().charAt(0)))
                        resSms.add(smsDto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("No_matchedValue ", "No_matchedValue :"+smsDto.getBody());
            }
        }
        Log.d("Answer check", inTheTextBox.toString());



        return inTheTextBox;


    }
}