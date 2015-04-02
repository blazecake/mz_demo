package my.android.app.chooseimages;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import my.android.app.chooseimages.adapter.ImagesAdapter;
import my.android.app.chooseimages.bean.Photo;
import my.android.app.chooseimages.utils.ImageManager;

/**
 * 浏览某一个目录下的所有图片。<br/>
 * <br/>
 * Created by yanglw on 2014/8/17.
 */
public class ImagesActivity extends ImageBaseActivity implements LoaderManager.LoaderCallbacks<Cursor>
{
    public static final String ARG_DIR_ID = "my.android.app.chooseimages.DIR_ID";
    public static final String ARG_DIR_NAME = "my.android.app.chooseimages.DIR_NAME";
    private GridView mGridView;

    private ImagesAdapter mAdapter;

    private String mDirId;

    private boolean mIsEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_images);

        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Photo photo = (Photo) parent.getItemAtPosition(position);
                if (photo == null)
                {
                    return;
                }

                if (!checkList.contains(photo))
                {
                    if (checkList.size() >= MyApplication.MAX_SIZE)
                    {
                        Toast.makeText(getApplicationContext(),
                                       getString(R.string.tip_max_size, MyApplication.MAX_SIZE),
                                       Toast.LENGTH_SHORT)
                             .show();
                        return;
                    }
                }
                mAdapter.setCheck(position, view);

                setBtnEnable(!checkList.isEmpty());
            }
        });
        mGridView.setOnScrollListener(ImageManager.pauseScrollListener);

        Intent intent = getIntent();
        mDirId = intent.getStringExtra(ARG_DIR_ID);
        setTitle(intent.getStringExtra(ARG_DIR_NAME));

        getSupportLoaderManager().initLoader(0, null, this);

        setBtnEnable(!checkList.isEmpty());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            setResult(RESULT_OK);
            finish();
            return;
        }
        if (resultCode == MyApplication.RESULT_CHANGE)
        {
            mAdapter.notifyDataSetChanged();
            setBtnEnable(!checkList.isEmpty());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.images, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (!mIsEnable)
        {
            return super.onOptionsItemSelected(item);
        }
        int id = item.getItemId();
        if (id == R.id.preview)
        {
            startActivityForResult(new Intent(getApplicationContext(), PreviewActivity.class), 1);
            return true;
        }
        if (id == R.id.num)
        {
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        if (mIsEnable)
        {
            menu.findItem(R.id.num).setTitle(String.valueOf(checkList.size()));
        }

        menu.findItem(R.id.num).setVisible(mIsEnable);
        menu.findItem(R.id.preview).setVisible(mIsEnable);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(getApplicationContext(),
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                new String[]{
                                        MediaStore.Images.Media.DATA//图片地址
                                },
                                mDirId == null ? null : MediaStore.Images.Media.BUCKET_ID + "=" + mDirId,
                                null,
                                MediaStore.Images.Media.DATE_MODIFIED + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        if (cursor.getCount() > 0)
        {
            ArrayList<Photo> list = new ArrayList<Photo>();

            cursor.moveToPosition(-1);
            while (cursor.moveToNext())
            {
                Photo photo = new Photo();

                photo.imgPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                list.add(photo);
            }

            mAdapter = new ImagesAdapter(getApplicationContext(), list, checkList);
            mGridView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {

    }

    private void setBtnEnable(boolean enable)
    {
        mIsEnable = enable;
        if (Build.VERSION.SDK_INT >= 11)
        {
            invalidateOptionsMenu();
        }
    }
}
