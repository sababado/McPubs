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
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.sababado.mcpubs.R;
import com.sababado.mcpubs.models.Constants;
import com.sababado.mcpubs.models.Pub;

import java.util.Arrays;

public class MyPubsActivity extends AppCompatActivity {

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

    public void newPub() {
        startPubDialog(null, R.string.new_pub);
    }

    public void startPubDialog(final Pub pub, @StringRes int title) {
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
                            savedPub.setPubType(String.valueOf(spn.getSelectedItem()));
                            String spacer = savedPub.getPubType() == Constants.MCO_P ? "" : " ";
                            savedTitle = savedPub.getPubTypeString() + spacer + savedTitle.toUpperCase();
                            if (!TextUtils.equals(pub == null ? "" : pub.getTitle(), savedTitle)) {
                                savedPub.setTitle(savedTitle);
                                pushPubUpdate(savedPub);
                            }
                        }
                    }
                })
                .create();
        if (pub != null) {
            tv.setText(pub.getTitle());
            int i = Arrays.binarySearch(Constants.PUB_TYPE_VALS, pub.getPubType());
            spn.setSelection(i);
        }

        alertDialog.setView(view);
        alertDialog.show();
    }

    private void pushPubUpdate(Pub pub) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        MyPubsFragment fragment = (MyPubsFragment) fragmentManager.findFragmentById(R.id.my_pubs_fragment);
        if (!fragment.isRemoving() && fragment.isVisible()) {
            fragment.addPub(pub);
        }
    }
}
