package com.share.sharaz.share;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;
/**
 * Created by rahul on 19-05-2016.
 */
public class FacultyActivity extends ListActivity{
    Button importCsv,uploadCsv;
    TextView lbl;
    ListView lv;
    ListAdapter adapter;
    DBController controller = new DBController(this);
    final Context context = this;

    ArrayList<HashMap<String, String>> myList;
    public static final int requestcode = 1;

    JSONObject jsonObj;
    JSONArray jsonArr;
    String url2="http://www.ewaysoft.in/student/uploadResult.php";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_result);
        importCsv= (Button) findViewById(R.id.btn_import1);
        uploadCsv= (Button) findViewById(R.id.btn_uploadResult);
        lbl = (TextView) findViewById(R.id.txtresulttext);
        lv = getListView();
        importCsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
                //fileintent.setType("gagt/sdf");
                fileintent.setType("file/*");
                try {
                    startActivityForResult(fileintent, requestcode);
                } catch (ActivityNotFoundException e) {
                   lbl.setText("No app found for importing the file.");
                }
            }
        });
        uploadCsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if(!jsonObj.toString().isEmpty())
                    {
                        try
                        {
                        new BackgroundTask1().execute(url2,jsonObj.toString());
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(),"Try Again Result Not Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch(Exception e)
                {
                        Toast.makeText(getApplicationContext(), "Please import excel file", Toast.LENGTH_SHORT).show();
                }
            }
        });
        myList= controller.getAllProducts();
        if (myList.size() != 0) {
            lv = getListView();
            adapter = new SimpleAdapter(FacultyActivity.this, myList,
                    R.layout.marks, new String[]{"Company", "Name", "Price"}, new int[]{
                    R.id.txtproductcompany, R.id.txtproductname, R.id.txtproductprice});
            setListAdapter(adapter);
            lbl.setText("");
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case requestcode:
                String filepath = data.getData().getPath();
                controller = new DBController(getApplicationContext());
                SQLiteDatabase db = controller.getWritableDatabase();
                String tableName = "proinfo";
                db.execSQL("delete from " + tableName);
                try {
                    if (resultCode == RESULT_OK) {
                        try {
                            FileReader file = new FileReader(filepath);
                            BufferedReader buffer = new BufferedReader(file);
                            ContentValues contentValues = new ContentValues();
                            String line = "";
                            db.beginTransaction();
                            jsonObj = new JSONObject();
                            jsonArr = new JSONArray();
                            //convert csv into json
                            //ConvertCsvToJson(filepath,"TestJavaBeans");
                            while ((line = buffer.readLine()) != null) {
                                String[] str = line.split(",", 3);  // defining 3 columns with null or blank field //values acceptance
                                //Id, Company,Name,Price
                                String company = str[0].toString();
                                String Name = str[1].toString();
                                String Price = str[2].toString();
                                contentValues.put("Company", company);
                                contentValues.put("Name", Name);
                                contentValues.put("Price", Price);
                                //for data in to json form
                                JSONObject pnObj = new JSONObject();
                                pnObj.put("roll", company);
                                pnObj.put("subject", Name);
                                pnObj.put("marks", Price);
                                jsonArr.put(pnObj);
                                db.insert(tableName, null, contentValues);
                                lbl.setText("Successfully Updated Database.");
                            }
                            db.setTransactionSuccessful();
                            db.endTransaction();

                            jsonObj.put("result", jsonArr);
                        } catch (IOException e) {
                            if (db.inTransaction())
                                db.endTransaction();
                            Dialog d = new Dialog(this);
                            d.setTitle(e.getMessage().toString() + "first");
                            d.show();
                            // db.endTransaction();
                        }
                    } else {
                        if (db.inTransaction())
                            db.endTransaction();
                        Dialog d = new Dialog(this);
                        d.setTitle("Only CSV files allowed");
                        d.show();
                    }
                } catch (Exception ex) {
                    if (db.inTransaction())
                        db.endTransaction();
                    Dialog d = new Dialog(this);
                    d.setTitle(ex.getMessage().toString() + "second");
                    d.show();
                    // db.endTransaction();
                }
        }
        myList= controller.getAllProducts();
        if (myList.size() != 0) {
            lv = getListView();
            ListAdapter adapter = new SimpleAdapter(FacultyActivity.this, myList,
                    R.layout.marks, new String[]{"Company", "Name", "Price"}, new int[]{
                    R.id.txtproductcompany, R.id.txtproductname, R.id.txtproductprice});
            setListAdapter(adapter);
            lbl.setText("Result Imported");
        }
    }
    class BackgroundTask1 extends AsyncTask<String, Void, String> {
        HttpURLConnection httpURLConnection=null;
        InputStream inputStream=null;
        ProgressDialog pDialog=null;

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                String jasonString=params[1];
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                String data = URLEncoder.encode("jsonString", "UTF-8")+"="+URLEncoder.encode(jasonString, "UTF-8");
                wr.write(data);
                wr.flush();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                String data1=stringBuffer.toString();
                /*
                String parentJson=stringBuffer.toString();
                JSONObject parentObject=new JSONObject(parentJson);
                // JSONArray parentArray = parentObject.getJSONArray("success");
                flag = parentObject.getString("flag");
                name1 = parentObject.getString("name");
                roll1 = parentObject.getString("roll");
                sem1 = parentObject.getString("pointer");
                String data1[]={flag,name1,roll1,sem1};
                */
                return data1;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
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
            pDialog = new ProgressDialog(FacultyActivity.this);
            pDialog.setMessage("Uploading Result");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            try
            {
                if(!result.isEmpty())
                {
                    lbl.setText("Result is Uploaded");

                }
            }catch(Exception e)
            {
                Toast.makeText(getApplicationContext(), "Try Again Result not uploaded", Toast.LENGTH_SHORT).show();
            }
            /*
            Intent i1=new Intent(getApplicationContext(),StudentResult.class);
            i1.putExtra("name",name1);
            i1.putExtra("roll",roll1);
            i1.putExtra("sem",sem1);
            startActivity(i1);
*/
        }
    }

}
