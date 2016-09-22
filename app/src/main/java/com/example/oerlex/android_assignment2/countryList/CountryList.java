package com.example.oerlex.android_assignment2.countryList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.oerlex.android_assignment2.R;

public class CountryList extends AppCompatActivity implements CalendarProviderClient {
    private ListView listView;
    private SimpleCursorAdapter simpleCursorAdapter;
    private CursorLoader cursorLoader;
    public CalendarUtils calendarUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);

        calendarUtils = new CalendarUtils();
        simpleCursorAdapter = new mySimpleCursorAdapter(this,R.layout.activity_country_row,null,EVENTS_LIST_PROJECTION, new int[]{R.id.name,R.id.date},0);

        listView = (ListView) findViewById(R.id.listview_country);
        registerForContextMenu(listView);

        listView.setAdapter(simpleCursorAdapter);
        cursorLoader = (CursorLoader) getLoaderManager().initLoader(LOADER_MANAGER_ID,null,this);
    }

    @Override
    protected void onResume(){
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String color = prefs.getString("list_view_color", "2");
        switch (color) {
            case "1":
                listView.setBackgroundColor(getResources().getColor(R.color.blue));
                break;
            case "2":
                listView.setBackgroundColor(getResources().getColor(R.color.grey));
                break;
            case "3":
                listView.setBackgroundColor(getResources().getColor(R.color.green));
                break;
            default:
                listView.setBackgroundColor(getResources().getColor(R.color.blue));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String name=data.getStringExtra("name");
                String date=data.getStringExtra("date");
                addNewEvent(Integer.parseInt(date),name);
                getLoaderManager().restartLoader(LOADER_MANAGER_ID,null,this);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("KEIN RESULTAT");
            }
        }
    }

    @Override
    public long getMyCountriesCalendarId() {
        long id;
        Cursor cursor = this.getContentResolver()
                .query(
                        CALENDARS_LIST_URI,
                        CALENDARS_LIST_PROJECTION,
                        CALENDARS_LIST_SELECTION,
                        CALENDARS_LIST_SELECTION_ARGS,
                        null);

        boolean hasCalendar = cursor.moveToFirst();
        if(hasCalendar){
            id = cursor.getLong(PROJ_CALENDARS_LIST_ID_INDEX);
        }else{
            ContentResolver contentResolver = this.getContentResolver();
            Uri uri = asSyncAdapter(CALENDARS_LIST_URI,CALENDAR_TITLE, CalendarContract.ACCOUNT_TYPE_LOCAL);

            ContentValues contentValues = new ContentValues();
            contentValues.put(Calendars.ACCOUNT_NAME, ACCOUNT_TITLE);
            contentValues.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
            contentValues.put(Calendars.NAME, CALENDAR_TITLE);
            contentValues.put(Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_TITLE);
            contentValues.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
            contentValues.put(Calendars.OWNER_ACCOUNT, ACCOUNT_TITLE);
            contentValues.put(Calendars.VISIBLE, 1);
            contentValues.put(Calendars.SYNC_EVENTS, 1);

            Uri uriLocation = contentResolver.insert(uri,contentValues);
            id = ContentUris.parseId(uriLocation);
        }
        return id;
    }

    public static Uri asSyncAdapter(Uri uri, String account, String accountType) {
        return uri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, accountType).build();
    }

    @Override
    public void addNewEvent(int year, String country) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Events.DTSTART,CalendarUtils.getEventStart(year));
        contentValues.put(CalendarContract.Events.DTEND,CalendarUtils.getEventEnd(year));
        contentValues.put(CalendarContract.Events.TITLE,country);
        contentValues.put(CalendarContract.Events.CALENDAR_ID, getMyCountriesCalendarId());
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE,CalendarUtils.getTimeZoneId());

        ContentResolver contentResolver = this.getContentResolver();
        contentResolver.insert(EVENTS_LIST_URI,contentValues);
    }

    @Override
    public void updateEvent(int eventId, int year, String country) {
        ContentResolver contentResolver = this.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Events.DTSTART, CalendarUtils.getEventStart(year));
        contentValues.put(CalendarContract.Events.DTEND, CalendarUtils.getEventEnd(year));
        contentValues.put(CalendarContract.Events.TITLE, country);

        Uri uri = ContentUris.withAppendedId(EVENTS_LIST_URI, eventId);
        contentResolver.update(uri,contentValues,null,null);
    }

    @Override
    public void deleteEvent(int eventId) {
        ContentResolver contentResolver = this.getContentResolver();
        Uri uri = ContentUris.withAppendedId(EVENTS_LIST_URI, eventId);
        contentResolver.delete(uri,null,null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(args!=null){
            String sortOrder = args.getString("sortorder");
            System.out.println("DINGDONG "+ sortOrder);
            return new CursorLoader(this,EVENTS_LIST_URI,EVENTS_LIST_PROJECTION, CalendarContract.Events.CALENDAR_ID + "="+ getMyCountriesCalendarId(),null,sortOrder);
        }else{
            return new CursorLoader(this,EVENTS_LIST_URI,EVENTS_LIST_PROJECTION, CalendarContract.Events.CALENDAR_ID + "="+ getMyCountriesCalendarId(),null,null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null){
            simpleCursorAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    public void addCountry(MenuItem item) {
        Intent intent = new Intent(this,CountryInput.class);
        startActivityForResult(intent,1);
    }

    public void openPreferences(MenuItem item) {
        Intent intent = new Intent(this, MyPreferenceActivity.class);
        startActivity(intent);
    }

    public class mySimpleCursorAdapter extends SimpleCursorAdapter {
        private Context mContext;
        private Context appContext;
        private int layout;
        private Cursor cr;
        private final LayoutInflater inflater;

        public mySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int i) {
            super(context,layout,c,from,to,i);
            this.layout=layout;
            this.mContext = context;
            this.inflater= LayoutInflater.from(context);
            this.cr=c;
        }

        @Override
        public View newView (Context context, Cursor cursor, ViewGroup parent) {
            return inflater.inflate(layout, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);
            TextView name =(TextView)view.findViewById(R.id.name);
            TextView date =(TextView)view.findViewById(R.id.date);

            String nameIndex = cursor.getString(PROJ_EVENTS_LIST_TITLE_INDEX);
            long dateIndex = cursor.getLong(PROJ_EVENTS_LIST_DTSTART_INDEX);

            int year = CalendarUtils.getEventYear(dateIndex);

            name.setText(nameIndex);
            date.setText(String.valueOf(year));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Bundle bundle = new Bundle();

        //noinspection SimplifiableIfStatement

        if (id == R.id.ascCountry){
            bundle.putString("sortorder", CalendarContract.Events.TITLE + " ASC");
        }
        if (id == R.id.desCountry){
            bundle.putString("sortorder", CalendarContract.Events.TITLE + " DESC");
        }
        if (id == R.id.ascYear){
            bundle.putString("sortorder", CalendarContract.Events.DTSTART + " ASC");
        }
        if (id == R.id.desYear){
            bundle.putString("sortorder", CalendarContract.Events.DTSTART + " DESC");
        }

        getLoaderManager().restartLoader(LOADER_MANAGER_ID,bundle,this);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.contextmenu, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.euh:
                return true;
            case R.id.ah:
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
