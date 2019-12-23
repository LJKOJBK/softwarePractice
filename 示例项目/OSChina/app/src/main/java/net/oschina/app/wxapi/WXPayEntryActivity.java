package net.oschina.app.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.oschina.app.R;
import net.oschina.app.improve.widget.SimplexToast;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);
    	api = WXAPIFactory.createWXAPI(this, "wxa8213dc827399101");
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp.errCode == 0){
			//显示充值成功的页面和需要的操作
			SimplexToast.show(this,"打赏成功");
		}
		if (resp.errCode == -1){
			//错误
			SimplexToast.show(this,"打赏失败");
		}

		if (resp.errCode == -2){
			SimplexToast.show(this,"取消支付");
			//用户取消
		}
		finish();
	}
}