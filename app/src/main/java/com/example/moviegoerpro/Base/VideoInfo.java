package com.example.moviegoerpro.Base;

public class VideoInfo {
    private String videoname;
    private String videotype;
    private String videourl;

    public String getVideoname() {
        return videoname;
    }

    public void setVideoname(String videoname) {
        this.videoname = videoname;
    }

    public String getVideotype() {
        return videotype;
    }

    public void setVideotype(String videotype) {
        this.videotype = videotype;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "videoname='" + videoname + '\'' +
                ", videotype='" + videotype + '\'' +
                ", videourl='" + videourl + '\'' +
                '}';
    }
}
