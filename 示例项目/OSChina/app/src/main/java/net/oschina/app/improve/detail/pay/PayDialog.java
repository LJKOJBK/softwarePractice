package net.oschina.app.improve.detail.pay;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.oschina.app.R;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.util.SimpleTextWatcher;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 支付对话框
 * Created by huanghaibin on 2017/6/16.
 */

public class PayDialog extends Dialog implements View.OnClickListener {


    private SubBean mBean;
    private EditText mEditInput;
    private Button mBtnPay;
    private OnPayListener mListener;
    private float mMoney;
    private int mPayType = 1;

    public PayDialog(@NonNull Context context, SubBean bean) {
        super(context);
        this.mBean = bean;
    }

    public void setOnPayListener(OnPayListener listener) {
        this.mListener = listener;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pay);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        getWindow().setAttributes(params);


        CircleImageView mPortrait = (CircleImageView) findViewById(R.id.iv_portrait);
        mEditInput = (EditText) findViewById(R.id.et_input);
        mBtnPay = (Button) findViewById(R.id.btn_pay);
        mBtnPay.setOnClickListener(this);
        final TextView mTextPayInfo = (TextView) findViewById(R.id.tv_pay_choice);
        findViewById(R.id.tv_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.getSelectDialog(getContext(),
                        "选择支付方式",
                        new String[]{"支付宝", "微信"},
                        "取消", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        mPayType = 1;
                                        mTextPayInfo.setText("使用支付宝支付,");
                                        break;
                                    case 1:
                                        mPayType = 2;
                                        mTextPayInfo.setText("使用微信支付,");
                                        break;
                                }
                            }
                        }).show();
            }
        });

        Glide.with(getContext())
                .load(mBean.getAuthor().getPortrait())
                .asBitmap()
                .placeholder(R.mipmap.widget_default_face)
                .error(R.mipmap.widget_default_face)
                .into(mPortrait);
        ((TextView) findViewById(R.id.tv_nick_name)).setText(mBean.getAuthor().getName());

        mBtnPay.setEnabled(false);
        mEditInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if (TextUtils.isEmpty(s)) {
                    mBtnPay.setEnabled(false);
                    return;
                }
                String mCastStr = s.toString();
                float cast;
                try {
                    cast = Float.valueOf(mCastStr);
                } catch (Exception e) {
                    cast = 0;
                    mEditInput.setText(null);
                }
                if (cast <= 0) {
                    mBtnPay.setEnabled(false);
                } else {
                    mMoney = cast;
                    mBtnPay.setEnabled(true);
                }
            }
        });

        ((RadioGroup) findViewById(R.id.rg_pay)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_five:
                        mEditInput.setEnabled(false);
                        mEditInput.setVisibility(View.GONE);
                        mMoney = 5.00f;
                        mEditInput.setText(String.valueOf(5));
                        break;
                    case R.id.rb_ten:
                        mEditInput.setEnabled(false);
                        mEditInput.setVisibility(View.GONE);
                        mMoney = 10.00f;
                        mEditInput.setText(String.valueOf(10));
                        break;
                    case R.id.rb_other:
                        mEditInput.setEnabled(true);
                        mEditInput.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (mMoney == 0 || mListener == null) return;
        if(mMoney < 1){
            SimplexToast.show(getContext(),"打赏金额最低1元");
            return;
        }
        if(mMoney> 10000){
            SimplexToast.show(getContext(),"打赏金额不能超过 10000 元");
            return;
        }
        mListener.onPay(mPayType == 1 ? mMoney * 100: mMoney * 100, mPayType);
        dismiss();
    }

    public interface OnPayListener {
        void onPay(float money, int type);
    }
}
