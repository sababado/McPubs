package com.sababado.mcpubs.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.sababado.mcpubs.R;
import com.sababado.mcpubs.models.Pub;

public class MyPubsActivity extends AppCompatActivity implements MyPubsFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pubs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPub();
            }
        });
        // TODO check for google play services
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_pubs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void editPub(Pub pub, long pubId) {
        startPubDialog(pub, pubId, R.string.edit_pub);
    }

    public void newPub() {
        startPubDialog(null, -1, R.string.new_pub);
    }

    public void startPubDialog(final Pub pub, final long pubId, @StringRes int title) {
        View view = LayoutInflater.from(this).inflate(R.layout.edit_pub, null, false);
        final Spinner spn = (Spinner) view.findViewById(R.id.pub_type_spinner);
        final EditText tv = (EditText) view.findViewById(R.id.pub_title);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Pub savedPub = pub == null ? new Pub() : pub;
                        String savedTitle = tv.getText().toString().trim();
                        // Only save changes if there are changes to save, or if editing a pub and
                        // the title has changed.
                        if (!TextUtils.isEmpty(savedTitle)) {
                            savedTitle = String.valueOf(spn.getSelectedItem()) + " " + savedTitle.toUpperCase();
                            if (!TextUtils.equals(pub == null ? "" : pub.getTitle(), savedTitle)) {
                                savedPub.setTitle(savedTitle);
                                pushPubUpdate(savedPub, pubId);
                            }
                        }
                    }
                })
                .create();
        if (pub != null) {
            tv.setText(pub.getTitle());
        }

        alertDialog.setView(view);
        alertDialog.show();
    }

    private void pushPubUpdate(Pub pub, long pubId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        MyPubsFragment fragment = (MyPubsFragment) fragmentManager.findFragmentById(R.id.my_pubs_fragment);
        if (!fragment.isRemoving() && fragment.isVisible()) {
            if (pubId > -1) {
                fragment.editPub(pubId, pub);
            } else {
                fragment.addPub(pub);
            }
        }
    }
}
