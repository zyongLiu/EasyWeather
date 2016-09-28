package com.liu.easyweather.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.liu.easyweather.R;
import com.liu.easyweather.bean.CityInfo;
import com.liu.easyweather.utils.PreferencesUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Liu on 2016-09-20.
 */
public class CityListActivity extends CheckPermissionsActivity {
    private EditText edt_search;
    private GridView gdv_hotcities;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citylist);
        initView();
        initEvent();
    }

    private void initEvent() {
        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i== EditorInfo.IME_ACTION_SEARCH){
                    // 先隐藏键盘
                    ((InputMethodManager) edt_search.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(
                                    CityListActivity.this
                                            .getCurrentFocus()
                                            .getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);


                    String keyWord=textView.getText().toString();

                    BmobQuery<CityInfo> query = new BmobQuery<CityInfo>();
                    //查询playerName叫“比目”的数据
                    query.addWhereContains("city",keyWord);
                    //返回50条数据，如果不加上这条语句，默认返回10条数据
                    query.setLimit(50);
                    //执行查询方法
                    query.findObjects(CityListActivity.this, new FindListener<CityInfo>() {
                        @Override
                        public void onSuccess(List<CityInfo> list) {
                            final String[] names=new String[list.size()];
                            for (int i=0;i<list.size();i++) {
                                names[i]=list.get(i).toString();
                            }

                            new AlertDialog.Builder(CityListActivity.this).setTitle("列表对话框")
                                    .setItems(names, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            String data=PreferencesUtils.getString(getApplicationContext(),"CityNames","");

                                            PreferencesUtils.putString(getApplicationContext(),"CityNames",
                                                    data+";"+names[which].split(",")[0]);
//                                            Toast.makeText(CityListActivity.this,names[which],Toast.LENGTH_SHORT).show();
                                            CityListActivity.this.finish();
                                        }
                                    }).setNegativeButton("取消", null).show();
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.i("TAG","查询失败");
                        }
                    });
                    return true;
                }
                return false;
            }
        });
    }

    private void initView() {
        edt_search= (EditText) findViewById(R.id.edt_search);
        gdv_hotcities= (GridView) findViewById(R.id.gv_hotcity);
    }
}
