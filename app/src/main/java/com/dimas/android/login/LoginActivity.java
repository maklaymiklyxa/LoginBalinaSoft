package com.dimas.android.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
            "[a-zA-Z0-9\\+\\_\\%\\-\\+]{1,256}"

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
                completeEmail(s.toString());
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
                if (isNetworkAvailable()) {
                    completeEmail(mEmailComplite.getText().toString());
                    if (isOk && validatePass(mPassText.getText().toString())) {
                        sendPostRetrofit();
                        if (isOk) {
                            mEmailComplite.setText("");
                            mEmailComplite.setError(null);
                            mPassText.setText("");
                            mPassText.setError(null);
                            isOk = false;
                        }
                    }
                } else {
                    new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogCustom)
                            .setTitle(R.string.dialogTitle)
                            .setMessage(R.string.dialogMessage)
                            .setPositiveButton(R.string.dialogOk, null)
                            .create()
                            .show();
                }
            }
        });
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
            paraObject.put("login", strPrefix);
            paraObject.put("password", mPassText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAPIService.savePost(paraObject.toString()).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful()) {
                    isOk = true;
                    saveArrayList(response.body().getData());
                    Toast.makeText(LoginActivity.this, R.string.signUpComplete, Toast.LENGTH_SHORT).show();
                } else if (response.code() == 400) {
                    isOk = false;
                    Toast.makeText(LoginActivity.this, R.string.nameExists, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.e("e", "Unable to submit post to API.");
            }
        });
    }

    private void completeEmail(String s) {
        int atSignPosition = s.indexOf("@");
        if (atSignPosition == -1) {
            strPrefix = s;
        } else {
            strPrefix = s.substring(0, atSignPosition);
        }
        if (isPrefixValid(strPrefix)) {
            list = new ArrayList<>();
            for (String domain : domains) {
                String option = strPrefix + "@" + domain;
                if (option.startsWith(s)) {
                    list.add(option);
                }
            }
            if (atSignPosition != -1 && atSignPosition < strPrefix.length() + 1) {
                String postfix = s.substring(atSignPosition + 1, s.length());
                if (isDomainValid(postfix) && isNetworkAvailable()) {
                    NetworkUrl networkUrl = new NetworkUrl();
                    networkUrl.execute(postfix);
                }
            } else {
                mEmailComplite.setError(getString(R.string.domainEmpty));
            }
        }
        mEmailComplite.setAdapter(new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_dropdown_item_1line, list));
    }

    private boolean isPrefixValid(String s) {
        if (s.length() > 0) {
            if (!EMAIL_PATTERN_PREFIX.matcher(s).matches()) {
                mEmailComplite.setError(getString(R.string.emailCheck));
                return false;
            }
        } else {
            mEmailComplite.setError(getString(R.string.emailUser));
            return false;
        }
        return true;
    }

    private boolean isDomainValid(String s) {
        if (!EMAIL_PATTERN_POSTFIX.matcher(s).matches()) {
            mEmailComplite.setError(getString(R.string.domainCheck));
            return false;
        }
        mEmailComplite.setError(null);
        return true;
    }

    private boolean validatePass(String s) {
        if (s.isEmpty()) {
            mPassText.setError(getString(R.string.fieldEmpty));
            return false;
        }
        if (mPassText.length() < 8) {
            mPassText.setError(getString(R.string.passLength));
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
                mEmailComplite.setError(getString(R.string.domainError));
                isOk = false;
            } else {
                isOk = true;
            }
        }
    }
}
