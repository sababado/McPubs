package com.sababado.mcpubs.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.sababado.ezprovider.Column;
import com.sababado.ezprovider.Id;
import com.sababado.ezprovider.Table;
import com.sababado.mcpubs.models.Constants.PubType;
import com.sababado.mcpubs.models.Constants.SaveStatus;
import com.sababado.mcpubs.models.Constants.UpdateStatus;

import java.util.Arrays;

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
    @Column(6)
    private long pubServerId;
    @Column(7)
    private String oldTitle;
    @Column(8)
    @PubType
    private int pubType;
    @Column(9)
    @Constants.SaveStatus
    private int saveStatus;

    public Pub() {
        saveStatus = Constants.SAVE_STATUS_SAVING;
    }

    public Pub(Cursor cursor) {
        id = cursor.getLong(0);
        title = cursor.getString(1);
        readableTitle = cursor.getString(2);
        isActive = cursor.getInt(3) == 1;
        //noinspection WrongConstant
        updateStatus = cursor.getInt(4);
        lastUpdated = cursor.getLong(5);
        pubServerId = cursor.getLong(6);
        oldTitle = cursor.getString(7);
        //noinspection WrongConstant
        pubType = cursor.getInt(8);
        //noinspection WrongConstant
        saveStatus = cursor.getInt(9);
    }

    public Pub(Parcel in) {
        id = in.readLong();
        title = in.readString();
        readableTitle = in.readString();
        isActive = in.readByte() == 0x01;
        //noinspection WrongConstant
        updateStatus = in.readInt();
        lastUpdated = in.readLong();
        pubServerId = in.readLong();
        oldTitle = in.readString();
        //noinspection WrongConstant
        pubType = in.readInt();
        //noinspection WrongConstant
        saveStatus = in.readInt();
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues(5);
        values.put("title", title);
        values.put("readableTitle", readableTitle);
        values.put("isActive", isActive);
        values.put("updateStatus", updateStatus);
        values.put("lastUpdated", lastUpdated);
        values.put("pubServerId", pubServerId);
        values.put("oldTitle", oldTitle);
        values.put("pubType", pubType);
        values.put("saveStatus", saveStatus);
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
        dest.writeLong(pubServerId);
        dest.writeString(oldTitle);
        dest.writeInt(pubType);
        dest.writeInt(saveStatus);
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

    @UpdateStatus
    public int getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(@UpdateStatus int updateStatus) {
        this.updateStatus = updateStatus;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getPubServerId() {
        return pubServerId;
    }

    public void setPubServerId(long pubServerId) {
        this.pubServerId = pubServerId;
    }

    public String getOldTitle() {
        return oldTitle;
    }

    public void setOldTitle(String oldTitle) {
        this.oldTitle = oldTitle;
    }

    @PubType
    public int getPubType() {
        return pubType;
    }

    public void setPubType(String pubType) {
        int i = Arrays.binarySearch(Constants.PUB_TYPES, pubType);
        setPubType(Constants.PUB_TYPE_VALS[i]);
    }

    public void setPubType(@PubType int pubType) {
        this.pubType = pubType;
    }

    @SaveStatus
    public int getSaveStatus() {
        return saveStatus;
    }

    public void setSaveStatus(@SaveStatus int saveStatus) {
        this.saveStatus = saveStatus;
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
        if (pubServerId != pub.pubServerId) return false;
        if (pubType != pub.pubType) return false;
        if (saveStatus != pub.saveStatus) return false;
        if (title != null ? !title.equals(pub.title) : pub.title != null) return false;
        if (readableTitle != null ? !readableTitle.equals(pub.readableTitle) : pub.readableTitle != null)
            return false;
        return oldTitle != null ? oldTitle.equals(pub.oldTitle) : pub.oldTitle == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (readableTitle != null ? readableTitle.hashCode() : 0);
        result = 31 * result + (isActive ? 1 : 0);
        result = 31 * result + updateStatus;
        result = 31 * result + (int) (lastUpdated ^ (lastUpdated >>> 32));
        result = 31 * result + (int) (pubServerId ^ (pubServerId >>> 32));
        result = 31 * result + (oldTitle != null ? oldTitle.hashCode() : 0);
        result = 31 * result + pubType;
        result = 31 * result + saveStatus;
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
                ", pubServerId=" + pubServerId +
                ", oldTitle='" + oldTitle + '\'' +
                ", pubType=" + pubType +
                ", saveStatus=" + saveStatus +
                '}';
    }
}
