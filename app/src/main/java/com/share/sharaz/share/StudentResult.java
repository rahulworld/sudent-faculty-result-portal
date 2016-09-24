package com.share.sharaz.share;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rahul on 19-05-2016.
 */
public class StudentResult extends Activity {
    TextView name,roll,sem;
    String roll1,name1,pointer1;
    Button getResult;
    TextView lbl;
    ListView lv1;
    ListAdapter adapter;
    ArrayList<HashMap<String, String>> myList;
    String url2="http://www.ewaysoft.in/student/showResult.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_result);
        name= (TextView) findViewById(R.id.tvName);
        roll= (TextView) findViewById(R.id.tvRoll);
        sem= (TextView) findViewById(R.id.tvScore);
        getResult= (Button) findViewById(R.id.btn_getResult);
        lbl = (TextView) findViewById(R.id.txtresulttext1);
        lv1=(ListView)findViewById(R.id.listview1);
        myList=new ArrayList<HashMap<String,String>>();
        //lv1 = getListView();
        Intent intent = getIntent();
        name1 = intent.getStringExtra("name");
        roll1 = intent.getStringExtra("roll");
        pointer1 = intent.getStringExtra("pointer");
            name.setText("Name  :      "+name1);
            roll.setText("Enroll  :   " + roll1);
            sem.setText("Semester  :   "+pointer1);
        getResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if(!roll1.isEmpty())
                    {
                        try
                        {
                            new BackgroundTask1().execute(url2,roll1);
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(),"Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Uncomplete Record make new Id", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    class BackgroundTask1 extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
        HttpURLConnection httpURLConnection=null;
        InputStream inputStream=null;
        ProgressDialog pDialog=null;

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                String roll=params[1];
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                String data = URLEncoder.encode("roll", "UTF-8")+"="+URLEncoder.encode(roll, "UTF-8");
                wr.write(data);
                wr.flush();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                //String data1=stringBuffer.toString();
                String parentJson=stringBuffer.toString();
                JSONObject parentObject=new JSONObject(parentJson);
                JSONArray parentArray=parentObject.getJSONArray("student");
                //List<> friendsModelList=new ArrayList();
                for(int i=0;i<parentArray.length();i++){
                    JSONObject finalObject=parentArray.getJSONObject(i);
                    HashMap<String,String> temp=new HashMap<String, String>();
                    temp.put("First",finalObject.getString("subject"));
                    temp.put("Second",finalObject.getString("marks"));
                    myList.add(temp);
                }

                return myList;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(StudentResult.this);
            pDialog.setMessage("Loading Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
            super.onPostExecute(result);
            pDialog.dismiss();

            try
            {
                if (result.size() != 0) {
                    ListViewAdapter adapter=new ListViewAdapter(StudentResult.this, result);
                    lv1.setAdapter(adapter);
                    lbl.setText("Result Imported");
                }
            }catch(Exception e)
            {
                Toast.makeText(getApplicationContext(), "Result Not Uploaded", Toast.LENGTH_SHORT).show();
            }


        }
    }
}
