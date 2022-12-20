package com.unipi.kormazos.app2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText countryName,countryCapital,countryPopulation,countryCoin,countryLanguage;
    SQLiteDatabase db;
    SharedPreferences preferences;
    EditText inputData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        countryName = findViewById(R.id.editTextTextPersonName);
        countryCapital = findViewById(R.id.editTextTextPersonName2);
        countryPopulation = findViewById(R.id.editTextTextPersonName3);
        countryCoin = findViewById(R.id.editTextTextPersonName4);
        countryLanguage = findViewById((R.id.editTextTextPersonName5));
        inputData = findViewById(R.id.editTextTextPersonName);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        db = openOrCreateDatabase("CountryBase.db",MODE_PRIVATE,null);
        db.execSQL("Create table if not exists Country(" +
                "CountryID integer primary key autoincrement," +
                "CountryName TEXT unique not null," +
                "CountryCapital TEXT," +
                "CountryPopulation integer," +
                "CountryCoin TEXT," +
                "CountryLanguge TEXT" +
                ")");

        db.execSQL("Insert or ignore into Country(CountryID,CountryName,CountryCapital,CountryPopulation,CountryCoin,CountryLanguge) values (" +
                "1,'Greece','Athens',10678632,'Euro','Greek')");
        db.execSQL("Insert or ignore into Country(CountryID,CountryName,CountryCapital,CountryPopulation,CountryCoin,CountryLanguge) values (" +
                "2,'Ukraine','Kyiv',41130432,'Euro','Ukraine')");
        db.execSQL("Insert or ignore into Country(CountryID,CountryName,CountryCapital,CountryPopulation,CountryCoin,CountryLanguge) values (" +
                "3,'Spain','Madrid',47326687,'Euro','Spanish')");
    }
    private void showMessage(String title, String message){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .show();
    }
    public void readall(View view){
        Cursor cursor = db.rawQuery("Select * from Country",null);
        StringBuilder builder = new StringBuilder();
        while (cursor.moveToNext()){
            builder.append("Country Name: ").append(cursor.getString(1)).append("\n");
            builder.append("Country Capital: ").append(cursor.getString(2)).append("\n");
            builder.append("Country Population: ").append(cursor.getInt(3)).append("\n");
            builder.append("Country Coin: ").append(cursor.getString(4)).append("\n");
            builder.append("Country Language: ").append(cursor.getString(5)).append("\n\n");
        }
        cursor.close();
        showMessage("Countries",builder.toString());
    }

    public void searchbyname(View view){
        if (!inputData.getText().toString().isEmpty()){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("mykey2",inputData.getText().toString());
            editor.apply();
        }else {
            Toast.makeText(this, "You need to write the name of the country first.", Toast.LENGTH_SHORT).show();
            return;
        }
        String s = preferences.getString("mykey2","No data yet.");
        Cursor c = db.rawQuery("Select * from Country where CountryName= '" + s + "' ", null);
        c.moveToNext();

        if(c.getCount() == 0){
            showMessage("Help","This name in not exist in DB.");
            return;
        }

        StringBuilder builder2 = new StringBuilder();
        builder2.append("Country name: ").append(c.getString(1)).append("\n");

        builder2.append("Country capital: ").append(c.getString(2)).append("\n");
        EditText editText2 = (EditText) findViewById(R.id.editTextTextPersonName2);
        editText2.getText().clear();
        editText2.append(c.getString(2));

        builder2.append("Country population: ").append(c.getInt(3)).append("\n");
        EditText editText3 = (EditText) findViewById(R.id.editTextTextPersonName3);
        editText3.getText().clear();
        editText3.append(c.getString(3));

        builder2.append("Country Coin: ").append(c.getString(4)).append("\n");
        EditText editText4 = (EditText) findViewById(R.id.editTextTextPersonName4);
        editText4.getText().clear();
        editText4.append(c.getString(4));

        builder2.append("Country Language: ").append(c.getString(5)).append("\n\n");
        EditText editText5 = (EditText) findViewById(R.id.editTextTextPersonName5);
        editText5.getText().clear();
        editText5.append(c.getString(5));
        showMessage("Countries",builder2.toString());
        c.close();

    }
    public void delete(View view) {
        if (!inputData.getText().toString().isEmpty()) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("mykey3", inputData.getText().toString());
            editor.apply();
        } else {
            Toast.makeText(this, "You need to write some data first", Toast.LENGTH_SHORT).show();
            return;
        }
        String s2 = preferences.getString("mykey3", "No data yet");
        Cursor d1 = db.rawQuery("Select * from Country where CountryName= '" + s2 + "' ", null);
        d1.moveToNext();
        if(d1.getCount() == 0){
            showMessage("Help","This Name in not exist in DB");
            return;
        }
        StringBuilder builder3 = new StringBuilder();
        builder3.append("Country  ").append(d1.getString(1)).append(" Deleted !\n");
        db.delete("Country","countryName=?",new String[]{s2});
        showMessage("Country ", builder3.toString());
        d1.close();
    }

    public void add(View view){
        String name = countryName.getText().toString();
        String capital = countryCapital.getText().toString();
        if (!inputData.getText().toString().isEmpty() && !countryCapital.getText().toString().isEmpty() )  {

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("mykey4", inputData.getText().toString());
            editor.apply();
        } else {
            Toast.makeText(this, "You need to write some data first.", Toast.LENGTH_SHORT).show();
            return;
        }
        String s5 = preferences.getString("mykey4", "No data yet.");
        Cursor d2 = db.rawQuery("Select * from Country where CountryName= '" + s5 + "' ", null);
        d2.moveToNext();
        if(d2.getCount() == 0){
            Integer population = Integer.valueOf(countryPopulation.getText().toString());
            String coin = countryCoin.getText().toString();
            String language = countryLanguage.getText().toString();
            db.execSQL("Insert or ignore into Country(CountryName,CountryCapital,CountryPopulation,CountryCoin,CountryLanguge) values(?,?,?,?,?)",new Object[]{name,capital,population,coin,language});
            Toast.makeText(this, "Data saved to database!", Toast.LENGTH_SHORT).show();}
        else{
            showMessage("Help","This name in already exist in DB.");
            return;
        }



    }

    public void update(View view){
        if (!inputData.getText().toString().isEmpty()) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("mykey5", inputData.getText().toString());
            editor.apply();
        } else {
            showMessage("Instruction","Step 1:\n" +
                    "Search for your Country(use the search button)\n\n " +
                    "Step 2:\n " +
                    "Edit the data in the textblocks\n\n" +
                    "Step3\n" +
                    "Press the Update Button. ");
            return;
        }
        String s4 = preferences.getString("mykey2","No data yet");
        Cursor c4 = db.rawQuery("Select * from Country where CountryName= '" + s4 + "' ", null);
        c4.moveToNext();

        if(c4.getCount() == 0){
            showMessage("Help","This Name in not exist in DB");
            showMessage("Instruction","Step 1:\n" +
                    "Search for your Country(use the search button)\n\n " +
                    "Step 2:\n " +
                    "Edit the data in the textblocks\n\n" +
                    "Step3\n" +
                    "Press the Update Button. ");
            return;
        }

        String name = countryName.getText().toString();
        String capital = countryCapital.getText().toString();
        Integer population = Integer.valueOf(countryPopulation.getText().toString());
        String coin = countryCoin.getText().toString();
        String language = countryLanguage.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put("CountryCapital" ,capital);
        cv.put("CountryPopulation",population);
        cv.put("CountryCoin",coin);
        cv.put("CountryLanguge",language);
        db.update("Country",cv,"countryName=?",new String[]{name});
        Toast.makeText(this, "Data update!", Toast.LENGTH_SHORT).show();
    }

    public void  clear(View view){

        EditText editText1 = (EditText) findViewById(R.id.editTextTextPersonName);
        editText1.getText().clear();

        EditText editText2 = (EditText) findViewById(R.id.editTextTextPersonName2);
        editText2.getText().clear();

        EditText editText3 = (EditText) findViewById(R.id.editTextTextPersonName3);
        editText3.getText().clear();

        EditText editText4 = (EditText) findViewById(R.id.editTextTextPersonName4);
        editText4.getText().clear();

        EditText editText5 = (EditText) findViewById(R.id.editTextTextPersonName5);
        editText5.getText().clear();

        showMessage("Cleared",null);

    }
}