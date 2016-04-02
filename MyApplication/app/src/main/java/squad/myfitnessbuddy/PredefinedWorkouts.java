package squad.myfitnessbuddy;

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
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class PredefinedWorkouts extends AppCompatActivity {

    public ListView workoutLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.predefined_workouts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Customize the Actionbar color to 'Black' and text to 'Setup Page'
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Predefined Workouts");

        workoutLV = (ListView) findViewById(R.id.workoutLV);

        populateWorkouts();
    }
    //Method to populate list of predefined workouts
    protected void populateWorkouts() {
        //create array to store Predefined Workout names
        final ArrayList<String> predefinedWorkoutExerciseList = new ArrayList<>();

        //create the database manager
        DataBaseHelper myDbHelper_v2;
        myDbHelper_v2 = new DataBaseHelper(this);

        //create database if it does not exist (or was erased)
        //open if it is already there
        try {
            myDbHelper_v2.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            myDbHelper_v2.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
        try {
            //get actual database from manager (the one we just opened)
            SQLiteDatabase exerciseDB = myDbHelper_v2.getReadableDatabase();

            //set cursor to start traversing search
            //raw query is SQL code (Select everything from table "exercises" and order them by name)
            Cursor c_v2 = exerciseDB.rawQuery("SELECT * FROM predefinedWorkouts", null);

            //get items from "name" column of table
            int idxName = c_v2.getColumnIndex("name");

            //move cursor to to of list (table)
            c_v2.moveToFirst();

            while (c_v2 != null) {
                predefinedWorkoutExerciseList.add(c_v2.getString(idxName));
                c_v2.moveToNext();
            }

            //close database to avoid memory leak
            exerciseDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //adapter to get list into ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, predefinedWorkoutExerciseList);
        workoutLV.setAdapter(adapter);
        //only one workout can be checked at a time
        workoutLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    //Code to save the chosen predefined workout to the saved workouts repository
   /*public void previewPredefinedWorkoutButton(View view)
    {
        SparseBooleanArray selected = workoutLV.getCheckedItemPositions();

        //nothing was pressed yet in listview to populate the sparse boolean array
        if(selected.size()<1){
            Toast.makeText(getApplicationContext(),"Choose a workout to preview.",Toast.LENGTH_SHORT).show();
        }
        else {
            String workoutStr = "";

            for (int i = 0; i < workoutLV.getAdapter().getCount(); i++) {
                if (selected.get(i)) {
                    workoutStr += workoutLV.getItemAtPosition(i).toString() + "|";

                }
            }
            //at least one workout is checked
            if(!workoutStr.equals("")){
                workoutStr = workoutStr.substring(0, workoutStr.length() - 1);


            }
            //no exercise is checked
            else{
                Toast.makeText(getApplicationContext(),"Choose a workout to preview.",Toast.LENGTH_SHORT).show();
            }

        }

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
                Log.i("Test", workoutStr);
            }
            //no exercise is checked
            else{
                Toast.makeText(getApplicationContext(),"You must select a workout.",Toast.LENGTH_SHORT).show();
            }

        }

    }*/

}

