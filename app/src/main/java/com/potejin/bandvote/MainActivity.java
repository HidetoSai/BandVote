package com.potejin.bandvote;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    static final String DB = "BandVote.db";
    static final int DB_VERSION = 1;
    static final String DBname_allBand = "allBand";
    static final String DBname_entryBand = "entryBand";
    static final String DBname_voter = "voter";
    private SQLiteDatabase databaseObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper dbHelperObject = new DatabaseHelper(MainActivity.this);
        databaseObject = dbHelperObject.getWritableDatabase();
        databaseObject.execSQL("CREATE TABLE IF NOT EXISTS " + DBname_allBand + "(id integer primary key autoincrement, name text not null)");
        databaseObject.execSQL("CREATE TABLE IF NOT EXISTS " + DBname_entryBand + "(id integer primary key autoincrement, name text not null, votes integer default 0)");
        databaseObject.execSQL("CREATE TABLE IF NOT EXISTS " + DBname_voter + "(id integer primary key autoincrement, name text not null, vote1 text not null, vote2 text not null, vote3 text not null)");

        ((Button)findViewById(R.id.entry)).setOnClickListener(this);
        ((Button)findViewById(R.id.vote)).setOnClickListener(this);
        ((Button)findViewById(R.id.count)).setOnClickListener(this);
        ((Button)findViewById(R.id.voter)).setOnClickListener(this);
        ((Button)findViewById(R.id.option)).setOnClickListener(this);
    }

    @Override
    public  void  onClick(View v) {
        if (v.getId() == R.id.entry) {
            onClickEntry();
        } else if (v.getId() == R.id.vote) {
            onClickVote();
        } else if (v.getId() == R.id.count) {
            onClickCount();
        } else if (v.getId() == R.id.voter) {
            onClickVoter();
        } else if (v.getId() == R.id.option) {
            onClickOption();
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DB, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + DBname_allBand + "(id integer primary key autoincrement, name text not null)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + DBname_entryBand + "(id integer primary key autoincrement, name text not null, votes integer default 0)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + DBname_voter + "(id integer primary key autoincrement, name text not null, vote1 text not null, vote2 text not null, vote3 text not null)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+DBname_allBand);
            db.execSQL("DROP TABLE IF EXISTS "+DBname_entryBand);
            db.execSQL("DROP TABLE IF EXISTS "+DBname_voter);
            onCreate(db);
        }
    }

    private void onClickEntry() {
        Intent entry = new Intent(MainActivity.this, Entry.class);
        startActivity(entry);
    }

    private void onClickVote() {
        Intent vote = new Intent(MainActivity.this, Vote.class);
        startActivity(vote);
    }

    private void onClickCount() {
        Intent count = new Intent(MainActivity.this, Count.class);
        startActivity(count);
    }

    private void onClickVoter() {
        Intent voter = new Intent(MainActivity.this, Voter.class);
        startActivity(voter);
    }

    private void onClickOption() {
        databaseObject.execSQL("DELETE FROM " + DBname_allBand);
        databaseObject.execSQL("DELETE FROM " + DBname_entryBand);
        databaseObject.execSQL("DELETE FROM " + DBname_voter);
        Toast.makeText(getApplicationContext(), "データベース削除完了", Toast.LENGTH_SHORT).show();
    }
}
