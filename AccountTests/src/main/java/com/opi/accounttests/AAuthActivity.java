package com.opi.accounttests;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import helpers.AccountAuthenticator;
import helpers.AccountGeneral;

/**
 * Created by jorge on 11/25/13.
 */
public class AAuthActivity extends AccountAuthenticatorActivity implements View.OnClickListener
{
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public final static String PARAM_USER_PASS = "USER_PASS";

    private final int REQ_SIGNUP = 1;

    private final String TAG = this.getClass().getSimpleName();

    private AccountManager mAccountManager;
    private String mAuthTokenType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_auth);

        Button btn = (Button)findViewById(R.id.btnSubmit);
        btn.setOnClickListener(this);


    }

    public void submit() {
        final String userName = ((TextView) findViewById(R.id.userTxt)).getText().toString();
        final String userPass = ((TextView) findViewById(R.id.passTxt)).getText().toString();
        new AsyncTask<Void, Void, Intent>() {
            @Override
            protected Intent doInBackground(Void... params) {
                //String authtoken = sServerAuthenticate.userSignIn(userName, userPass, mAuthTokenType);
                final Intent res = new Intent();
                res.putExtra(AccountManager.KEY_ACCOUNT_NAME, userName);
                res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AccountGeneral.ACCOUNT_TYPE);
                //res.putExtra(AccountManager.KEY_AUTHTOKEN, authtoken);
                res.putExtra(PARAM_USER_PASS, userPass);
                return res;
            }
            @Override
            protected void onPostExecute(Intent intent) {
                finishLogin(intent);
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {
        submit();
    }

    public void onCancelClick(View v) {

        this.finish();

    }
    private void finishLogin(Intent intent) {
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;
            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            mAccountManager.setPassword(account, accountPassword);
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onSaveClick(View v) {
        TextView tvUsername;
        TextView tvPassword;
        TextView tvApiKey;
        String username;
        String password;
        String apiKey;
        boolean hasErrors = false;

        tvUsername = (TextView) this.findViewById(R.id.userTxt);
        tvPassword = (TextView) this.findViewById(R.id.passTxt);
        tvApiKey = (TextView) this.findViewById(R.id.userTxt);

        tvUsername.setBackgroundColor(Color.WHITE);
        tvPassword.setBackgroundColor(Color.WHITE);
        tvApiKey.setBackgroundColor(Color.WHITE);

        username = tvUsername.getText().toString();
        password = tvPassword.getText().toString();
        apiKey = tvApiKey.getText().toString();

        if (username.length() < 3) {
            hasErrors = true;
            tvUsername.setBackgroundColor(Color.MAGENTA);
        }
        if (password.length() < 3) {
            hasErrors = true;
            tvPassword.setBackgroundColor(Color.MAGENTA);
        }
        if (apiKey.length() < 3) {
            hasErrors = true;
            tvApiKey.setBackgroundColor(Color.MAGENTA);
        }

        if (hasErrors) {
            return;
        }

        // Now that we have done some simple "client side" validation it
        // is time to check with the server

        // ... perform some network activity here

        // finished

        String accountType = this.getIntent().getStringExtra(AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY);
        if (accountType == null) {
            accountType = AccountGeneral.ACCOUNT_TYPE;
        }

        AccountManager accMgr = AccountManager.get(this);

        if (hasErrors) {

            // handel errors

        } else {

            // This is the magic that addes the account to the Android Account Manager
            final Account account = new Account(username, accountType);
            accMgr.addAccountExplicitly(account, password, null);

            // Now we tell our caller, could be the Andreoid Account Manager or even our own application
            // that the process was successful

            final Intent intent = new Intent();
            intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
            intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
            intent.putExtra(AccountManager.KEY_AUTHTOKEN, accountType);
            this.setAccountAuthenticatorResult(intent.getExtras());
            this.setResult(RESULT_OK, intent);
            this.finish();

        }
    }
}
