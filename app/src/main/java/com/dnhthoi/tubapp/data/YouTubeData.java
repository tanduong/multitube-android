package com.dnhthoi.tubapp.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
/**
 * Created by ant on 02/03/2016.
 */
public class YouTubeData  extends RealmObject {

    @PrimaryKey
    private String          url;
    private String          title;
    private String          thumbnails;
    public String getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(String thumbnails) {
        this.thumbnails = thumbnails;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
