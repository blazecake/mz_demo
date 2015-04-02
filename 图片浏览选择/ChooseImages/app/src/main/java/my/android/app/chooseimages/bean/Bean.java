package my.android.app.chooseimages.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * JavaBean的基类。<br/>
 * <br/>
 * Created by yanglw on 2014/8/15.
 */
public class Bean implements Parcelable
{
    public String id;
    public String text;
    public String imgPath;

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(text);
        dest.writeString(imgPath);
    }

    public static <T extends Bean> T readFromParcel(Parcel in, T t)
    {
        if (t == null)
        {
            return null;
        }

        if (in == null)
        {
            return null;
        }

        t.id = in.readString();
        t.text = in.readString();
        t.imgPath = in.readString();
        return t;
    }

    public static final Parcelable.Creator<Bean> CREATOR
            = new Parcelable.Creator<Bean>()
    {
        public Bean createFromParcel(Parcel in)
        {
            return readFromParcel(in, new Bean());
        }

        public Bean[] newArray(int size)
        {
            return new Bean[size];
        }
    };
}
