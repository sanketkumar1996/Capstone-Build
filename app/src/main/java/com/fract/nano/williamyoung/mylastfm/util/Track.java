package com.fract.nano.williamyoung.mylastfm.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Track implements Parcelable {
    private String artist;
    private String album;
    private String trackName;
    private int length;
    private String image;
    private String albumCover;
    private String bandUrl;

    /**
     * Factory to create new Track object indirectly
     * @param artist : Artist Name String
     * @param album : Album Name String
     * @param trackName : Track Name String
     * @param length : Duration of Track int
     * @param image : Image URL
     * @param albumCover : Album Cover Image URL
     * @param bandUrl : Last.FM Band URL
     * @return new initialized Track
     */
    public static Track newInstance(String artist, String album, String trackName, int length, String image, String albumCover, String bandUrl) {
        Track track = new Track();

        track.setArtist(artist);
        track.setAlbum(album);
        track.setTrackName(trackName);
        track.setLength(length);
        track.setImage(image);
        track.setAlbumCover(albumCover);
        track.setBandUrl(bandUrl);

        return track;
    }

    public String getArtist() { return artist; }

    public void setArtist(String artist) { this.artist = artist; }

    public String getAlbum() { return album; }

    public void setAlbum(String album) { this.album = album; }

    public String getTrackName() { return trackName; }

    public void setTrackName(String trackName) { this.trackName = trackName; }

    public int getLength() { return length; }

    public void setLength(int length) { this.length = length; }

    /**
     * Formats track object duration
     * @return : Track Duration in Min:Sec format
     */
    public String getFormattedLength() {
        return String.format(Locale.US, "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(length),
            TimeUnit.MILLISECONDS.toSeconds(length) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(length))
        );
    }

    public String getImage() { return image; }

    public void setImage(String imageUrl) { this.image = imageUrl; }

    public String getAlbumCover() { return albumCover; }

    public void setAlbumCover(String albumCover) { this.albumCover = albumCover; }

    public String getBandUrl() { return bandUrl; }

    public void setBandUrl(String bandUrl) { this.bandUrl = bandUrl; }

    public static Parcelable.Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel source) {
            return Track.newInstance(
                source.readString(), // Artist Name
                source.readString(), // Album Name
                source.readString(), // Track Name
                source.readInt(),    // Track Duration
                source.readString(), // Image URL
                source.readString(), // Album Cover Image URL
                source.readString()  // Last.FM Band URL
            );
        }

        @Override
        public Track[] newArray(int size) { return new Track[size]; }
    };

    public int describeContents() { return 0; }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(artist);
        parcel.writeString(album);
        parcel.writeString(trackName);
        parcel.writeInt(length);
        parcel.writeString(image);
        parcel.writeString(albumCover);
        parcel.writeString(bandUrl);
    }

    /**
     * Compares track equality based on Artist Name, Album Name, and Track Name
     * @param track : Track object to be compared with
     * @return : Equality of track object to other track object
     */
    @Override
    public boolean equals(Object track) {
        boolean retVal = false;

        if (track instanceof Track) {
            Track ptr = (Track) track;
            retVal = (ptr.artist.equals(this.artist)) &&
                     (ptr.album.equals(this.album)) &&
                     (ptr.trackName.equals(this.trackName));
        }

        return retVal;
    }
}