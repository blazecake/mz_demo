package my.android.app.chooseimages.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import my.android.app.chooseimages.R;
import my.android.app.chooseimages.bean.Photo;
import my.android.app.chooseimages.utils.ImageManager;

/**
 * 首页图片已经选择的图片列表。<br/>
 * <br/>
 * Created by yanglw on 2014/8/17.
 */
public class MainAdapter extends MyBaseAdapter<Photo>
{
    public MainAdapter(Context context, List<Photo> list)
    {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView imageView;
        if (convertView == null)
        {
            imageView = (ImageView) mInflater.inflate(R.layout.i_main, parent, false);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        ImageManager.imageLoader.displayImage("file:///" + mList.get(position).imgPath,
                                              imageView,
                                              ImageManager.options);
        return imageView;
    }
}
