package com.sababado.mcpubs.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sababado.ezprovider.Contracts;
import com.sababado.mcpubs.PubAdapter;
import com.sababado.mcpubs.R;
import com.sababado.mcpubs.models.Constants;
import com.sababado.mcpubs.models.Pub;
import com.sababado.mcpubs.network.NetworkUtils;
import com.sababado.mcpubs.network.PubService;

import java.io.IOException;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyPubsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = MyPubsFragment.class.getSimpleName();

    private PubAdapter pubAdapter;
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
        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText(getString(R.string.empty_pub_list_text));
        if (pubAdapter == null) {
            pubAdapter = new PubAdapter(getContext(), null);
        }
        setListAdapter(pubAdapter);
        registerForContextMenu(getListView());
    }

    public void addPub(Pub pub) {
        ContentValues values = pub.toContentValues();
        Contracts.Contract contract = Contracts.getContract(Pub.class);
        getActivity().getContentResolver().insert(contract.CONTENT_URI, values);
        PubService.startActionSavePub(getContext(), pub);
    }

//    public void editPub(long pubId, Pub pub) {
//        savePub(pubId, pub);
//        doUpdatePub(pub);
//    }

    private void savePub(long pubId, Pub pub) {
        ContentValues values = pub.toContentValues();
        Contracts.Contract contract = Contracts.getContract(Pub.class);
        getActivity().getContentResolver().update(contract.CONTENT_URI, values,
                Contracts.Contract.ID_COLUMN_NAME + "=" + String.valueOf(pubId),
                null);
    }

    public void deletePub(Pub pub) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.pub_list_item_action_menu, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Cursor cursor = pubAdapter.getCursor();
        cursor.moveToPosition(info.position);
        Pub clickedPub = new Pub(cursor);

        boolean showReviewedOption = clickedPub.getUpdateStatus() != Constants.NO_CHANGE;
        menu.findItem(R.id.action_mark_as_reviewed).setVisible(showReviewedOption);
        menu.findItem(R.id.action_retry).setVisible(clickedPub.getSaveStatus() == Constants.SAVE_STATUS_FAILED);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Cursor cursor = pubAdapter.getCursor();
        cursor.moveToPosition(info.position);
        final Pub clickedPub = new Pub(cursor);
        switch (item.getItemId()) {
//            case R.id.action_edit:
//                if (callbacks != null) {
//                    callbacks.editPub(clickedPub, info.id);
//                }
//                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.delete_pub)
                        .setMessage(R.string.are_you_sure_delete_pub)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PubService.startActionDeletePub(getContext(), clickedPub);
                            }
                        }).show();
                return true;
            case R.id.action_mark_as_reviewed:
                clickedPub.setUpdateStatus(Constants.NO_CHANGE);
                savePub(info.id, clickedPub);
                return true;
            case R.id.action_retry:
                PubService.startActionSavePub(getContext(), clickedPub);
                return true;
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
        getListView().showContextMenuForChild(v);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Contracts.Contract contract = Contracts.getContract(Pub.class);
        final Uri uri = contract.CONTENT_URI;
        final String[] projection = contract.COLUMNS;
        return new CursorLoader(getContext(), uri, projection, null, null, "updateStatus desc, title asc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (pubAdapter != null) {
            pubAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (pubAdapter != null) {
            pubAdapter.swapCursor(null);
        }
    }

    public interface Callbacks {
//        void editPub(Pub pub, long pubId);
    }
}
