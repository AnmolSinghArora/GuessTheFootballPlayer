package com.anmolarora.guessthefootballplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebnames = new ArrayList<String>();
    int chosenCeleb = 0;
    ImageView imageView;
    int locationOfCorrectAnswer = 0;
    String[] answers = new String[4];
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void celebChosen(View view){

        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){

            Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_LONG).show();

        }else{

            Toast.makeText(getApplicationContext(), "Wrong!It was "+ celebnames.get(chosenCeleb), Toast.LENGTH_LONG).show();

        }

        createNewQuestion();

    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{


        @Override
        protected Bitmap doInBackground(String... urls) {


            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;


            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }

            return null;

        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) { //Varargs and not an array//

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null; // kind of like a browser

            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();//Like opening browser window

                InputStream in = urlConnection.getInputStream(); //stream to hold input of data as it comes in

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read(); // reads contents of url

                while (data != -1) { // data loops through all characters

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            } catch (Exception e) {


                e.printStackTrace(); // to give error details like not proper address

                return "Failed";
            }
        }
    }

    public void createNewQuestion(){

        Random random = new Random();
        chosenCeleb = random.nextInt(celebURLs.size());

        ImageDownloader imageTask = new ImageDownloader();
        Bitmap celebImage;
        try {
            celebImage = imageTask.execute(celebURLs.get(chosenCeleb)).get(); // get image link
            imageView.setImageBitmap(celebImage); // image set

            locationOfCorrectAnswer = random.nextInt(4);

            int incorrectAnswer;

            for (int i=0; i<4; i++){

                if (i == locationOfCorrectAnswer){

                    answers[i] = celebnames.get(chosenCeleb);

                }else{

                    incorrectAnswer = random.nextInt(celebURLs.size());

                    while (incorrectAnswer == chosenCeleb){

                        incorrectAnswer = random.nextInt(celebURLs.size());
                    }

                    answers[i] = celebnames.get(incorrectAnswer);

                }


            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        } catch (Exception e) {

            e.printStackTrace();

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);

        DownloadTask task = new DownloadTask();

        //String result = task.execute(" ").get();
        String result = null;
        try {
            result = task.execute("http://www.manutd.com/en/Players-And-Staff/First-Team.aspx").get();

            String[] splitResult = result.split("<!-- Pagination -->"); // split the page

            Pattern p = Pattern.compile("<img height=\"79\" width=\"116\" src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]); // 0 because we want part before <!-- Pagination -->

            while(m.find()){

                celebURLs.add(m.group(1));

            }

            p = Pattern.compile("First-Team/(.*?).aspx");
            m = p.matcher(splitResult[0]);

            while(m.find()){

                celebnames.add(m.group(1));

            }


        } catch (InterruptedException e) {

            e.printStackTrace();//shows all errors

        } catch (ExecutionException e) {

            e.printStackTrace();

        }

        createNewQuestion();

    }
}
