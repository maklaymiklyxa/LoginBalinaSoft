package com.dimas.android.login;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

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

    private String str;
    private AppCompatAutoCompleteTextView mEmailComplite;
    private Button mSignBtn;
    private EditText mPassText;
    private CheckBox mShowPass;

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


    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailComplite = (AppCompatAutoCompleteTextView) findViewById(R.id.emailAutoComplete);
        mPassText = (EditText) findViewById(R.id.passEnter);
        mSignBtn = (Button) findViewById(R.id.logIn);
        mShowPass = (CheckBox) findViewById(R.id.showPass);

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
                if (isOk && validatePass(mPassText.getText().toString())) {
                    return;
                }
            }
        });
    }

    private void completeEmail(Editable s) {
        if (Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
            mEmailComplite.setError(null);
        }

        str = s.toString();

        int atSignPosition = str.indexOf("@");
        if (atSignPosition == -1) {
            if (!isPrefixValid(str)) {
                mEmailComplite.setError("You can only use letters, numbers, periods (‘.’), and underscores (‘_’),(‘-’) in your username.");
            }
        } else if (atSignPosition > 1 && atSignPosition < (str.length() + 1)) {

            String prefix = str.substring(0, atSignPosition);
            if (!isPrefixValid(prefix)) {
                mEmailComplite.setError("You can only use letters, numbers, periods (‘.’), and underscores (‘_’),(‘-’) in your username.");
            } else {
                prefix = str.substring(0, atSignPosition + 1);
                list = new ArrayList<>();
                for (String domain : domains) {
                    String option = prefix + domain;
                    if (option.startsWith(s.toString())) {
                        list.add(option);
                    }
                }
            }
            String postfix = str.substring(atSignPosition + 1, s.length());
            if (!isDomainValid(postfix)) {
                mEmailComplite.setError("Check your domain name");
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
        if (mPassText.length() < 5) {
            mPassText.setError("Enter 5 or more symbols");
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
            } else {
                isOk = true;
            }
        }
    }
}
