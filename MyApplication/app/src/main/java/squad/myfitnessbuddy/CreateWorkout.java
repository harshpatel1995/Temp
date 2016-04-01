package squad.myfitnessbuddy;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class CreateWorkout extends AppCompatActivity {

    //listview control that displays exercises on screen
    public ListView exerciseLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_workout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Customize the Actionbar color to 'Black' and text to 'Setup Page'
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Create Workout");

        //gets intent of page that just called this one
        Intent intent = getIntent();

        exerciseLV = (ListView) findViewById(R.id.exercisesLV);

        //populate list of exercises
        populateExercises();


    }

    //Method to populate list of exercises
    protected void populateExercises(){
        //create array to store exercise names
        final ArrayList<String> exercisesList = new ArrayList<>();

        //create the database manager
        DataBaseHelper myDbHelper;
        myDbHelper = new DataBaseHelper(this);

        //create database if it does not exist (or was erased)
        //open if it is already there
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
        try {
            //get actual database from manager (the one we just opened)
            SQLiteDatabase exerciseDB = myDbHelper.getReadableDatabase();

            //set cursor to start traversing search
            //raw query is SQL code (Select everything from table "exercises" and order them by name)
            Cursor c = exerciseDB.rawQuery("SELECT * FROM exercises ORDER BY name", null);

            //get items from "name" column of table
            int nameIndex = c.getColumnIndex("name");

            //move cursor to to of list (table)
            c.moveToFirst();

            //add exercise to list and move cursor to next exercise
            while (c != null) {

               exercisesList.add(c.getString(nameIndex));
                c.moveToNext();
            }

            //close database to avoid memory leak
            exerciseDB.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        //adapter to get list into ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, exercisesList);
        exerciseLV.setAdapter(adapter);
        //check multiple exercises at a time
        exerciseLV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    }


    //************Still Working On  @@@Brandon
    //Code to save a new workout (will add to database)
    public void saveCustomWorkout(View view)
    {
        //get workout name from user
        EditText workoutNameED = (EditText) findViewById(R.id.createWorkoutName);
        String workoutNameStr = workoutNameED.getText().toString();

        //boolean array for all list positions
        //true if checked, false if unchecked
        SparseBooleanArray checked = exerciseLV.getCheckedItemPositions();

        //there is no workout name
        if (workoutNameStr.equals("")){
            Toast.makeText(getApplicationContext(),"The workout must have a name.",Toast.LENGTH_SHORT).show();
        }
        //nothing was pressed yet in listview to populate the sparse boolean array
        else if(checked.size()<1){
            Toast.makeText(getApplicationContext(),"You must select at least one exercise.",Toast.LENGTH_SHORT).show();
        }
        else {
            //list of exercises to store in database for workout
            String exercisesForWorkoutStr = "";

            for (int i = 0; i < exerciseLV.getAdapter().getCount(); i++) {
                if (checked.get(i)) {
                    exercisesForWorkoutStr += exerciseLV.getItemAtPosition(i).toString() + "|";
                }
            }
            //at least one exercise is checked
            if(!exercisesForWorkoutStr.equals("")){
            exercisesForWorkoutStr = exercisesForWorkoutStr.substring(0, exercisesForWorkoutStr.length() - 1);
            Log.i("Test", exercisesForWorkoutStr);
            }
            //no exercise is checked
            else{
                Toast.makeText(getApplicationContext(),"You must select at least one exercise.",Toast.LENGTH_SHORT).show();
            }

            }

    }

    public void saveCustomWorkoutInDataBase(String workoutNameStr, String exercisesStr){

        SQLiteDatabase database = this.openOrCreateDatabase("mfbDatabase.db", MODE_PRIVATE, null);

        database.execSQL(ConstantValues.cCREATE_OR_OPEN_SAVED_WORKOUTS_DATABASE_SQL);


    }




}


