package my.android.app.chooseimages;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import my.android.app.chooseimages.utils.ImageManager;

/**
 * 用于初始化ImageLoader以及定义一些常量。<br/>
 * <br/>
 * Created by yanglw on 2014/8/15.
 */
public class MyApplication extends Application
{
    /** 表示通过Intent传递到下一个Activity的图片列表 */
    public static final String ARG_PHOTO_LIST = "my.android.app.chooseimages.PHOTO_LIST";
    /** 表示通过Intent传递到上一个Activity的图片列表 */
    public static final String RES_PHOTO_LIST = "my.android.app.chooseimages.PHOTO_LIST";

    /** 表示选择的图片发生了变化 */
    public static final int RESULT_CHANGE = 10010;

    /** 最多能够选择的图片个数 */
    public static final int MAX_SIZE = 20;

    @Override
    public void onCreate()
    {
        super.onCreate();

        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(3)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

        ImageManager.init();
    }
}
