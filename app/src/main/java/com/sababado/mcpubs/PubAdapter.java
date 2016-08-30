package com.sababado.mcpubs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sababado.mcpubs.models.Pub;
import com.sababado.mcpubs.models.Constants;

import java.util.Date;
import java.util.List;

/**
 * Created by robert on 8/29/16.
 */
public class PubAdapter extends BaseAdapter {

    private List<Pub> pubList;
    private Context context;

    public PubAdapter(Context context, List<Pub> pubList) {
        this.pubList = pubList;
        this.context = context;
    }

    public void setData(List<Pub> pubList) {
        this.pubList = pubList;
    }

    @Override
    public int getCount() {
        return pubList == null ? 0 : pubList.size();
    }

    @Override
    public Pub getItem(int position) {
        return pubList == null ? null : pubList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return pubList == null ? 0 : pubList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.pub_list_item, parent, false);
            ViewHolder vh = new ViewHolder();
            vh.title = (TextView) view.findViewById(R.id.title);
            vh.readableTitle = (TextView) view.findViewById(R.id.readable_title);
            vh.status = (TextView) view.findViewById(R.id.status);
            vh.lastUpdated = (TextView) view.findViewById(R.id.last_updated);
            view.setTag(vh);
        }

        ViewHolder vh = (ViewHolder) view.getTag();
        Pub pub = getItem(position);
        vh.title.setText(pub.getTitle());
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

        ((View) (vh.status.getParent())).setEnabled(disableRow);

        return view;
    }

    private class ViewHolder {
        public TextView title;
        public TextView readableTitle;
        public TextView lastUpdated;
        public TextView status;
    }
}
