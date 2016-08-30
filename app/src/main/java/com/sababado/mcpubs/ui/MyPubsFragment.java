package com.sababado.mcpubs.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sababado.mcpubs.PubAdapter;
import com.sababado.mcpubs.R;
import com.sababado.mcpubs.models.Pub;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyPubsFragment extends ListFragment {

    private static final String ARG_PUB_LIST = "arg_pub_list";

    private PubAdapter pubAdapter;
    private ArrayList<Pub> pubList;
    private Callbacks callbacks;

    public MyPubsFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbacks = (Callbacks) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setEmptyText(getString(R.string.empty_pub_list_text));

        if (savedInstanceState != null) {
            pubList = savedInstanceState.getParcelableArrayList(ARG_PUB_LIST);
        }

        // TODO get pubList if null
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (pubAdapter == null) {
            pubAdapter = new PubAdapter(getContext(), pubList);
        }
        setListAdapter(pubAdapter);
        registerForContextMenu(getListView());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(ARG_PUB_LIST, pubList);
        super.onSaveInstanceState(outState);
    }

    public void addPub(Pub pub) {
        if (pubList == null) {
            pubList = new ArrayList<>();
        }
        pubList.add(pub);
        pubAdapter.setData(pubList);
        pubAdapter.notifyDataSetChanged();
    }

    public void editPub(int index, Pub pub) {
        pubList.set(index, pub);
        pubAdapter.setData(pubList);
        pubAdapter.notifyDataSetChanged();
    }

    public void deletePub(int index) {
        pubList.remove(index);
        pubAdapter.setData(pubList);
        pubAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.pub_list_item_action_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int position = info.position;
        switch (item.getItemId()) {
            case R.id.action_edit:
                Pub clickedPub = pubAdapter.getItem(position);
                if (callbacks != null) {
                    callbacks.editPub(clickedPub, position);
                }
                break;
            case R.id.action_delete:
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.delete_pub)
                        .setMessage(R.string.are_you_sure_delete_pub)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletePub(position);
                            }
                        }).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        unregisterForContextMenu(getListView());
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        callbacks = null;
        super.onDetach();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        getListView().showContextMenu();
        Pub clickedPub = pubAdapter.getItem(position);
        if (callbacks != null) {
            callbacks.editPub(clickedPub, position);
        }
    }

    public interface Callbacks {
        void editPub(Pub pub, int index);
    }
}
