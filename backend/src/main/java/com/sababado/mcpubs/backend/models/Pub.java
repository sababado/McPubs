package com.sababado.mcpubs.backend.models;

import com.googlecode.objectify.annotation.Id;
import com.sababado.mcpubs.backend.db.utils.Column;
import com.sababado.mcpubs.backend.db.utils.DbRecord;
import com.sababado.mcpubs.backend.db.utils.DbUtils;
import com.sababado.mcpubs.backend.db.utils.TableName;
import com.sababado.mcpubs.backend.utils.StringUtils;
import com.sababado.mcpubs.backend.utils.UnrecognizedPubException;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by robert on 8/25/16.
 */
@TableName("Pub")
public class Pub extends DbRecord {
    public static final String FULL_CODE = "fullCode";
    public static final String ROOT_CODE = "rootCode";
    public static final String CODE = "code";
    public static final String VERSION = "version";
    public static final String IS_ACTIVE = "isActive";
    public static final String PUB_TYPE = "pubType";
    public static final String TITLE = "title";
    public static final String READABLE_TITLE = "readableTitle";
    public static final String LAST_UPDATED = "lastUpdated";

    public static final int DOCTRINE_UNSUPPORTED = 2002;
    public static final int MCO = 2005;
    public static final int MCO_P = 2006;
    public static final int NAVMC = 2008;
    public static final int NAVMC_DIR = 2009;

    @Id
    @Column(Column.ID)
    long id;
    @Column(FULL_CODE)
    String fullCode;
    @Column(ROOT_CODE)
    String rootCode;
    @Column(CODE)
    int code;
    @Column(VERSION)
    String version;
    @Column(IS_ACTIVE)
    boolean isActive;
    @Column(PUB_TYPE)
    int pubType;
    @Column(TITLE)
    String title;
    @Column(READABLE_TITLE)
    String readableTitle;
    @Column(value = LAST_UPDATED, ignoreInQueryGenerator = true)
    long lastUpdated;

    public Pub() {
        code = -1;
    }

    public Pub(String title, String readableTitle, boolean isActive, int pubType) throws UnrecognizedPubException {
        this();
        this.isActive = isActive;
        this.readableTitle = readableTitle;
        this.pubType = pubType;
        setTitle(title);
    }

    public Pub(ResultSet resultSet) throws SQLException {
        this(resultSet, false);
    }

    public Pub(ResultSet resultSet, boolean fromJoin) throws SQLException {
        super(resultSet);
        id = resultSet.getLong(fromJoin ? DbUtils.getFkColumnName(ID, "Pub") : ID);
        fullCode = resultSet.getString(FULL_CODE);
        rootCode = resultSet.getString(ROOT_CODE);
        code = resultSet.getInt(CODE);
        version = resultSet.getString(VERSION);
        isActive = resultSet.getBoolean(IS_ACTIVE);
        pubType = resultSet.getInt(PUB_TYPE);
        title = resultSet.getString(TITLE);
        readableTitle = resultSet.getString(READABLE_TITLE);
        lastUpdated = resultSet.getDate(LAST_UPDATED).getTime();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullCode() {
        return fullCode;
    }

    public void setFullCode(String fullCode) {
        this.fullCode = fullCode;
    }

    public String getRootCode() {
        return rootCode;
    }

    public void setRootCode(String rootCode) {
        this.rootCode = rootCode;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getPubType() {
        return pubType;
    }

    public void setPubType(int pubType) {
        this.pubType = pubType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws UnrecognizedPubException {
        this.title = title;

        String[] titleParts = title.split("\\s");
        if (titleParts.length > 1) {
            fullCode = titleParts[1];
            String secondLine = titleParts[1];

            if (pubType == MCO ||
                    pubType == MCO_P ||
                    pubType == NAVMC) {
                fullCode = secondLine;
                parseMcoTitle(fullCode);
            } else if (pubType == NAVMC_DIR) {
                fullCode = titleParts[2];
                parseMcoTitle(fullCode);
            } else if (pubType == DOCTRINE_UNSUPPORTED) {
                rootCode = fullCode = secondLine;
            } else {
                throw new UnrecognizedPubException(title);
            }
        } else {
            throw new UnrecognizedPubException(title);
        }
    }

    public String getReadableTitle() {
        return readableTitle;
    }

    public void setReadableTitle(String readableTitle) {
        this.readableTitle = readableTitle;
    }

    public static String getInsertQuery() {
        return getInsertQuery(Pub.class);
    }

    public static String getUpdateQuery() {
        return getUpdateQuery(Pub.class);
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * Helper method to parse a title that appears to be an MCO or MCO P
     *
     * @param fullCode
     */
    private void parseMcoTitle(String fullCode) {
        String[] parts = fullCode.trim().split("\\.");
        rootCode = parts[0];

        String temp = parts[1].split(StringUtils.REGEX_ANY_LETTER)[0];
        code = Integer.parseInt(temp);
        version = parts[1].replace(temp, "");
        if (version.length() == 0) {
            version = null;
        }

        this.fullCode = rootCode + "." + code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pub pub = (Pub) o;

        if (id != pub.id) return false;
        if (code != pub.code) return false;
        if (isActive != pub.isActive) return false;
        if (pubType != pub.pubType) return false;
        if (lastUpdated != pub.lastUpdated) return false;
        if (fullCode != null ? !fullCode.equals(pub.fullCode) : pub.fullCode != null) return false;
        if (rootCode != null ? !rootCode.equals(pub.rootCode) : pub.rootCode != null) return false;
        if (version != null ? !version.equals(pub.version) : pub.version != null) return false;
        if (title != null ? !title.equals(pub.title) : pub.title != null) return false;
        return readableTitle != null ? readableTitle.equals(pub.readableTitle) : pub.readableTitle == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (fullCode != null ? fullCode.hashCode() : 0);
        result = 31 * result + (rootCode != null ? rootCode.hashCode() : 0);
        result = 31 * result + code;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (isActive ? 1 : 0);
        result = 31 * result + pubType;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (readableTitle != null ? readableTitle.hashCode() : 0);
        result = 31 * result + (int) (lastUpdated ^ (lastUpdated >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Pub{" +
                "id=" + id +
                ", fullCode='" + fullCode + '\'' +
                ", rootCode='" + rootCode + '\'' +
                ", code=" + code +
                ", version='" + version + '\'' +
                ", isActive=" + isActive +
                ", pubType=" + pubType +
                ", title='" + title + '\'' +
                ", readableTitle='" + readableTitle + '\'' +
                ", lastUpdated=" + lastUpdated +
                "} " + super.toString();
    }
}
