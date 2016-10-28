package com.potejin.bandvote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class Count extends AppCompatActivity implements View.OnClickListener {

    static final String DB = "BandVote.db";
    static final int DB_VERSION = 1;
    static final String DBname_allBand = "allBand";
    static final String DBname_entryBand = "entryBand";
    private SQLiteDatabase databaseObject;
    ContentValues contentValObject = new ContentValues();
    TextView result[] = new TextView[30];
    Cursor cursor;
    private String query;
    private String result_name[] = new String[30];
    private int result_vote[] = new int[30];
    private double result_voterate[] = new double[30];
    private int i = 0, j = 0, sum = 0;
    private String debug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);

        ((Button)findViewById(R.id.csv)).setOnClickListener(this);

        LinearLayout view = (LinearLayout)findViewById(R.id.layout);

        DatabaseHelper dbHelperObject = new DatabaseHelper(Count.this);
        databaseObject = dbHelperObject.getWritableDatabase();

        query = "SELECT * FROM entryBand ORDER BY votes DESC";
        cursor = databaseObject.rawQuery(query, null);

        while(cursor.moveToNext()){
            int index_name = cursor.getColumnIndex("name");
            int index_vote = cursor.getColumnIndex("votes");
            String name = cursor.getString(index_name);
            int vote = cursor.getInt(index_vote);
            result_name[i] = name;
            result_vote[i] = vote;
            sum += result_vote[i];
            i++;
        }

        cursor.close();

        for(j = 0; j < i; j++) {
            result_voterate[j] = (double)result_vote[j] / (double)sum * 100;
            result[j] = new TextView(this);
            result[j].setText(result_name[j] + "      "  + result_vote[j] + "      " + result_voterate[j] + "%");
            result[j].setId(j);
            result[j].setTextSize(20);
            result[j].setTextColor(Color.WHITE);
            view.addView(result[j]);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.csv) {
            EditText name = (EditText) findViewById(R.id.filename);
            String filename = name.getText().toString();
            if (name.length() == 0) {
                Toast.makeText(getApplicationContext(), "ファイル名を入力して下さい", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    String tmpCsvPath = Environment.getExternalStorageDirectory().getPath() + "/" + filename + ".csv";
                    // csvファイル作成
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpCsvPath, false), "UTF-8"));
                    bw.write("バンド名,");
                    bw.write("得票数,");
                    bw.write("得票率");
                    bw.newLine();
                    for (j = 0; j < i; j++) {
                        bw.write(result_name[j] + ",");
                        bw.write(result_vote[j] + ",");
                        bw.write(Double.toString(result_voterate[j]) + "%");
                        bw.newLine();
                    }
                    bw.flush();
                    bw.close();

                    final Properties property = new Properties();
                    property.put("mail.smtp.host", "smtp.gmail.com");
                    property.put("mail.host", "smtp.gmail.com");
                    property.put("mail.smtp.port", "465");
                    property.put("mail.smtp.socketFactory.port", "465");
                    property.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

                    // セッション
                    final Session session = Session.getInstance(property, new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("e1211", "laughmaker09");
                        }
                    });

                    MimeMessage mimeMsg = new MimeMessage(session);

                    mimeMsg.setSubject("高専祭バンド投票結果", "utf-8");
                    mimeMsg.setFrom(new InternetAddress("e1211@s.akashi.ac.jp"));
                    mimeMsg.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress("absolution09bloom@gmail.com"));

                    // 添付ファイル
                    final MimeBodyPart txtPart = new MimeBodyPart();
                    txtPart.setText("ボ", "utf-8");

                    final MimeBodyPart filePart = new MimeBodyPart();
                    File file = new File(tmpCsvPath);
                    FileDataSource fds = new FileDataSource(file);
                    DataHandler data = new DataHandler(fds);
                    filePart.setDataHandler(data);
                    filePart.setFileName(MimeUtility.encodeWord(filename));

                    final Multipart mp = new MimeMultipart();
                    mp.addBodyPart(txtPart);
                    mp.addBodyPart(filePart);
                    mimeMsg.setContent(mp);

                    // メール送信する。
                    final Transport transport = session.getTransport("smtp");
                    transport.connect("e1211", "laughmaker09");
                    transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+DBname_allBand);
            db.execSQL("DROP TABLE IF EXISTS "+DBname_entryBand);
            onCreate(db);
        }
    }
}
