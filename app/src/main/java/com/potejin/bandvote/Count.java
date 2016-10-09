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

public class Count extends AppCompatActivity {

    static final String DB = "BandVote.db";
    static final int DB_VERSION = 1;
    static final String DBname_allBand = "allBand";
    static final String DBname_entryBand = "entryBand";
    private SQLiteDatabase databaseObject;
    ContentValues contentValObject = new ContentValues();
    TextView result[] = new TextView[30];
    private String result_name[] = new String[30];
    private int result_vote[] = new int[30];
    private int i = 0, j = 0;
    private String debug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);

        LinearLayout view = (LinearLayout)findViewById(R.id.layout);

        DatabaseHelper dbHelperObject = new DatabaseHelper(Count.this);
        databaseObject = dbHelperObject.getWritableDatabase();

        String query_select ="SELECT * FROM entryBand ORDER BY votes DESC";
        Cursor cursor = databaseObject.rawQuery(query_select, null);

        while(cursor.moveToNext()){
            int index_name = cursor.getColumnIndex("name");
            int index_vote = cursor.getColumnIndex("votes");
            String name = cursor.getString(index_name);
            int vote = cursor.getInt(index_vote);
            result_name[i] = name;
            result_vote[i] = vote;
            result[i] = new TextView(this);
            result[i].setText(result_name[i] + "      "  + result_vote[i]);
            result[i].setId(i);
            result[i].setTextSize(20);
            result[i].setTextColor(Color.WHITE);
            view.addView(result[i]);
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
