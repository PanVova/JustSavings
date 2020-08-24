package com.example.justsavings.TABS;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.justsavings.R;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class GoalsFragment extends Fragment   {
    FloatingActionButton fab;
    Calendar dateAndTime= Calendar.getInstance();
    Button button;
    TextView text_daily;
    TextView text_daily_sum;
    TextView text_remaining_num;
    TextView text_days;
    TextView text_sum;
    TextView text_saved_sum;
    TextView text_progress;
    ImageView imageView;
    ProgressBar progressBar;

    String goal;
    String saved;
    String remaining;
    String name;
    String sign;
    String date;
    int image;
    int id;
    String goals = "goals5";
    DBHelper dbHelper;
    SQLiteDatabase db;
    String LOG_TAG ="LOG";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        dbHelper = new DBHelper(getActivity());
        db = dbHelper.getWritableDatabase();

         text_remaining_num = view.findViewById(R.id.goal_remaining_num);
         text_daily = view.findViewById(R.id.goal_daily);
         text_daily_sum = view.findViewById(R.id.goal_daily_sum);
         text_sum = view.findViewById(R.id.goal_sum);
         text_saved_sum = view.findViewById(R.id.goal_saved_sum);
         text_days = view.findViewById(R.id.goal_days);
         text_progress = view.findViewById(R.id.goal_progress);
         button = view.findViewById(R.id.goal_button);
         imageView = view.findViewById(R.id.goal_image);
         progressBar = view.findViewById(R.id.goal_progressBar);

         name = getArguments().getString("Name");
         image = getArguments().getInt("Image");
         goal = getArguments().getString("Money");
         saved = getArguments().getString("Saved");
         sign = getArguments().getString("Sign");
         id = getArguments().getInt("ID");
         imageView.setImageResource(image);

        Cursor c = db.query(goals , null, null, null, null, null, null);
        c.moveToFirst();

        int idColIndex = c.getColumnIndex("id");

        do {
            if(id==c.getInt(idColIndex)) break;

        } while (c.moveToNext());

        int dateColIndex = c.getColumnIndex("date");
        date = c.getString(dateColIndex);

        String dateStr =  date;
        c.close();

        update_goal();

        if (dateStr!=null) {

            DateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
            dateAndTime = Calendar.getInstance();
            try {
                dateAndTime.setTime(df.parse(dateStr));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            setInitialDateTime();
            update_time();

        }else {
            text_daily.setVisibility(View.GONE);
            text_daily_sum.setVisibility(View.GONE);
            text_days.setVisibility(View.GONE);
        }


        setHasOptionsMenu(true);

        fab = view.findViewById(R.id.goal_add_button);
        fab.setOnClickListener(view1 -> amount_Dialog());

        button.setOnClickListener(view12 -> setDate(view12));

        return view;
    }

    public void setDate(View v) {
        new DatePickerDialog(getContext(), d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void  update_goal() {
        Cursor c = db.query(goals , null, null, null, null, null, null);
        c.moveToFirst();
        int signColIndex = c.getColumnIndex("sign");

        text_sum.setText(goal+c.getString(signColIndex));
        text_saved_sum.setText(saved+c.getString(signColIndex));

        if (Integer.parseInt(saved) >= Integer.parseInt(goal)) remaining = "0";
        else remaining = String.valueOf(Integer.parseInt(goal) - Integer.parseInt(saved));

        text_remaining_num.setText(String.valueOf(remaining)+c.getString(signColIndex));

        int procent = (int) ((Double.parseDouble(saved) / Double.parseDouble(goal))*100);

        text_progress.setText(String.valueOf(procent)+'%');
        progressBar.setProgress(procent);
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "DB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("create table "+ goals +"("
                    + "id integer primary key autoincrement,"
                    + "image int,"
                    + "name text,"
                    + "sign text,"
                    + "goal_money text,"
                    + "saved_money text" +");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private void setInitialDateTime() {
        String text = DateUtils.formatDateTime(getActivity(), dateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
        button.setText(text);
    }

    private void update_time() {
        Calendar today = Calendar.getInstance();
        Cursor c = db.query(goals, null, null, null, null, null, null);
        c.moveToFirst();

        int signColIndex = c.getColumnIndex("sign");
        sign = c.getString(signColIndex);

        long time  = daysBetween(today,dateAndTime);

        if (time==1)
        {
            text_days.setText(String.valueOf(time)+" Day to go");
            text_daily_sum.setText(remaining+sign); //day
            setInitialDateTime();
        } else{

            text_days.setText(String.valueOf(time)+" Days to go");
            setInitialDateTime();
            double daily =  Double.parseDouble(remaining)/time;
            text_daily_sum.setText(String.valueOf(Math.round(daily * 100.0) / 100.0d)+sign); //day
        }
        c.close();

        text_daily.setVisibility(View.VISIBLE);
        text_daily_sum.setVisibility(View.VISIBLE);
        text_days.setVisibility(View.VISIBLE);
    }

    public void setTime(){

        date = DateUtils.formatDateTime(getActivity(), dateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
        db.update(goals, initialize_data(image,name,goal,saved,sign,date), "ID = " + id,null);

        Cursor c = db.query(goals, null, null, null, null, null, null);
        c.moveToFirst();
        int signColIndex = c.getColumnIndex("sign");

        Calendar today = Calendar.getInstance();
        long time  = daysBetween(today,dateAndTime);

        if(time<=0) Toast.makeText(getActivity(), "Date must be not today or previous dates ", Toast.LENGTH_LONG).show();
        else if (time==1)
        {
            text_days.setText(String.valueOf(time)+" Day to go");
            text_daily_sum.setText(text_remaining_num.getText().toString()); //day
        }else{
            text_days.setText(String.valueOf(time)+" Days to go");
            double daily =  Double.parseDouble(remaining)/time;
            text_daily_sum.setText(String.valueOf(Math.round(daily * 100.0) / 100.0d)+c.getString(signColIndex)); //day
        }
        if(time>0)
        {
            db.update(goals, initialize_data(image,name,goal,saved,c.getString(signColIndex),date), "ID = " + id,null);
            setInitialDateTime();
            text_daily.setVisibility(View.VISIBLE);
            text_daily_sum.setVisibility(View.VISIBLE);
            text_days.setVisibility(View.VISIBLE);
            c.close();
        }
    }

    DatePickerDialog.OnDateSetListener d= (view, year, monthOfYear, dayOfMonth) -> { dateAndTime.set(Calendar.YEAR, year);dateAndTime.set(Calendar.MONTH, monthOfYear);dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setTime();
    };

    public ContentValues initialize_data(int image, String name, String goal_money, String saved_money,String sign,String date) {
        ContentValues cv = new ContentValues();
        cv.put("image", image);
        cv.put("name", name);
        cv.put("sign", sign);
        cv.put("goal_money", goal_money);
        cv.put("saved_money", saved_money);
        cv.put("date",date);

        return cv;
    }

    public long daysBetween(Calendar startDate, Calendar endDate) {
        long start = startDate.getTimeInMillis();
        long end = endDate.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toDays(end - start)+1;
    }

    public void amount_Dialog(){
        final Dialog MyDialog = new Dialog(getActivity());
        MyDialog.setContentView(R.layout.transaction);

        Cursor c = db.query(goals , null, null, null, null, null, null);
        c.moveToFirst();
        int signColIndex = c.getColumnIndex("sign");

        Button cancel = MyDialog.findViewById(R.id.trans_cancel);
        Button ok = MyDialog.findViewById(R.id.trans_ok);
        EditText amount = MyDialog.findViewById(R.id.trans_amount);
        RadioGroup radioGroup = MyDialog.findViewById(R.id.radioGroup);

        amount.setHint("Set amount");
        cancel.setEnabled(true);
        ok.setEnabled(true);

        cancel.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "CANCEL", Toast.LENGTH_LONG).show();
            MyDialog.cancel();
        });
        ok.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "OK", Toast.LENGTH_LONG).show();

            if(amount.getText().toString().isEmpty()  )
            {
                Toast.makeText(getContext(), "Fill the amount", Toast.LENGTH_LONG).show();
                c.close();
                MyDialog.cancel();
            }
            else if(Integer.parseInt(amount.getText().toString())>0)
            {
                if(radioGroup.getCheckedRadioButtonId() ==R.id.radio_red )
                {
                    Log.d(LOG_TAG,"GREEN");
                    add_Amount(amount.getText().toString(),0,c.getString(signColIndex));
                    c.close();

                }else if(radioGroup.getCheckedRadioButtonId() ==R.id.radio_gray )
                {
                    Log.d(LOG_TAG,"RED");
                    add_Amount(amount.getText().toString(),1,c.getString(signColIndex));
                    c.close();
                }
                MyDialog.cancel();

            }else if (Integer.parseInt(amount.getText().toString())<=0){
                Toast.makeText(getActivity(), "Number must be more 0", Toast.LENGTH_LONG).show();
                MyDialog.cancel();
            }

            MyDialog.cancel();
        });
        MyDialog.show();
    }

    void add_Amount(String amount, int number, String sign) {

        if (number==0) //+
        {
            saved = String.valueOf(Integer.parseInt(saved)+Integer.parseInt(amount));

        } else if (number == 1) //-
        {
            int a = Integer.valueOf(amount);
            a = -a;
            if (Integer.parseInt(saved)+a <=0)
            {
                saved="0";
            }else {
                amount = String.valueOf(a);
                saved = String.valueOf(Integer.parseInt(saved)+Integer.parseInt(amount));
            }

        }


        db.update(goals, initialize_data(image,name,goal,saved,sign,date), "ID = " + id,null);

        update_goal();
        if (date !=null) { update_time(); }
    }
}