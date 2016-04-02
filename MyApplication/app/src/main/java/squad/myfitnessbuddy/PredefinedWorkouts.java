package squad.myfitnessbuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class PredefinedWorkouts extends AppCompatActivity {

    ListView workoutLV, previewLV;
    SQLiteDatabase exerciseDB;
    SharedPreferences sharedPreference;
    LinearLayout previewPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.predefined_workouts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);

        //Customize the Actionbar color to 'Black' and text to 'Setup Page'
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Predefined Workouts");

        try {
            exerciseDB = this.openOrCreateDatabase("mfbDatabase.db", MODE_PRIVATE, null);
            //open or create database
            exerciseDB.execSQL(ConstantValues.cCREATE_OR_OPEN_SAVED_WORKOUTS_DATABASE_SQL);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        workoutLV = (ListView) findViewById(R.id.workoutLV);


        populateWorkouts();
        
        sharedPreference = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
    }
    //Method to populate list of predefined workouts
    protected void populateWorkouts() {
        //create array to store Predefined Workout names
        final ArrayList<String> predefinedWorkoutList = new ArrayList<>();

        try {

            //set cursor to start traversing search
            //raw query is SQL code (Select everything from table "exercises" and order them by name)
            Cursor c_v2 = exerciseDB.rawQuery("SELECT * FROM predefinedWorkouts", null);
            //get items from "name" column of table
            int idxName = c_v2.getColumnIndex("name");
            //move cursor to top of list (table)
            c_v2.moveToFirst();

            while (c_v2 != null) {
                predefinedWorkoutList.add(c_v2.getString(idxName));
                c_v2.moveToNext();
            }
            //close cursor to avoid memory leak
            c_v2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //adapter to get list into ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, predefinedWorkoutList);
        workoutLV.setAdapter(adapter);
        //only one workout can be checked at a time
        workoutLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    //Code to save the chosen predefined workout to the saved workouts repository
   public void previewPredefinedWorkoutButton(View view) {
       String selectedWorkoutName = getCheckedItemName(workoutLV);

       if (!selectedWorkoutName.equals("")) {

           SharedPreferences.Editor editor = sharedPreference.edit();

           //Use the editor to store the name of the current workout to preview in the SharedPreference
           editor.putString(ConstantValues.cSP_PREVIEW_WORKOUT, selectedWorkoutName);
           editor.putBoolean(ConstantValues.cSP_PREVIEW_FOR_PREDEFINED,true);
           editor.apply();

           //open preview page
           Intent previewWorkout = new Intent(getApplicationContext(), PreviewWorkout.class);
           startActivity(previewWorkout);
       }
       else{
           //if nothing selected, display error message
           Toast.makeText(getApplicationContext(),"Please select a workout to preview.",Toast.LENGTH_SHORT).show();
       }

   }

    //returns the name of the item that is checked in the list.
    //returns empty string if it noting is checked
    public String getCheckedItemName(ListView listView)
    {
        String selectedItem = "";

        SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();

        //iterate through list
        for(int counter = 0; counter < listView.getCount(); counter++){
            //if something is checked get name of item
            if (sparseBooleanArray.get(counter) == true){
                selectedItem = listView.getItemAtPosition(counter).toString();
                break;
            }
        }
        return selectedItem;
    }

    //go back
    public void predefinedWorkoutBackButton(View view){
        finish();
    }

    public void selectPredefinedWorkoutButton(View view)
    {
        SparseBooleanArray selected = workoutLV.getCheckedItemPositions();

        //nothing was pressed yet in listview to populate the sparse boolean array
        if(selected.size()<1){
            Toast.makeText(getApplicationContext(),"You must select a workout.",Toast.LENGTH_SHORT).show();
        }
        else {
            String workoutStr = "";

            for (int i = 0; i < workoutLV.getAdapter().getCount(); i++) {
                if (selected.get(i)) {
                    workoutStr += workoutLV.getItemAtPosition(i).toString() + "|";
                }
            }
            //at least one exercise is checked
            if(!workoutStr.equals("")){
                workoutStr = workoutStr.substring(0, workoutStr.length() - 1);
                Log.i("Info", workoutStr);
            }
            //no exercise is checked
            else{
                Toast.makeText(getApplicationContext(),"You must select a workout.",Toast.LENGTH_SHORT).show();
            }

        }

    }

}

