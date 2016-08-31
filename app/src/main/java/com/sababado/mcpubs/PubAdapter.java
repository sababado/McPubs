package com.sababado.mcpubs;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.sababado.mcpubs.models.Constants;
import com.sababado.mcpubs.models.Pub;

import java.util.Date;

/**
 * Created by robert on 8/29/16.
 */
public class PubAdapter extends CursorAdapter {

    private LayoutInflater inflater;

    public PubAdapter(Context context, Cursor c) {
        super(context, c, 0);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.pub_list_item, parent, false);
        ViewHolder vh = new ViewHolder();
        vh.title = (TextView) view.findViewById(R.id.title);
        vh.readableTitle = (TextView) view.findViewById(R.id.readable_title);
        vh.status = (TextView) view.findViewById(R.id.status);
        vh.lastUpdated = (TextView) view.findViewById(R.id.last_updated);
        view.setTag(vh);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = (ViewHolder) view.getTag();
        Pub pub = new Pub(cursor);
        vh.title.setText(pub.getTitle());

        vh.readableTitle.setVisibility(TextUtils.isEmpty(pub.getReadableTitle()) ? View.GONE : View.VISIBLE);
        vh.readableTitle.setText(pub.getReadableTitle());

        String date = Utils.DATE_FORMAT.format(new Date(pub.getLastUpdated()));
        vh.lastUpdated.setText(
                context.getResources().getString(R.string.last_updated_at, date));

        boolean disableRow = false;
        if (pub.getUpdateStatus() == Constants.NO_CHANGE) {
            vh.status.setVisibility(View.GONE);
        } else if (pub.getUpdateStatus() == Constants.UPDATED) {
            vh.status.setVisibility(View.VISIBLE);
            vh.status.setTextColor(context.getResources().getColor(R.color.gold));
            vh.status.setText(R.string.updated);
        } else {
            vh.status.setVisibility(View.VISIBLE);
            vh.status.setTextColor(context.getResources().getColor(R.color.red));
            int textId = pub.getUpdateStatus() == Constants.DELETED ? R.string.deleted : R.string.updated_but_deleted;
            vh.status.setText(textId);
            disableRow = true;
        }

        if (!TextUtils.isEmpty(pub.getOldTitle())) {
            String oldTitle = context.getResources().getString(R.string.previously_called, pub.getOldTitle());
            String finalStatusText = vh.status.getText().toString() + "\n" + oldTitle;
            vh.status.setText(finalStatusText);
        }

        ((View) (vh.status.getParent())).setEnabled(disableRow);
    }

    private class ViewHolder {
        public TextView title;
        public TextView readableTitle;
        public TextView lastUpdated;
        public TextView status;
    }
}
