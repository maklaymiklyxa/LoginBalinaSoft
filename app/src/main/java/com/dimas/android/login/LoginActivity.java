package com.dimas.android.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dimas.android.login.data.model.Post;
import com.dimas.android.login.data.remote.APIService;
import com.dimas.android.login.data.remote.ApiUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    String[] domains = new String[]{
            /* Default domains included */
            "aol.com", "att.net", "comcast.net", "facebook.com", "gmail.com", "gmx.com", "googlemail.com",
            "google.com", "hotmail.com", "hotmail.co.uk", "mac.com", "me.com", "mail.com", "msn.com",
            "live.com", "sbcglobal.net", "verizon.net", "yahoo.com", "yahoo.co.uk",

            /* Other global domains */
            "email.com", "games.com" /* AOL */, "gmx.net", "hush.com", "hushmail.com", "inbox.com",
            "lavabit.com", "love.com" /* AOL */, "pobox.com", "rocketmail.com" /* Yahoo */,
            "safe-mail.net", "wow.com" /* AOL */, "ygm.com" /* AOL */, "ymail.com" /* Yahoo */, "zoho.com", "fastmail.fm",

            /* United States ISP domains */
            "bellsouth.net", "charter.net", "cox.net", "earthlink.net", "juno.com",

            /* British ISP domains */
            "btinternet.com", "virginmedia.com", "blueyonder.co.uk", "freeserve.co.uk", "live.co.uk",
            "ntlworld.com", "o2.co.uk", "orange.net", "sky.com", "talktalk.co.uk", "tiscali.co.uk",
            "virgin.net", "wanadoo.co.uk", "bt.com",

            /* Domains used in Asia */
            "sina.com", "qq.com", "naver.com", "hanmail.net", "daum.net", "nate.com", "yahoo.co.jp", "yahoo.co.kr", "yahoo.co.id", "yahoo.co.in", "yahoo.com.sg", "yahoo.com.ph",

            /* French ISP domains */
            "hotmail.fr", "live.fr", "laposte.net", "yahoo.fr", "wanadoo.fr", "orange.fr", "gmx.fr", "sfr.fr", "neuf.fr", "free.fr",

            /* German ISP domains */
            "gmx.de", "hotmail.de", "live.de", "online.de", "t-online.de" /* T-Mobile */, "web.de", "yahoo.de",

            /* Russian ISP domains */
            "mail.ru", "rambler.ru", "yandex.ru",

            /* Belgian ISP domains */
            "hotmail.be", "live.be", "skynet.be", "voo.be", "tvcablenet.be",

            /* Argentinian ISP domains */
            "hotmail.com.ar", "live.com.ar", "yahoo.com.ar", "fibertel.com.ar", "speedy.com.ar", "arnet.com.ar",

            /* Domains used in Mexico */
            "hotmail.com", "yahoo.com.mx", "live.com.mx", "yahoo.com", "hotmail.es", "live.com", "hotmail.com.mx", "prodigy.net.mx", "msn.com"
    };

    private AppCompatAutoCompleteTextView mEmailComplite;
    private Button mSignBtn;
    private EditText mPassText;
    private CheckBox mShowPass;
    private APIService mAPIService;

    private boolean isOk = false;

    private static final Pattern EMAIL_PATTERN_PREFIX = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}"

    );
    private static final Pattern EMAIL_PATTERN_POSTFIX = Pattern.compile(
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );


    private ArrayList<String> list;
    private String strPrefix = null;
    private static final String KEY_INDEX_EMAIL = "email";
    private static final String KEY_PASSWORD = "pass";
    private static final String KEY_ISOK = "isok";
    private static final String KEY_PREFIX = "prefix";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (savedInstanceState != null) {
            mEmailComplite.setText(savedInstanceState.getString(KEY_INDEX_EMAIL));
            mPassText.setText(savedInstanceState.getString(KEY_INDEX_EMAIL));
            isOk = savedInstanceState.getBoolean(KEY_ISOK);
            strPrefix = savedInstanceState.getString(KEY_PREFIX);
        }
        mEmailComplite = (AppCompatAutoCompleteTextView) findViewById(R.id.emailAutoComplete);
        mPassText = (EditText) findViewById(R.id.passEnter);
        mSignBtn = (Button) findViewById(R.id.logIn);
        mShowPass = (CheckBox) findViewById(R.id.showPass);
        mAPIService = ApiUtils.getApiService();
        mEmailComplite.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                completeEmail(s);
            }
        });
        mPassText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePass(s.toString());
            }
        });
        mPassText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    mSignBtn.callOnClick();
                    return true;
                }
                return false;
            }
        });
        mShowPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    mPassText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mPassText.setSelection(mPassText.length());
                } else {
                    mPassText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mPassText.setSelection(mPassText.length());
                }
            }
        });

        mSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmailComplite.getText().toString().isEmpty()) {
                    mEmailComplite.setError("Please input email!");
                    isOk = false;
                }
                if (isOk & validatePass(mPassText.getText().toString())) {
                    if (isNetworkAvailable()) {
                        sendPostOkHttp();
                        sendPostRetrofit();
                        mEmailComplite.setText("");
                        mEmailComplite.setError(null);
                        mPassText.setText("");
                        mPassText.setError(null);
                        isOk = false;
                        Toast.makeText(LoginActivity.this,"Registration done",Toast.LENGTH_SHORT).show();
                    } else {
                        new android.app.AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Network needed")
                                .setMessage("This app needs any network connection!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        return;
                                    }
                                })
                                .create()
                                .show();
                    }
                } else {
                    validatePass(mPassText.getText().toString());
                }
            }
        });
    }

    public void sendPostOkHttp() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("login", "zxcvbnmdsafsff7747474terwerw");
            jo.put("password", mPassText.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        PostOkHttp t = new PostOkHttp(jo.toString());
        t.execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_INDEX_EMAIL, mEmailComplite.getText().toString());
        outState.putString(KEY_PASSWORD, mPassText.getText().toString());
        outState.putString(KEY_PREFIX, strPrefix);
        outState.putBoolean(KEY_ISOK, isOk);
    }

    private void sendPostRetrofit() {
        JSONObject paraObject = new JSONObject();
        try {
            paraObject.put("login", "131w74rewre54454weqwee3rew4242msmfsd");
            paraObject.put("password", mPassText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAPIService.savePost(paraObject.toString()).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful()) {
                    saveArrayList(response.body().getData());
                    Toast.makeText(LoginActivity.this, "Sign up complete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.e("e", "Unable to submit post to API.");
            }
        });
    }

    private void completeEmail(Editable s) {
        if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
            mEmailComplite.setError("Wrong email!");
            isOk = false;
        } else {
            mEmailComplite.setError(null);
        }

        String str = s.toString();


        int atSignPosition = str.indexOf("@");
        if (atSignPosition == -1) {
            if (!isPrefixValid(str)) {
                isOk = false;
                mEmailComplite.setError("You can only use letters, numbers, periods (‘.’), and underscores (‘_’),(‘-’) in your username.");
            }
            strPrefix = str;
        } else if (atSignPosition < (str.length() + 1)) {
            strPrefix = str.substring(0, atSignPosition);
            if (strPrefix.length() > 0) {
                if (!isPrefixValid(strPrefix)) {
                    isOk = false;
                    mEmailComplite.setError("You can only use letters, numbers, periods (‘.’), and underscores (‘_’),(‘-’) in your username.");
                } else {
                    strPrefix = str.substring(0, atSignPosition + 1);
                    list = new ArrayList<>();
                    for (String domain : domains) {
                        String option = strPrefix + domain;
                        if (option.startsWith(s.toString())) {
                            list.add(option);
                        }
                    }

                    String postfix = str.substring(atSignPosition + 1, s.length());
                    if (!isDomainValid(postfix)) {
                        isOk = false;
                        mEmailComplite.setError("Check your domain name. You can only use letters and periods (‘.’) in your domain.");
                    } else {
                        if (isNetworkAvailable()) {
                            NetworkUrl networkUrl = new NetworkUrl();
                            networkUrl.execute(postfix);
                        } else {
                            new android.app.AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Network needed")
                                    .setMessage("This app needs any network connection!")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            return;
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    }
                }

            } else {
                isOk = false;
                mEmailComplite.setError("Your user name is empty!");
            }
        }
        mEmailComplite.setAdapter(new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_dropdown_item_1line, list));
    }

    private boolean isPrefixValid(String s) {
        if (s != null) {
            if (EMAIL_PATTERN_PREFIX.matcher(s).matches()) {
                return true;
            }
        }
        return false;
    }

    private boolean isDomainValid(String s) {
        if (s != null) {
            if (EMAIL_PATTERN_POSTFIX.matcher(s).matches()) {
                return true;
            }
        }
        return false;
    }

    private boolean validatePass(String s) {
        if (s.isEmpty()) {
            mPassText.setError("Field can't be empty");
            return false;
        }
        if (mPassText.length() < 8) {
            mPassText.setError("Enter 8 or more symbols");
            return false;
        } else {
            mPassText.setError(null);
            return true;
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityMgr;
        connectivityMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public void saveArrayList(Object obj) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(obj);
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        prefsEditor.putString("MyObject", jsonString);
        prefsEditor.apply();
    }

    private class NetworkUrl extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                URL url = new URL("https://www." + strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(3000);
                connection.connect();
                int code = connection.getResponseCode();

                if (code == 200) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (!aBoolean) {
                mEmailComplite.setError("Domain not found");
                isOk = false;
            } else {
                isOk = true;
            }
        }
    }
}
