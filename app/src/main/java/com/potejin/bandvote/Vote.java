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

public class Vote extends AppCompatActivity implements View.OnClickListener {

    static final String DB = "BandVote.db";
    static final int DB_VERSION = 1;
    static final String DBname_allBand = "allBand";
    static final String DBname_entryBand = "entryBand";
    static final String DBname_voter = "voter";
    private SQLiteDatabase databaseObject;
    ContentValues contentValObject = new ContentValues();
    ContentValues contentValObject2 = new ContentValues();
    Cursor cursor;
    private CheckBox checkbox[] = new CheckBox[30];
    private CheckBox checked[] = new CheckBox[30];
    private String result_str[] = new String[30];
    private int i = 0, j = 0, count = 0, votes = 0, vote_num = 1;
    private String debug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        LinearLayout view = (LinearLayout)findViewById(R.id.layout);
        ((Button)findViewById(R.id.vote)).setOnClickListener(this);

        DatabaseHelper dbHelperObject = new DatabaseHelper(Vote.this);
        databaseObject = dbHelperObject.getWritableDatabase();

        String query_select ="SELECT * FROM entryBand";
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
        if (v.getId() == R.id.vote) {
            EditText voter = (EditText)findViewById(R.id.voter);
            if (voter.length() == 0) {
                Toast.makeText(getApplicationContext(), "投票者名を入力して下さい", Toast.LENGTH_SHORT).show();
            } else {
                String votername = voter.getText().toString();
                cursor = databaseObject.rawQuery("SELECT * FROM " + DBname_voter + " WHERE name = " + "'"+votername+"'", null);
                if (cursor.moveToFirst()) {
                    Toast.makeText(getApplicationContext(), "投票者が既に登録されています", Toast.LENGTH_SHORT).show();
                    cursor.close();
                } else {
                    cursor.close();
                    for (j = 0; j < i; j++) {
                        checked[j] = (CheckBox) findViewById(j);
                        if (checked[j].isChecked()) {
                            count++;
                        }
                    }
                    if (count == 3) {
                        contentValObject2.put("name", votername);
                        for (j = 0; j < i; j++) {
                            checked[j] = (CheckBox) findViewById(j);
                            if (checked[j].isChecked()) {
                                String bandname = checked[j].getText().toString();
                                contentValObject2.put("vote" + vote_num++, bandname);
                                cursor = databaseObject.rawQuery("SELECT * FROM " + DBname_entryBand + " WHERE name = " + "'"+bandname+"'", null);
                                cursor.moveToFirst();
                                int index_vote = cursor.getColumnIndex("votes");
                                votes = cursor.getInt(index_vote);
                                contentValObject.put("votes", ++votes);
                                databaseObject.update(DBname_entryBand, contentValObject, "name = " + "'"+bandname+"'", null);
                                cursor.close();
                            }
                        }
                        databaseObject.insert(DBname_voter, null, contentValObject2);
                        Toast.makeText(getApplicationContext(), "投票完了", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "バンドを3つ選んで下さい", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            vote_num = 1;
            count = 0;
        }
    }
}
