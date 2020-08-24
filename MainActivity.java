package com.example.justsavings.MAIN_ACTIVITY;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.justsavings.R;
import com.example.justsavings.TABS.Main3Activity;
import java.util.ArrayList;
import java.util.Random;
import static android.support.v7.widget.OrientationHelper.VERTICAL;

public class MainActivity extends AppCompatActivity implements GoalRecyclerViewAdapter.ItemClickListener {

    GoalRecyclerViewAdapter adapter;
    String LOG_TAG = "LOG";
    ArrayList<Goal> notes_list = new ArrayList<>();
    DBHelper dbHelper;
    SQLiteDatabase db;
    int STATUS = 0;
    int DIALOG_STATUS = 0;
    FloatingActionButton fab;
    String goals = "goals5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.app_name);

        dbHelper = new DBHelper(MainActivity.this);
        db = dbHelper.getWritableDatabase();
        //db.execSQL("DELETE FROM goals5;");
        view_Data(db);
        STATUS=1;

        RecyclerView recyclerView = findViewById(R.id.main_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GoalRecyclerViewAdapter(this, notes_list);
        adapter.setClickListener(this);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);
        recyclerView.setLongClickable(true);

        fab = findViewById(R.id.main_fab);
        fab.setOnClickListener(view -> {DIALOG_STATUS=1; create_edit_Dialog(-1); } );

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });
    }

    public int take_photo_randomly() {
        final Random random = new Random();
        int images[] = {R.drawable.android, R.drawable.androids,R.drawable.banana,R.drawable.cash,R.drawable.crown,R.drawable.favorite,R.drawable.footprint,
                R.drawable.wallpaper,R.drawable.handicrafts,R.drawable.image,R.drawable.king,R.drawable.light_bulb,R.drawable.marketing,R.drawable.photo_camera,
                R.drawable.picture,R.drawable.picture1,R.drawable.start_up};
        int pos = random.nextInt(images.length);
        return images[pos];
    }

    public ContentValues initialize_data(int image,String name,String goal_money,String saved_money,String sign) {
        ContentValues cv = new ContentValues();
        cv.put("image", image);
        cv.put("name", name);
        cv.put("sign", sign);
        cv.put("goal_money", goal_money);
        cv.put("saved_money", saved_money);
        return cv;
    }

    public void view_Data(SQLiteDatabase db) {
        Log.d(LOG_TAG, "--- Rows in mytable: ---");
        Cursor c = db.query(goals , null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int imageColIndex = c.getColumnIndex("image");
            int nameColIndex = c.getColumnIndex("name");
            int signColIndex = c.getColumnIndex("sign");
            int goal_moneyColIndex = c.getColumnIndex("goal_money");
            int saved_moneyColIndex = c.getColumnIndex("saved_money");
            int dateColIndex = c.getColumnIndex("date");

            do {
                Log.d(LOG_TAG,
                        "ID = " + c.getInt(idColIndex) +
                                ", image = " + c.getInt(imageColIndex) +
                                ", name = " + c.getString(nameColIndex)+
                                ", sign = " + c.getString(signColIndex) +
                                ", goal_money = " + c.getInt(goal_moneyColIndex) +
                                ", saved_money = " + c.getString(saved_moneyColIndex)+
                                ", date = " + c.getString(dateColIndex));

                if(STATUS ==0) {
                    notes_list.add(new Goal(c.getInt(idColIndex),c.getString(nameColIndex), c.getString(goal_moneyColIndex), c.getInt(imageColIndex) ,c.getString(saved_moneyColIndex),c.getString(signColIndex)));
                }

            } while (c.moveToNext());
        } else  Log.d(LOG_TAG, "0 rows");
        c.close();
    }

    public void create_edit_Dialog(int position){
        final Dialog MyDialog = new Dialog(MainActivity.this);
        MyDialog.setContentView(R.layout.create_edit_goal);

        Button cancel = MyDialog.findViewById(R.id.ceg_cancel);
        Button ok = MyDialog.findViewById(R.id.ceg_ok);
        ImageView imageView = MyDialog.findViewById(R.id.ceg_image);
        TextView title = MyDialog.findViewById(R.id.ceg_title);
        EditText name_of_note = MyDialog.findViewById(R.id.ceg_name);
        EditText amount = MyDialog.findViewById(R.id.ceg_amount);
        int photo_id = take_photo_randomly();

        cancel.setEnabled(true);
        ok.setEnabled(true);

        switch (DIALOG_STATUS)
        {
            case 1:
                name_of_note.setHint("Goal name");
                amount.setHint("Goal amount");
                imageView.setImageResource(photo_id);
                title.setText("Create Goal");

                break;
            case 2:
                name_of_note.setText(notes_list.get(position).name);
                amount.setText(notes_list.get(position).required_money);
                imageView.setImageResource(notes_list.get(position).url);
                title.setText("Edit Goal");
                break;
        }

        cancel.setOnClickListener(v -> MyDialog.cancel());
        ok.setOnClickListener(v -> {
            if(name_of_note.getText().toString().isEmpty() || amount.getText().toString().isEmpty() )
            {
                Toast.makeText(getApplicationContext(), "Fill all the text", Toast.LENGTH_LONG).show();
                MyDialog.cancel();
            }else if(Integer.parseInt(amount.getText().toString())>0)
            {
                switch (DIALOG_STATUS)
                {
                    case 1:
                        add_Goal(name_of_note.getText().toString(),amount.getText().toString(),photo_id);
                        MyDialog.cancel();
                        break;
                    case 2:
                        Cursor c = db.query(goals, null, null, null, null, null, null);
                        int signColIndex;
                        int savedColIndex;
                        int imageColIndex;

                        c.moveToPosition(position);

                        signColIndex = c.getColumnIndex("sign");
                        savedColIndex = c.getColumnIndex("saved_money");
                        imageColIndex =c.getColumnIndex("image");

                        notes_list.set(position, new Goal(notes_list.get(position).id,name_of_note.getText().toString(), amount.getText().toString(),c.getInt(imageColIndex),c.getString(savedColIndex),c.getString(signColIndex)));
                        db.update(goals, initialize_data(c.getInt(imageColIndex),name_of_note.getText().toString(),amount.getText().toString(), notes_list.get(position).amount_collected, notes_list.get(position).currency_sign), "ID = " + notes_list.get(position).id,null);
                        adapter.notifyItemChanged(position);
                        c.close();
                        MyDialog.cancel();
                        break;
                }
            }else{
                Toast.makeText(getApplicationContext(), "Number must be more 0", Toast.LENGTH_LONG).show();
                MyDialog.cancel();
            }
        });
        MyDialog.show();
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(MainActivity.this, Main3Activity.class);
        intent.putExtra("ID",  notes_list.get(position).id);
        intent.putExtra("Name",  notes_list.get(position).name);
        intent.putExtra("Image", notes_list.get(position).url);
        intent.putExtra("Required", notes_list.get(position).required_money);
        intent.putExtra("Saved", notes_list.get(position).amount_collected);
        intent.putExtra("Sign", notes_list.get(position).currency_sign);
        db.close();
        startActivity(intent);
    }

    @Override
    public void onLongClick(View view, int position) {
        ed_Goal(position);
    }

    class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context) { super(context, "DB", null, 1); }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table "+ goals +"("
                    + "id integer primary key autoincrement,"
                    + "image int,"
                    + "name text,"
                    + "sign text,"
                    + "goal_money text,"
                    + "date text,"
                    + "saved_money text" +");");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }

    public void ed_Goal(int position){
        final Dialog MyDialog = new Dialog(MainActivity.this);
        MyDialog.setContentView(R.layout.list_row_dialog);

        Button edit = MyDialog.findViewById(R.id.list_row_dialog_edit);
        Button delete = MyDialog.findViewById(R.id.list_row_dialog_delete);
        TextView title = MyDialog.findViewById(R.id.list_row_dialog_title);

        edit.setEnabled(true);
        delete.setEnabled(true);
        title.setText(notes_list.get(position).name);

        edit.setOnClickListener(v -> {
            MyDialog.cancel();
            DIALOG_STATUS=2;
            create_edit_Dialog(position);

    });
        delete.setOnClickListener(v -> {
            db.delete(goals, "ID = " + notes_list.get(position).id, null);
            notes_list.remove(position);
            adapter.notifyItemRemoved(position);
            MyDialog.cancel();
        });
        MyDialog.show();
    } //Edit_Delete Goal

    public void add_Goal(String name , String amount, int photo_id) {
        Cursor c = db.query(goals , null, null, null, null, null, null);
        int signColIndex;

        if(c.moveToFirst())
        {
            signColIndex = c.getColumnIndex("sign");
            int rowID = (int) db.insert(goals, null, initialize_data(photo_id,name,amount,"0",c.getString(signColIndex)));
            notes_list.add(new Goal(rowID,name, amount, photo_id,"0",c.getString(signColIndex)));
        }else {
            int rowID = (int) db.insert(goals, null, initialize_data(photo_id,name,amount,"0","$"));
            notes_list.add(new Goal(rowID,name, amount, photo_id,"0","$"));
        }

        c.close();
        adapter.notifyDataSetChanged();

        view_Data(db);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { getMenuInflater().inflate(R.menu.menu_main, menu);  return true; }

    public void change_sign(){
        final Dialog MyDialog = new Dialog(MainActivity.this);
        MyDialog.setContentView(R.layout.sign);

        Button cancel = MyDialog.findViewById(R.id.sign_cancel);
        Button ok = MyDialog.findViewById(R.id.sign_ok);
        Spinner spinner = MyDialog.findViewById(R.id.sign_spinner);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(MyDialog.getContext(), R.array.currency_signs, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);

        cancel.setEnabled(true);
        ok.setEnabled(true);


        cancel.setOnClickListener(v -> MyDialog.cancel());
        ok.setOnClickListener(v -> {

            String check = spinner.getSelectedItem().toString();
            db.execSQL("UPDATE "+goals+" SET sign =  " +"'"+check+"' "); ///UPDATE
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivityForResult(myIntent, 0);
            MyDialog.cancel();

        });
        MyDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            change_sign();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}