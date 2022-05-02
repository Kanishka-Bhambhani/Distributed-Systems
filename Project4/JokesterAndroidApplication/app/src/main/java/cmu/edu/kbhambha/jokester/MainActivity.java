/**
 * Name : Kanishka Bhambhani
 * Andrew Id: kbhambha
 */
package cmu.edu.kbhambha.jokester;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;


import android.view.Menu;
import android.view.MenuItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class MainActivity
 * The class is used to interact with the user and the model that gets the data from the 3rd party api
 * We get the user's data and send it to the heroku api it order to communicate with the 3rd party api
 */
public class MainActivity extends AppCompatActivity {

    //Declaration of various ui fields used in the android application
    static Spinner categorySpinner;
    static CheckBox nsfwFlag;
    static CheckBox sexistFlag;
    static CheckBox racistFlag;
    static CheckBox politicalFlag;
    static CheckBox explicitFlag;
    static CheckBox religiousFlag;
    static CheckBox singleJokeType;
    static CheckBox twoPartJokeType;
    static SeekBar amountOfJokes;
    static TextView numberOfJokes;
    public static TextView jokes;

    //The object of the class to implement asynchronous background process of communicating with 3rd api
    final MainActivity ma = this;

    /**
     * function onCreate
     * the on create function is used to initialize the fields
     * @param savedInstanceState the state of ondroid application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting the view to the content view xml
        setContentView(R.layout.content_main);
        //Initializing the fields with their ui ids
        categorySpinner = findViewById(R.id.category);
        nsfwFlag = findViewById(R.id.nsfw);
        sexistFlag = findViewById(R.id.sexist);
        racistFlag = findViewById(R.id.racist);
        politicalFlag = findViewById(R.id.political);
        explicitFlag = findViewById(R.id.explicit);
        religiousFlag = findViewById(R.id.religious);
        singleJokeType = findViewById(R.id.single);
        twoPartJokeType = findViewById(R.id.twopart);
        amountOfJokes = findViewById(R.id.amountOfJokes);
        numberOfJokes = findViewById(R.id.textView4);
        jokes = findViewById(R.id.jokes);
        //Changing the progress of number of jokes selected by user based on the selection
        amountOfJokes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                numberOfJokes.setText("Number of Jokes: " + progress);
                //textView.setY(100); just added a value set this properly using screen with height aspect ratio , if you do not set it by default it will be there below seek bar

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * function JokeMeUp
     * The function is called once we have gathered all the data from the user
     * It calls the function from the handler to send the data to the apu
     * @param view
     * @throws JSONException
     */
    public void onJokeMeUp(View view) throws JSONException {
        JSONObject request = new JSONObject();
        //Checking the data of all the fields selected by the user
        request.put("category", categorySpinner.getSelectedItem().toString());
        request.put("nsfw", nsfwFlag.isChecked());
        request.put("racist", racistFlag.isChecked());
        request.put("political", politicalFlag.isChecked());
        request.put("sexist", sexistFlag.isChecked());
        request.put("explicit", explicitFlag.isChecked());
        request.put("religious", religiousFlag.isChecked());
        request.put("single", singleJokeType.isChecked());
        request.put("twoPart", twoPartJokeType.isChecked());
        request.put("amountOfJokes", amountOfJokes.getProgress());

        //Creating the object of the handler class and calling the function from the class
        JokesterHandler jh = new JokesterHandler();
        jokes.setSelectAllOnFocus(true);
        jh.getJokes(request,ma);
    }

    /**
     * function sendJokes()
     * This function is called once the background processing is done and the jokes can now be displayed to the user
     * @param response the response from the api
     */
    public void sendJokes(JSONArray response) {
        //Making the test view visible
        jokes.setVisibility(View.VISIBLE);
        //Setting the text view to auto size
        jokes.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_NONE);
        //Getting the jokes from the api and displaying them
        try {
            if(response != null) {
                String processedJoke = "";
                for (int i = 0; i < response.length(); i++) {
                    processedJoke += response.getString(i) + "\n \n";
                }
                jokes.setText(processedJoke);
                jokes.setSelectAllOnFocus(true);
            }
            else
            {
                jokes.setText("No jokes found. Please Try with different values");
            }
        } catch (JSONException e) { //If the exception is wrong
            e.printStackTrace();
        }
    }

}