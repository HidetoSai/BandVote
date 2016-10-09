package com.potejin.bandvote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Entry extends AppCompatActivity implements View.OnClickListener {

    static final String DB = "BandVote.db";
    static final int DB_VERSION = 1;
    static final String DBname_allBand = "allBand";
    static final String DBname_entryBand = "entryBand";
    private SQLiteDatabase databaseObject;
    ContentValues contentValObject = new ContentValues();
    private CheckBox checkbox[] = new CheckBox[30];
    private CheckBox checked[] = new CheckBox[30];
    private String result_str[] = new String[30];
    private int i = 0, j = 0;
    private String debug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        LinearLayout view = (LinearLayout)findViewById(R.id.layout);
        ((Button)findViewById(R.id.newentry)).setOnClickListener(this);
        ((Button)findViewById(R.id.oldentry)).setOnClickListener(this);

        DatabaseHelper dbHelperObject = new DatabaseHelper(Entry.this);
        databaseObject = dbHelperObject.getWritableDatabase();

        String query_select ="SELECT * FROM allBand";
        Cursor cursor = databaseObject.rawQuery(query_select, null);

        while(cursor.moveToNext()){
            int index_name = cursor.getColumnIndex("name");
            String name = cursor.getString(index_name);
            result_str[i] = name;
            checkbox[i] = new CheckBox(this);
            checkbox[i].setText(result_str[i]);
            checkbox[i].setId(i);
            checkbox[i].setTextSize(20);
            view.addView(checkbox[i]);
            i++;
        }

        cursor.close();
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DB, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + DBname_allBand + "(id integer primary key autoincrement, name text not null)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + DBname_entryBand + "(id integer primary key autoincrement, name text not null, votes integer default 0)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+DBname_allBand);
            db.execSQL("DROP TABLE IF EXISTS "+DBname_entryBand);
            onCreate(db);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.newentry) {
            EditText name = (EditText)findViewById(R.id.bandname);
            String bandname = name.getText().toString();
            if (name.length() == 0) {
                Toast.makeText(getApplicationContext(), "バンド名を入力して下さい", Toast.LENGTH_SHORT).show();
            } else {
                contentValObject.put("name", bandname);
                Cursor cursor = databaseObject.rawQuery("SELECT * FROM " + DBname_entryBand + " WHERE name = " + "'"+bandname+"'", null);
                if (!cursor.moveToFirst()) {
                    databaseObject.insert(DBname_allBand, null, contentValObject);
                    Toast.makeText(getApplicationContext(), "バンドの新規登録完了", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "既に登録されています", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            }
        } else if (v.getId() == R.id.oldentry) {
            for(j = 0; j < i; j++) {
                checked[j] = (CheckBox)findViewById(j);
                if(checked[j].isChecked()) {
                    String text = checked[j].getText().toString();
                    contentValObject.put("name", text);
                    Cursor cursor = databaseObject.rawQuery("SELECT * FROM " + DBname_entryBand + " WHERE name = " + "'"+text+"'", null);
                    if (!cursor.moveToNext()) {
                        databaseObject.insert(DBname_entryBand, null, contentValObject);
                    }
                    cursor.close();
                }
            }
            Toast.makeText(getApplicationContext(), "出演バンド登録完了", Toast.LENGTH_SHORT).show();
        }
    }
}
