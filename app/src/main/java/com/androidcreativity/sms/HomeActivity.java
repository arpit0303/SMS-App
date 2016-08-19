package com.androidcreativity.sms;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    List<String> smsAddress, smsBody, smsTime;
    ListView smsList;
    Cursor cursor;
    Toolbar toolbar;
    EditText searchBar;
    ImageView cancelSearch, searchBtn;
    SMSAdapter adapter;
    MenuItem item1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);

        smsAddress = new ArrayList<String>();
        smsBody = new ArrayList<String>();
        smsTime = new ArrayList<String>();
        smsList = (ListView) findViewById(R.id.sms_list);

        searchBar = (EditText) findViewById(R.id.edit_search_bar);
        cancelSearch = (ImageView) findViewById(R.id.cancel_search_btn);
        searchBtn = (ImageView) findViewById(R.id.search_btn);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, NewMessage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        getSMS();

        smsList.setOnItemClickListener(this);
    }

    public void getSMS() {
        cursor = getContentResolver().query(Uri.parse("content://sms/"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                //cursor column Name: address, body
                if (smsAddress.isEmpty() || !(smsAddress.contains(cursor.getString(cursor.getColumnIndexOrThrow("address"))))) {
                    smsAddress.add(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                    smsBody.add(cursor.getString(cursor.getColumnIndexOrThrow("body")));

                    long milliSeconds = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
                    Calendar calendar = Calendar.getInstance();
                    Date currentDate = calendar.getTime();

                    calendar.setTimeInMillis(milliSeconds);
                    DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
                    String finalDateString = formatter.format(calendar.getTime());
                    String currentDateString = formatter.format(currentDate);

                    if (currentDateString.equals(finalDateString)){
                        formatter = new SimpleDateFormat("hh:mm a");
                        finalDateString = formatter.format(calendar.getTime());
                    }else{
                        formatter = new SimpleDateFormat("dd MMM");
                        finalDateString = formatter.format(calendar.getTime());
                    }

                    smsTime.add(finalDateString);
                }
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("Oops!!!")
                    .setMessage("You don't have any SMS.")
                    .setPositiveButton(android.R.string.ok, null);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        cursor.close();

        if (!smsAddress.isEmpty()) {
            adapter = new SMSAdapter(HomeActivity.this, smsAddress, smsBody, smsTime);
            smsList.setAdapter(adapter);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(HomeActivity.this, SMSDetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("smsAddress", smsAddress.get(i));
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home,menu);
        item1 = (MenuItem) menu.findItem(R.id.action_google_drive);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                toolbar.setLogo(null);
                toolbar.setTitle("");
                item.setVisible(false);
                item1.setVisible(false);
                searchBar.setVisibility(View.VISIBLE);
                cancelSearch.setVisibility(View.VISIBLE);
                searchBtn.setVisibility(View.VISIBLE);
                searchBtn.setOnClickListener(this);
                cancelSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        searchBar.setVisibility(View.GONE);
                        cancelSearch.setVisibility(View.GONE);
                        searchBtn.setVisibility(View.GONE);
                        toolbar.setLogo(R.mipmap.ic_launcher);
                        toolbar.setTitle("SMS");
                        item.setVisible(true);
                        item1.setVisible(true);
                        if(getCurrentFocus()!=null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                    }
                });
                break;
            case R.id.action_google_drive:

                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.search_btn:
                String searchText = searchBar.getText().toString();
                Intent intent = new Intent(HomeActivity.this, SearchResultActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("searchText", searchText);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

}
