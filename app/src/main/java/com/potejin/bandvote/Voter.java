package com.potejin.bandvote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

public class Voter extends AppCompatActivity {

    static final String DB = "BandVote.db";
    static final int DB_VERSION = 1;
    static final String DBname_allBand = "allBand";
    static final String DBname_entryBand = "entryBand";
    private SQLiteDatabase databaseObject;
    ContentValues contentValObject = new ContentValues();
    private int i = 0;
    private String debug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter);

        LinearLayout view = (LinearLayout)findViewById(R.id.layout);

        DatabaseHelper dbHelperObject = new DatabaseHelper(Voter.this);
        databaseObject = dbHelperObject.getWritableDatabase();

        String query_select ="SELECT * FROM voter";
        Cursor cursor = databaseObject.rawQuery(query_select, null);

        while(cursor.moveToNext()){
            int index_name = cursor.getColumnIndex("name");
            int index_vote1 = cursor.getColumnIndex("vote1");
            int index_vote2 = cursor.getColumnIndex("vote2");
            int index_vote3 = cursor.getColumnIndex("vote3");
            String name = cursor.getString(index_name);
            String vote1 = cursor.getString(index_vote1);
            String vote2 = cursor.getString(index_vote2);
            String vote3 = cursor.getString(index_vote3);
            TextView result = new TextView(this);
            result.setText(name + "\n" + vote1 + "\n" + vote2 + "\n" + vote3 + "\n");
            result.setId(i);
            result.setTextSize(20);
            result.setTextColor(Color.WHITE);
            view.addView(result);
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
}
