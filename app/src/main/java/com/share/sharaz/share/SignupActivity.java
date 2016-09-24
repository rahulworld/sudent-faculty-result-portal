package com.share.sharaz.share;

/**
 * Created by rahul on 19-05-2016.
 */
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    String url1="http://www.ewaysoft.in/student/registerUser1.php";
    ProgressDialog progressDialog;

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.input_rollno) EditText _rollText;
    @InjectView(R.id.input_sem) EditText _semText;

    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String roll = _rollText.getText().toString();
        String sem = _semText.getText().toString();
        new BackgroundTask().execute(url1,name,email,password,roll,sem);
        /*
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 10000);*/
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
    class BackgroundTask extends AsyncTask<String, Void, String> {
        HttpURLConnection httpURLConnection=null;
        InputStream inputStream=null;
        //ProgressDialog pDialog;
        String[] data1;
        String flag,name1,roll1,sem1;

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                String name=params[1];
                String email=params[2];
                String password=params[3];
                String roll=params[4];
                String sem=params[5];
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                String data = URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(name, "UTF-8")+"&"+
                        URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(email, "UTF-8")+"&"+
                        URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(password, "UTF-8")+"&"+
                        URLEncoder.encode("roll", "UTF-8")+"="+URLEncoder.encode(roll, "UTF-8")+"&"+
                        URLEncoder.encode("pointer", "UTF-8")+"="+URLEncoder.encode(sem, "UTF-8");
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
            //loadingDialog = ProgressDialog.show(getApplicationContext(), "Please wait", "Loading...");

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            onSignupSuccess();
            progressDialog.dismiss();
                if (!result.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Your Account is Created", Toast.LENGTH_LONG).show();
                    Intent i2=new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(i2);
                }
                else{
                    Toast.makeText(getApplicationContext(), "not Created", Toast.LENGTH_SHORT).show();
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
