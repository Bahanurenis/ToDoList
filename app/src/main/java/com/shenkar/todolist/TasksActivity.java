package com.shenkar.todolist;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.shenkar.Alarm.AlarmController;
import com.shenkar.GeoLocation.GeofenceErrorMessages;
import com.shenkar.GeoLocation.GeofenceTransitionsIntentService;
import com.shenkar.common.AppConst;
import com.shenkar.common.GeoPoint;
import com.shenkar.common.OnDataSourceChangeListener;
import com.shenkar.common.Task;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TasksActivity extends ActionBarActivity implements OnDataSourceChangeListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    static final int GET_TASK_REQUEST = 2;
    static final int EDIT_TASK_REQUEST = 3;
    private MainController mainController;
    private AlarmController alarmController;
    private TaskListBaseAdapter adapter;
    private String categoryName;
    private static final String TAG = "TasksActivity";
    private GoogleApiClient mGoogleApiClient;
    private static List<Geofence> mGeoFenceList;
    private static boolean hasGeoFence;
    private static boolean edited;
    private PendingIntent mGeofencePendingIntent;
    private int geoFenceId;
    private String geoFenceLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#4285f4"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        mainController = new MainController(this);
        alarmController = new AlarmController(this);
        mainController.registerOnDataSourceChanged(this);
        hasGeoFence = false;
        edited = false;
        ListView notCompletedTaskLv = (ListView) findViewById(R.id.taskListViewNotCompleted);
        Intent intent = getIntent(); // getting the category that passed from MainActivity
        categoryName = intent.getStringExtra(AppConst.CATEGORY_TO_ADD_TASK);
        getSupportActionBar().setTitle(categoryName);
        if (notCompletedTaskLv != null) {
            adapter = new TaskListBaseAdapter(this,mainController.getNotCompletedTasks(categoryName));
            notCompletedTaskLv.setAdapter(adapter);
            notCompletedTaskLv.setOnTouchListener(new OnSwipeTouchListener(this,notCompletedTaskLv){
                public void onSwipeLeft(int position) { //delete task
                    Task t = (Task) adapter.getItem(position);
                    mainController.taskDeleteConfirmation(t); //controller will handle what happens next
                }
                public void onSwipeRight(int position) { // mark task as done
                    Task t = (Task) adapter.getItem(position);
                    mainController.taskCompleteConfirmation(t);
                }

            });

            notCompletedTaskLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Task t = (Task) adapter.getItem(position);
                    Intent editIntent = new Intent(getBaseContext(),EditTaskActivity.class);
                    editIntent.putExtra(AppConst.TASK_ID_FOR_INTENT,t.getId());
                    editIntent.putExtra(AppConst.TASK_NAME_FOR_INTENT,t.getTaskName());
                    editIntent.putExtra(AppConst.TASK_NOTE_FOR_INTENT,t.getTaskNote());
                    editIntent.putExtra(AppConst.TASK_PRIORITY_FOR_INTENT,t.getPriority());
                    editIntent.putExtra(AppConst.WHEN_ALARM,t.getTimeToAlarm());
                    editIntent.putExtra(AppConst.WHERE_ALARM,t.getTaskLocation());
                    startActivityForResult(editIntent, EDIT_TASK_REQUEST);
                }
            });

        }
        mGeoFenceList = new ArrayList<>(); // Empty list for storing geofences.
        buildGoogleApiClient();
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            Toast.makeText(
                    this,"GeoFence Added",Toast.LENGTH_SHORT).show();
        }
        else {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_add:
                Intent addIntent = new Intent(this, AddTaskActivity.class);
                addIntent.putExtra(AppConst.CATEGORY_TO_ADD_TASK,categoryName);
                startActivityForResult(addIntent, GET_TASK_REQUEST);
                return true;
            case R.id.action_see_completed:
                Intent completedIntent = new Intent(this,CompletedTasksActivity.class);
                startActivity(completedIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == GET_TASK_REQUEST) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                String taskName = extras.getString(AppConst.TASK_NAME_FOR_INTENT);
                String taskNote = extras.getString(AppConst.TASK_NOTE_FOR_INTENT);
                int taskPriority = extras.getInt(AppConst.TASK_PRIORITY_FOR_INTENT);
                long timeToAlarm = extras.getLong(AppConst.WHEN_ALARM);
                String taskLocation = extras.getString(AppConst.WHERE_ALARM);
                Task t = new Task();
                t.setTaskName(taskName);
                t.setTaskNote(taskNote);
                t.setPriority(taskPriority);
                t.setCategory(categoryName);
                t.setCompleted(0); //not completed when creating it
                t.setTimeToAlarm(timeToAlarm);
                t.setTaskLocation(taskLocation);
                int id = mainController.addTask(t,categoryName);
                t.setId(id);
                geoFenceId = t.getId();
                geoFenceLocation = t.getTaskLocation();
                if (t.getTimeToAlarm() > 0 ) {
                    alarmController.CreateAlarm(t.getTaskName(), t.getTimeToAlarm(), t.getId());
                }
                if (geoFenceLocation != null && !geoFenceLocation.equals("")) {
                    hasGeoFence = true;
                }
            }
        }
        else if (resultCode == RESULT_OK && requestCode == EDIT_TASK_REQUEST) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                int id = extras.getInt(AppConst.TASK_ID_FOR_INTENT);
                String newTaskName = extras.getString(AppConst.TASK_NAME_FOR_INTENT);
                String newTaskNote = extras.getString(AppConst.TASK_NOTE_FOR_INTENT);
                int newTaskPriority = extras.getInt(AppConst.TASK_PRIORITY_FOR_INTENT);
                long newTimeToAlarm = extras.getLong(AppConst.WHEN_ALARM);
                String newTaskLocation = extras.getString(AppConst.WHERE_ALARM);
                Task t = new Task();
                t.setId(id);
                t.setTaskName(newTaskName);
                t.setTaskNote(newTaskNote);
                t.setPriority(newTaskPriority);
                t.setCategory(categoryName);
                t.setCompleted(0); //not completed when editing it
                t.setTimeToAlarm(newTimeToAlarm);
                t.setTaskLocation(newTaskLocation);
                geoFenceLocation = newTaskLocation;
                mainController.editTask(t);
                if (t.getTimeToAlarm() > 0) {
                    alarmController.cancelAlarm(t.getId());
                    alarmController.CreateAlarm(t.getTaskName(),t.getTimeToAlarm(),t.getId());
                }
                if (geoFenceLocation != null && !geoFenceLocation.equals("")) {
                    edited = true;
                    hasGeoFence = true;
                }
            }
        }
    }

    @Override
    public void DataSourceChanged() {
        if (adapter != null) {
            adapter.UpdateDataSource(mainController.getNotCompletedTasks(categoryName));
            adapter.notifyDataSetChanged();
        }
    }

    public void activateGeoFence() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeoFencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            securityException.getMessage();
        }
    }

    public void cancelGeoFence() {
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                // This is the same pending intent that was used in addGeofences().
                getGeoFencePendingIntent()
        ).setResultCallback(this); // Result processed in onResult().
    }


    //AsyncTask cause the GeoCoder object
    private class GetLatLong extends AsyncTask<String,Void,GeoPoint> {
        @Override
        protected GeoPoint doInBackground(String... params) {
            Geocoder geocoder = new Geocoder(TasksActivity.this);
            List<Address> address;
            try {
                address = geocoder.getFromLocationName(params[0], 1);
                if (address == null) {
                    return null;
                }
                Address location = address.get(0);
                GeoPoint p1 = new GeoPoint();
                p1.setLat(location.getLatitude());
                p1.setLng(location.getLongitude());
                return p1;
            }
            catch (IOException e) {
                e.getMessage();
                return null;
            }
        }
    }

    public void addGeoFence(int id, String address) {
        GeoPoint geoPoint = null;
        try {
            geoPoint = new GetLatLong().execute(address).get();
        }
        catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
        if (geoPoint != null) {
            geoPoint.setId(id);
            mGeoFenceList.add(new Geofence.Builder()
                    .setRequestId(String.valueOf(geoPoint.getId()))
                    .setCircularRegion(geoPoint.getLat(),geoPoint.getLng(), AppConst.GEOFENCE_RADIUS_IN_METERS)
                    .setExpirationDuration(AppConst.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build());
            activateGeoFence();
        }
        else {
            Log.e(TAG,"geoPoint is null");
        }
    }

    private PendingIntent getGeoFencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeoFenceList);
        return builder.build();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        Log.d(TAG,"onStart");
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        Log.d(TAG,"onStop");
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
        if (hasGeoFence && !edited) { //create geofence for the first time
            addGeoFence(geoFenceId, geoFenceLocation);
            hasGeoFence = false;
        }
        else if (edited && hasGeoFence){ //edit geofence
            cancelGeoFence();
            addGeoFence(geoFenceId, geoFenceLocation);
            edited = false;
            hasGeoFence = false;
        }
        else if (edited && geoFenceLocation.equals("")){ //cancel geofence
            cancelGeoFence();
            edited = false;
        }
        Log.d(TAG,"onConnected");
    }


}
