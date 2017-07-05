package com.example.marc.sms_test_002;

import java.util.ArrayList;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import java.text.SimpleDateFormat;

public class MainActivity extends Activity {

    private static final String LogTag = "android_marc_log: ";
    //private static final String INBOX_URI = "content://sms/inbox";

    private static MainActivity activity;
    private ArrayList<String> smsList = new ArrayList<String>();
    private ListView mListView;
    private ArrayAdapter<String> adapter;
    Boolean permission = false;
    public static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 99;
    int NumMessages = 0;
    public static MainActivity instance() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LogTag, "starting");
        setContentView(R.layout.activity_main);
        Log.v(LogTag, "001");

        mListView = (ListView) findViewById(R.id.list11);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(MyItemClickListener);
        Log.v(LogTag, "002");

        readSMS();
    }

    @Override
    public void onStart() {
        super.onStart();
        activity = this;
    }

    public void readSMS() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            permission = checkSmsPermission();
        if (permission) {
            Log.v(LogTag, "003");
            ContentResolver contentResolver = getContentResolver();
            Log.v(LogTag, "004");
            Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
            Log.v(LogTag, "005");

            int senderIndex = smsInboxCursor.getColumnIndex("address");
            int messageIndex = smsInboxCursor.getColumnIndex("body");
            int smsDateIndex = smsInboxCursor.getColumnIndex("date");
            long dateLong;

            if (messageIndex < 0 || !smsInboxCursor.moveToFirst()) return;
            adapter.clear();
            do {
                String sender = smsInboxCursor.getString(senderIndex);
                String message = smsInboxCursor.getString(messageIndex);
                String smsDate = smsInboxCursor.getString(smsDateIndex);
                //TextView tv2 = (TextView) findViewById(R.id.textView2);
                //tv2.setText(Integer.toString(senderIndex));
                //TextView tv3 = (TextView) findViewById(R.id.textView3);
                //tv3.setText(Integer.toString(messageIndex));
                //String formattedText = String.format(getResources().getString(R.string.sms_message), sender, message);
                long itemLong = Long.parseLong(smsDate);
                java.util.Date d = new java.util.Date(itemLong);
                String itemDateStr = new SimpleDateFormat("dd-MMM HH:mm:ss").format(d);
                String formattedText = String.format("%s<br/>%s<br/>%s", sender, itemDateStr, message);
                adapter.add(Html.fromHtml(formattedText).toString());
                NumMessages += 1;
            } while (smsInboxCursor.moveToNext());
            TextView tv1 = (TextView) findViewById(R.id.textView1);
            tv1.setText(Integer.toString(NumMessages) + " SMS messages");

        }
    }

    public boolean checkSmsPermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECEIVE_SMS)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
            return false;
        } else {
            return true;
        }
    }

    public void updateList(final String newSms) {
        adapter.insert(newSms, 0);
        adapter.notifyDataSetChanged();
    }

    private OnItemClickListener MyItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            try {
                Toast.makeText(getApplicationContext(), adapter.getItem(pos), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };
}
