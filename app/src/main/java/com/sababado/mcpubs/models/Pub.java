package com.sababado.mcpubs.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.sababado.ezprovider.Column;
import com.sababado.ezprovider.Id;
import com.sababado.ezprovider.Table;
import com.sababado.mcpubs.models.Constants.UpdateStatus;

/**
 * Created by robert on 8/29/16.
 */
@Table(name = "Pub", code = 1)
public class Pub implements Parcelable {
    @Id
    private long id;
    @Column(1)
    private String title;
    @Column(2)
    private String readableTitle;
    @Column(3)
    private boolean isActive;
    @Column(4)
    @UpdateStatus
    private int updateStatus;
    @Column(5)
    private long lastUpdated;

    public Pub() {

    }

    public Pub(Cursor cursor) {
        id = cursor.getLong(0);
        title = cursor.getString(1);
        readableTitle = cursor.getString(2);
        isActive = cursor.getInt(3) == 1;
        //noinspection WrongConstant
        updateStatus = cursor.getInt(4);
        lastUpdated = cursor.getLong(5);
    }

    public Pub(Parcel in) {
        id = in.readLong();
        title = in.readString();
        readableTitle = in.readString();
        isActive = in.readByte() == 0x01;
        //noinspection WrongConstant
        updateStatus = in.readInt();
        lastUpdated = in.readLong();
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues(5);
        values.put("title", title);
        values.put("readableTitle", readableTitle);
        values.put("isActive", isActive);
        values.put("updateStatus", updateStatus);
        values.put("lastUpdated", lastUpdated);
        return values;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(readableTitle);
        dest.writeByte((byte) (isActive ? 0x01 : 0x00));
        dest.writeInt(updateStatus);
        dest.writeLong(lastUpdated);
    }

    public static final Creator<Pub> CREATOR = new Creator<Pub>() {
        @Override
        public Pub createFromParcel(Parcel source) {
            return new Pub(source);
        }

        @Override
        public Pub[] newArray(int size) {
            return new Pub[0];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReadableTitle() {
        return readableTitle;
    }

    public void setReadableTitle(String readableTitle) {
        this.readableTitle = readableTitle;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(int updateStatus) {
        this.updateStatus = updateStatus;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pub pub = (Pub) o;

        if (id != pub.id) return false;
        if (isActive != pub.isActive) return false;
        if (updateStatus != pub.updateStatus) return false;
        if (lastUpdated != pub.lastUpdated) return false;
        if (title != null ? !title.equals(pub.title) : pub.title != null) return false;
        return readableTitle != null ? readableTitle.equals(pub.readableTitle) : pub.readableTitle == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (readableTitle != null ? readableTitle.hashCode() : 0);
        result = 31 * result + (isActive ? 1 : 0);
        result = 31 * result + updateStatus;
        result = 31 * result + (int) (lastUpdated ^ (lastUpdated >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Pub{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", readableTitle='" + readableTitle + '\'' +
                ", isActive=" + isActive +
                ", updateStatus=" + updateStatus +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
