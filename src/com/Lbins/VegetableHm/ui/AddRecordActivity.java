package com.Lbins.VegetableHm.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.Lbins.VegetableHm.R;
import com.Lbins.VegetableHm.adapter.Publish_mood_GridView_Adapter;
import com.Lbins.VegetableHm.base.BaseActivity;
import com.Lbins.VegetableHm.base.InternetURL;
import com.Lbins.VegetableHm.data.RecordSingData;
import com.Lbins.VegetableHm.util.*;
import com.Lbins.VegetableHm.widget.CustomerSpinner;
import com.Lbins.VegetableHm.widget.NoScrollGridView;
import com.Lbins.VegetableHm.widget.SelectPhoPop;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/2/18.
 */
public class AddRecordActivity extends BaseActivity implements View.OnClickListener {
    private final static int SELECT_LOCAL_PHOTO = 110;
    private NoScrollGridView publish_moopd_gridview_image;//图片
    private Publish_mood_GridView_Adapter adapter;

    private ArrayList<String> dataList = new ArrayList<String>();
    private ArrayList<String> tDataList = new ArrayList<String>();
    private List<String> uploadPaths = new ArrayList<String>();

    private Uri uri;
    private SelectPhoPop selectPhoPop;

    private ImageView add_video;

    //内容
//    private EditText mm_msg_title;
    private EditText mm_msg_content;
    private CustomerSpinner msgTypeSpinner;
    private String mm_msg_type;
    ArrayAdapter<String> adapterEmpType;
    private ArrayList<String> empTypeList = new ArrayList<String>();

    AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_record_activity);
        initView();
    }

    void initView() {
        dataList.add("camera_default");
        this.findViewById(R.id.back).setOnClickListener(this);
        add_video = (ImageView) this.findViewById(R.id.add_video);
        add_video.setOnClickListener(this);
//        mm_msg_title = (EditText) this.findViewById(R.id.mm_msg_title);
        mm_msg_content = (EditText) this.findViewById(R.id.mm_msg_content);
        msgTypeSpinner = (CustomerSpinner) this.findViewById(R.id.mm_msg_type);
        msgTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mm_msg_type = empTypeList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        empTypeList.add(getResources().getString(R.string.please_select_msg_type));
        empTypeList.add(getResources().getString(R.string.type_qiugou));
        empTypeList.add(getResources().getString(R.string.type_gongying));
        adapterEmpType = new ArrayAdapter<String>(AddRecordActivity.this, android.R.layout.simple_spinner_item, empTypeList);
        msgTypeSpinner.setList(empTypeList);
        msgTypeSpinner.setAdapter(adapterEmpType);

        publish_moopd_gridview_image = (NoScrollGridView) this.findViewById(R.id.publish_moopd_gridview_image);
        adapter = new Publish_mood_GridView_Adapter(this, dataList);
        publish_moopd_gridview_image.setAdapter(adapter);
        publish_moopd_gridview_image.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String path = dataList.get(position);
                if (path.contains("camera_default")) {
                    showSelectImageDialog();
                } else {
                    Intent intent = new Intent(AddRecordActivity.this, ImageDelActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("path", dataList.get(position));
                    startActivityForResult(intent, CommonDefine.DELETE_IMAGE);
                }
            }
        });
        this.findViewById(R.id.btn).setOnClickListener(this);
        this.findViewById(R.id.btn_kf).setOnClickListener(this);
        this.findViewById(R.id.btn_video).setOnClickListener(this);
    }

    boolean isMobileNet, isWifiNet;

    @Override
    public void onClick(View view) {
        try {
            isMobileNet = HttpUtils.isMobileDataEnable(getApplicationContext());
            isWifiNet = HttpUtils.isWifiDataEnable(getApplicationContext());
            if (!isMobileNet && !isWifiNet) {
                Toast.makeText(this, R.string.net_work_error, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_kf:
                //联系客服
                Intent kefuV = new Intent(AddRecordActivity.this, SelectTelActivity.class);
                startActivity(kefuV);
                break;
            case R.id.btn:
                //先判断权限
                if ("1".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class))) {
                    //已登录

                } else {
                    //未登录
                    Intent loginV = new Intent(AddRecordActivity.this, LoginActivity.class);
                    startActivity(loginV);
                    return;
                }
                if ("0".equals(getGson().fromJson(getSp().getString("is_fabugongying", ""), String.class)) && getResources().getString(R.string.type_gongying).equals(mm_msg_type)) {
                    //没有发布蔬菜供应的权限
                    showMsg(AddRecordActivity.this, getResources().getString(R.string.add_error_one));
                    return;
                }
                if ("0".equals(getGson().fromJson(getSp().getString("is_fabuqiugou", ""), String.class)) && getResources().getString(R.string.type_qiugou).equals(mm_msg_type)) {
                    //没有发布蔬菜供应的权限
                    showMsg(AddRecordActivity.this, getResources().getString(R.string.add_error_two));
                    return;
                }

                if (StringUtil.isNullOrEmpty(mm_msg_type) || getResources().getString(R.string.add_error_three).equals(mm_msg_type)) {
                    showMsg(AddRecordActivity.this, getResources().getString(R.string.please_select_msg_type));
                    return;
                }
//                if(StringUtil.isNullOrEmpty(mm_msg_title.getText().toString())){
//                    showMsg(AddRecordActivity.this, "请输入标题");
//                    return;
//                }
                if (StringUtil.isNullOrEmpty(mm_msg_content.getText().toString())) {
                    showMsg(AddRecordActivity.this, getResources().getString(R.string.please_input_text));
                    return;
                }
//                if(mm_msg_title.getText().toString().length() > 100){
//                    showMsg(AddRecordActivity.this, "标题100字以内");
//                    return;
//                }

                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_msg_length", ""), String.class))) {
                    //发布信息长度不为空
                    if (mm_msg_content.getText().toString().length() > Integer.parseInt(getGson().fromJson(getSp().getString("mm_msg_length", ""), String.class))) {
                        showMsg(AddRecordActivity.this, getResources().getString(R.string.add_error_four));
                        return;
                    }
                }

                if ("0".equals(getGson().fromJson(getSp().getString("is_pic", ""), String.class)) && dataList.size() > 1) {
                    //不允许发布图片
                    showMsg(AddRecordActivity.this, getResources().getString(R.string.add_error_five));
                    return;
                }
                if ("1".equals(getGson().fromJson(getSp().getString("is_pic", ""), String.class)) && dataList.size() > 4) {
                    //允许发布图片 3
                    showMsg(AddRecordActivity.this, getResources().getString(R.string.add_error_six));
                    return;
                }
                if ("2".equals(getGson().fromJson(getSp().getString("is_pic", ""), String.class)) && dataList.size() > 7) {
                    //允许发布图片 6
                    showMsg(AddRecordActivity.this, getResources().getString(R.string.add_error_seven));
                    return;
                }
                if ("3".equals(getGson().fromJson(getSp().getString("is_pic", ""), String.class)) && dataList.size() > 10) {
                    //允许发布图片 9
                    showMsg(AddRecordActivity.this, getResources().getString(R.string.add_error_eight));
                    return;
                }
//                if (StringUtil.isNullOrEmpty((getGson().fromJson(getSp().getString("mm_emp_msg_num", ""), String.class)))) {
//                    String str = getResources().getString(R.string.add_msg_one )+ (getGson().fromJson(getSp().getString("mm_emp_msg_num", ""), String.class)) + getResources().getString(R.string.tiao);
//                    showMsg(AddRecordActivity.this, str);
//                    return;
//                }
                progressDialog = new ProgressDialog(AddRecordActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                //检查有没有选择图片
                if (dataList.size() <= 1) {
                    addRecord();
                    return;
                } else {
                    for (int i = 1; i < dataList.size(); i++) {
                        //七牛
                        Bitmap bm = FileUtils.getSmallBitmap(dataList.get(i));
                        final String cameraImagePath = FileUtils.saveBitToSD(bm, System.currentTimeMillis() + ".jpg");
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("space", InternetURL.QINIU_SPACE);
                        RequestParams params = new RequestParams(map);
                        client.get(InternetURL.UPLOAD_TOKEN, params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                try {
                                    String token = response.getString("data");
                                    UploadManager uploadManager = new UploadManager();
                                    uploadManager.put(StringUtil.getBytes(cameraImagePath), StringUtil.getUUID(), token,
                                            new UpCompletionHandler() {
                                                @Override
                                                public void complete(String key, ResponseInfo info, JSONObject response) {
                                                    //key
                                                    uploadPaths.add(key);
                                                    if (uploadPaths.size() == (dataList.size() - 1)) {
                                                        publishAll();
                                                    }
                                                }
                                            }, null);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                            }
                        });
                    }
                }

                break;
            case R.id.add_video:
                //拍摄视频
                Intent videoV = new Intent(AddRecordActivity.this, AddVideoActivity.class);
                startActivity(videoV);
                finish();
                break;
        }
    }

    // 选择相册，相机
    private void showSelectImageDialog() {
        if ("0".equals(getGson().fromJson(getSp().getString("is_pic", ""), String.class))) {
            //不允许发布图片
            showMsg(AddRecordActivity.this, getResources().getString(R.string.add_error_five));
        } else {
            //隐藏键盘
            selectPhoPop = new SelectPhoPop(AddRecordActivity.this, itemsOnClick);
            //显示窗口
            selectPhoPop.showAtLocation(AddRecordActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    private ArrayList<String> getIntentArrayList(ArrayList<String> dataList) {

        ArrayList<String> tDataList = new ArrayList<String>();

        for (String s : dataList) {
            if (!s.contains("camera_default")) {
                tDataList.add(s);
            }
        }
        return tDataList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        add_video.setVisibility(View.GONE);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_LOCAL_PHOTO:
                    tDataList = data.getStringArrayListExtra("datalist");
                    if (tDataList != null) {
                        for (int i = 0; i < tDataList.size(); i++) {
                            String string = tDataList.get(i);
                            dataList.add(string);
                        }
                        if (dataList.size() < 9) {
                            dataList.add("camera_default");
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        finish();
                    }
                    break;
                case CommonDefine.TAKE_PICTURE_FROM_CAMERA:
                    String sdStatus = Environment.getExternalStorageState();
                    if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                        return;
                    }
                    Bitmap bitmap = ImageUtils.getUriBitmap(this, uri, 400, 400);
                    String cameraImagePath = FileUtils.saveBitToSD(bitmap, System.currentTimeMillis() + ".jpg");
                    dataList.add("camera_default");
                    dataList.add(cameraImagePath);

                    adapter.notifyDataSetChanged();
                    break;
                case CommonDefine.TAKE_PICTURE_FROM_GALLERY:
                    tDataList = data.getStringArrayListExtra("datalist");
                    if (tDataList != null) {
                        dataList.clear();
                        dataList.add("camera_default");
                        for (int i = 0; i < tDataList.size(); i++) {
                            String string = tDataList.get(i);
                            dataList.add(string);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case CommonDefine.DELETE_IMAGE:
                    int position = data.getIntExtra("position", -1);
                    dataList.remove(position);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }


    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            selectPhoPop.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    Intent cameraIntent = new Intent();
                    cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    // 根据文件地址创建文件
                    File file = new File(CommonDefine.FILE_PATH);
                    if (file.exists()) {
                        file.delete();
                    }
                    uri = Uri.fromFile(file);
                    // 设置系统相机拍摄照片完成后图片文件的存放地址
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    // 开启系统拍照的Activity
                    startActivityForResult(cameraIntent, CommonDefine.TAKE_PICTURE_FROM_CAMERA);
                }
                break;
                case R.id.mapstorage: {
                    Intent intent = new Intent(AddRecordActivity.this, AlbumActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("dataList", getIntentArrayList(dataList));
//                    bundle.putString("editContent", et_sendmessage.getText().toString());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, CommonDefine.TAKE_PICTURE_FROM_GALLERY);
                }
                break;
                default:
                    break;
            }
        }

    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void addRecord() {
        //添加信息
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SEND_RECORD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    RecordSingData data = getGson().fromJson(s, RecordSingData.class);
                                    showMsg(AddRecordActivity.this, getResources().getString(R.string.add_record_success));
                                    if (getResources().getString(R.string.type_qiugou).equals(mm_msg_type)) {
                                        //调用广播，刷新主页
                                        Intent intent1 = new Intent(Constants.SEND_INDEX_SUCCESS_QIUGOU);
                                        intent1.putExtra("addRecord", data.getData());
                                        sendBroadcast(intent1);
                                    }
                                    if (getResources().getString(R.string.type_gongying).equals(mm_msg_type)) {
                                        //调用广播，刷新主页
                                        Intent intent1 = new Intent(Constants.SEND_INDEX_SUCCESS_GONGYING);
                                        intent1.putExtra("addRecord", data.getData());
                                        sendBroadcast(intent1);
                                    }

                                    finish();
                                } else if (Integer.parseInt(code) == 3) {
                                    String str = getResources().getString(R.string.add_msg_one )+ (getGson().fromJson(getSp().getString("mm_emp_msg_num", ""), String.class)) + getResources().getString(R.string.tiao);
                                    showMsg(AddRecordActivity.this, str);
                                } else if (Integer.parseInt(code) == 9) {
                                    Toast.makeText(AddRecordActivity.this, R.string.login_out, Toast.LENGTH_SHORT).show();
                                    save("password", "");
                                    Intent loginV = new Intent(AddRecordActivity.this, LoginActivity.class);
                                    startActivity(loginV);
                                    finish();
                                } else {
                                    Toast.makeText(AddRecordActivity.this, R.string.add_record_error_one, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(AddRecordActivity.this, R.string.add_record_error_one, Toast.LENGTH_SHORT).show();
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(AddRecordActivity.this, R.string.add_record_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_emp_id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                params.put("mm_emp_msg_num", getGson().fromJson(getSp().getString("mm_emp_msg_num", ""), String.class));
                params.put("mm_msg_title", "");
                params.put("mm_msg_content", mm_msg_content.getText().toString());
                if ("蔬菜求购".equals(mm_msg_type)) {
                    params.put("mm_msg_type", "0");
                }
                if ("蔬菜供应".equals(mm_msg_type)) {
                    params.put("mm_msg_type", "1");
                }
                params.put("mm_msg_picurl", "");
                params.put("provinceid", getGson().fromJson(getSp().getString("mm_emp_provinceId", ""), String.class));
                params.put("cityid", getGson().fromJson(getSp().getString("mm_emp_cityId", ""), String.class));
                params.put("countryid", getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class));

                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("access_token", ""), String.class))) {
                    params.put("accessToken", getGson().fromJson(getSp().getString("access_token", ""), String.class));
                } else {
                    params.put("accessToken", "");
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        getRequestQueue().add(request);
    }


    //上传完图片后开始发布
    private void publishAll() {
//        final String contentStr = et_sendmessage.getText().toString();
        final StringBuffer filePath = new StringBuffer();
        for (int i = 0; i < uploadPaths.size(); i++) {
            filePath.append(uploadPaths.get(i));
            if (i != uploadPaths.size() - 1) {
                filePath.append(",");
            }
        }
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SEND_RECORD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    RecordSingData data = getGson().fromJson(s, RecordSingData.class);
                                    showMsg(AddRecordActivity.this, getResources().getString(R.string.add_record_success));
                                    if (getResources().getString(R.string.type_qiugou).equals(mm_msg_type)) {
                                        //调用广播，刷新主页
                                        Intent intent1 = new Intent(Constants.SEND_INDEX_SUCCESS_QIUGOU);
                                        intent1.putExtra("addRecord", data.getData());
                                        sendBroadcast(intent1);
                                    }
                                    if (getResources().getString(R.string.type_gongying).equals(mm_msg_type)) {
                                        //调用广播，刷新主页
                                        Intent intent1 = new Intent(Constants.SEND_INDEX_SUCCESS_GONGYING);
                                        intent1.putExtra("addRecord", data.getData());
                                        sendBroadcast(intent1);
                                    }

                                    finish();
                                } else if (Integer.parseInt(code) == 3) {
                                    String str = getResources().getString(R.string.add_msg_one )+ (getGson().fromJson(getSp().getString("mm_emp_msg_num", ""), String.class)) + getResources().getString(R.string.tiao);
                                    showMsg(AddRecordActivity.this, str);
                                } else {
                                    Toast.makeText(AddRecordActivity.this, R.string.add_record_error_one, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(AddRecordActivity.this, R.string.add_record_error_one, Toast.LENGTH_SHORT).show();
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(AddRecordActivity.this, R.string.add_record_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_emp_id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                params.put("mm_emp_msg_num", getGson().fromJson(getSp().getString("mm_emp_msg_num", ""), String.class));
                params.put("mm_msg_title", "");
                params.put("mm_msg_content", mm_msg_content.getText().toString());
                if (getResources().getString(R.string.type_qiugou).equals(mm_msg_type)) {
                    params.put("mm_msg_type", "0");
                }
                if (getResources().getString(R.string.type_gongying).equals(mm_msg_type)) {
                    params.put("mm_msg_type", "1");
                }
                params.put("mm_msg_picurl", String.valueOf(filePath));
                params.put("provinceid", getGson().fromJson(getSp().getString("mm_emp_provinceId", ""), String.class));
                params.put("cityid", getGson().fromJson(getSp().getString("mm_emp_cityId", ""), String.class));
                params.put("countryid", getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class));
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("access_token", ""), String.class))) {
                    params.put("accessToken", getGson().fromJson(getSp().getString("access_token", ""), String.class));
                } else {
                    params.put("accessToken", "");
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        getRequestQueue().add(request);
    }


}
