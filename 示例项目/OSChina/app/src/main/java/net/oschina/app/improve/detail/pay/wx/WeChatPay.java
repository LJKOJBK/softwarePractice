package net.oschina.app.improve.detail.pay.wx;

import android.app.Activity;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.Serializable;

/**
 * 微信支付
 * Created by huanghaibin on 2017/6/14.
 */
@SuppressWarnings("all")
public final class WeChatPay {
    private IWXAPI msgApi;

    public WeChatPay(Activity mActivity) {
        //wxd23e1a82c7c450d1
        msgApi = WXAPIFactory.createWXAPI(mActivity, "wxa8213dc827399101", true);
        msgApi.registerApp("wxa8213dc827399101");
    }

    public boolean isWxAppInstalled() {
        return msgApi.isWXAppInstalled();
    }

    public void pay(PayResult result) {
        PayReq request = new PayReq();
        request.appId = "wxa8213dc827399101";
        request.partnerId = result.getPartnerid();
        request.prepayId = result.getPrepayid();
        request.packageValue = "Sign=WXPay";
        request.nonceStr = result.getNoncestr();
        request.timeStamp = result.getTimestamp();
        request.sign = result.getSign();
        msgApi.sendReq(request);
    }

    /**
     * 支付信息预拉取
     */
    public static class PayResult implements Serializable {
        private String sign;
        private String partnerid;
        private String prepayid;
        private String noncestr;
        private String timestamp;

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
