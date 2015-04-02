package my.android.app.chooseimages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import my.android.app.chooseimages.adapter.MainAdapter;
import my.android.app.chooseimages.bean.Photo;

/**
 * 首页。<br/>
 * <br/>
 * Created by yanglw on 2014/8/17.
 */
public class MainActivity extends ActionBarActivity
{
    private ArrayList<Photo> mList;
    private MainAdapter mAdapter;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);

        mTextView = (TextView) findViewById(R.id.tv);
        GridView gridView = (GridView) findViewById(R.id.gridview);
        mTextView.setText(getString(R.string.check_length, 0));

        mList = new ArrayList<Photo>();
        mAdapter = new MainAdapter(getApplicationContext(), mList);
        gridView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
        {
            if (data != null)
            {
                ArrayList<Photo> list = data.getParcelableArrayListExtra(MyApplication.RES_PHOTO_LIST);

                mList.clear();
                if (list != null)
                {
                    mList.addAll(list);
                }

                mAdapter.notifyDataSetChanged();
                mTextView.setText(getString(R.string.check_length, mAdapter.getCount()));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.add)
        {
            Intent intent = new Intent(getApplicationContext(), ImageDirActivity.class);
            intent.putExtra(MyApplication.ARG_PHOTO_LIST, mList);
            startActivityForResult(intent, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
