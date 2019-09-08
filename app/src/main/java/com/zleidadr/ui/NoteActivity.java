package com.zleidadr.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zleidadr.R;
import com.zleidadr.Zleida;
import com.zleidadr.common.Constant;
import com.zleidadr.entity.Dict;
import com.zleidadr.entity.ReceivableReq;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoteActivity extends Activity {
   @BindView(R.id.tv_back)
    FrameLayout mTvBack;
   @BindView(R.id.tv_title)
    TextView mTvTitle;
   @BindView(R.id.spinner_access_obj)
    Spinner mSpinnerAccessObj;
   @BindView(R.id.spinner_access_result)
    Spinner mSpinnerAccessResult;
   @BindView(R.id.spinner_address_valid)
    Spinner mSpinnerAddressValid;
   @BindView(R.id.et_note_des)
    EditText mEtNoteDes;

    private List<Dict> mAccessObj = Zleida.sDictMap.get(Constant.DICTNAME_ACCESS_OBJECT);
    private List<Dict> mResultSelf = Zleida.sDictMap.get(Constant.DICTNAME_ACCESS_RESULT_SELF);
    private List<Dict> mResultOther = Zleida.sDictMap.get(Constant.DICTNAME_ACCESS_RESULT_OTHER);
    private List<Dict> mAddressValidity = Zleida.sDictMap.get(Constant.DICTNAME_ADDRESS_VALIDITY);
    private List<String> mAccessObjStr;
    private List<String> mResultStr;
    private List<String> mAddressValidityStr;
    private ArrayAdapter<String> mAdapterObject;
    private BaseAdapter mAdapterResult;
    private ArrayAdapter<String> mAdapterAddress;
    private String mLocalId;
    private ReceivableReq mReceivableReq;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        ButterKnife.bind(this);
        mLocalId = getIntent().getStringExtra(Constant.BUNDLE_STR_LOCALID);
        if (!TextUtils.isEmpty(mLocalId)) {
            mReceivableReq = ReceivableReq.findById(ReceivableReq.class, Integer.valueOf(mLocalId));
        } else {
            Toast.makeText(NoteActivity.this, "此条催收记录不存在", Toast.LENGTH_SHORT).show();
        }
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        } else if (mDialog != null) {
            mDialog = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initView() {
        mTvBack.setVisibility(View.VISIBLE);
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText("填写信息");
        String noteDetail = mReceivableReq.getDetail();
        if (!TextUtils.isEmpty(noteDetail)) {
            mEtNoteDes.setText(noteDetail);
        }
        mAccessObjStr = dict2Str(mAccessObj);

        mAdapterObject = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mAccessObjStr);
        mAdapterObject.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerAccessObj.setAdapter(mAdapterObject);
        mSpinnerAccessObj.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mReceivableReq.setAccessObject(mAccessObj.get(position).getDictKey());
                if (mAccessObj.get(position).getDictKey().equals(Constant.DICTKEY_SELF)) {
                    mResultStr = dict2Str(mResultSelf);
                } else {
                    mResultStr = dict2Str(mResultOther);
                }
                mAdapterResult.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        String accessObject = mReceivableReq.getAccessObject();
        for (int i = 0; i < mAccessObj.size(); i++) {
            if (mAccessObj.get(i).getDictKey().equals(accessObject)) {
                mSpinnerAccessObj.setSelection(i);
            }
        }

        mResultStr = dict2Str(mResultSelf);
        mAdapterResult = new BaseAdapter() {
            @Override
            public int getCount() {
                return mResultStr.size();
            }

            @Override
            public Object getItem(int position) {
                return mResultStr.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(android.R.layout.simple_spinner_item, parent, false);
                }
                TextView text = (TextView) convertView.findViewById(android.R.id.text1);
                text.setText(mResultStr.get(position));
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                }
                TextView text = (TextView) convertView.findViewById(android.R.id.text1);
                text.setText(mResultStr.get(position));
                return convertView;
            }
        };
        mSpinnerAccessResult.setAdapter(mAdapterResult);
        mSpinnerAccessResult.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String accessObj = mReceivableReq.getAccessObject();
                mReceivableReq.setAccessResult(accessObj.equals(Constant.DICTKEY_SELF) ? mResultSelf.get(position).getDictKey() : mResultOther.get(position).getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        String accessResult = mReceivableReq.getAccessResult();
        if (!TextUtils.isEmpty(accessResult)) {

            if (!TextUtils.isEmpty(accessObject) && accessObject.equals(Constant.DICTKEY_SELF)) {
                for (int i = 0; i < mResultSelf.size(); i++) {
                    if (mResultSelf.get(i).getDictKey().equals(accessResult)) {
                        mSpinnerAccessResult.setSelection(i);
                    }
                }
            } else if (!TextUtils.isEmpty(accessObject) && !accessObject.equals(Constant.DICTKEY_SELF)) {
                for (int i = 0; i < mResultOther.size(); i++) {
                    if (mResultOther.get(i).getDictKey().equals(accessResult)) {
                        mResultStr = dict2Str(mResultOther);
                        mAdapterResult.notifyDataSetChanged();
                        mSpinnerAccessResult.setSelection(i);
                    }
                }
            }

        }

        mAddressValidityStr = dict2Str(mAddressValidity);
        mAdapterAddress = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mAddressValidityStr);
        mAdapterAddress.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerAddressValid.setAdapter(mAdapterAddress);
        mSpinnerAddressValid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mReceivableReq.setAddressValidity(mAddressValidity.get(position).getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mEtNoteDes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });
        String addressValid = mReceivableReq.getAddressValidity();
        for (int i = 0; i < mAddressValidity.size(); i++) {
            if (mAddressValidity.get(i).getDictKey().equals(addressValid)) {
                mSpinnerAddressValid.setSelection(i);
            }
        }
    }

    @OnClick(R.id.btn_save)
    public void onClick(Button button) {
        String noteDetail = mEtNoteDes.getText().toString();
        if (!TextUtils.isEmpty(noteDetail)) {
            mReceivableReq.setDetail(noteDetail);
        }
        mReceivableReq.save();
        finish();
    }

    private List<String> dict2Str(List<Dict> dictList) {
        List<String> list = new ArrayList<>();
        if (dictList != null && dictList.size() > 0) {
            for (Dict dict : dictList) {
                list.add(dict.getDictValue());
            }
        }
        return list;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
        mDialog = builder.setMessage("信息填写还未完成,请确认是否放弃?").setNegativeButton("放弃", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDialog.dismiss();
                finish();
            }
        }).setPositiveButton("继续", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDialog.dismiss();
            }
        }).create();
        mDialog.show();
    }
}