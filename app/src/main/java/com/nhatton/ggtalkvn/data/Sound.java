package com.nhatton.ggtalkvn.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Sound implements Comparable<Sound>, Parcelable {

    public static final Parcelable.Creator<Sound> CREATOR = new Parcelable.Creator<Sound>() {
        @Override
        public Sound createFromParcel(Parcel source) {
            return new Sound(source);
        }

        @Override
        public Sound[] newArray(int size) {
            return new Sound[size];
        }
    };
    @Id(autoincrement = true)
    private Long id;
    private String text;
    private float speed;
    private float pitch;

    @Generated(hash = 1795596526)
    public Sound(Long id, String text, float speed, float pitch) {
        this.id = id;
        this.text = text;
        this.speed = speed;
        this.pitch = pitch;
    }

    @Generated(hash = 127056582)
    public Sound() {
    }

    private Sound(Builder builder) {
        setText(builder.text);
        setSpeed(builder.speed);
        setPitch(builder.pitch);
    }

    protected Sound(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.text = in.readString();
        this.speed = in.readFloat();
        this.pitch = in.readFloat();
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sound sound = (Sound) o;

        return text != null ? text.equals(sound.text) : sound.text == null;
    }

    @Override
    public int hashCode() {
        return text != null ? text.hashCode() : 0;
    }

    @Override
    public int compareTo(@NonNull Sound o) {
        return id.compareTo(o.id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.text);
        dest.writeFloat(this.speed);
        dest.writeFloat(this.pitch);
    }

    public static final class Builder {
        private String text;
        private float speed;
        private float pitch;

        public Builder() {
        }

        public Builder text(String val) {
            text = val;
            return this;
        }

        public Builder speed(float val) {
            speed = val;
            return this;
        }

        public Builder pitch(float val) {
            pitch = val;
            return this;
        }

        public Sound build() {
            return new Sound(this);
        }
    }
}
