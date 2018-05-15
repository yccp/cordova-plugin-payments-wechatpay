package news.chen.yu.ionic;

import android.util.Log;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class Wechatpay extends CordovaPlugin {
    public static String TAG = "cordova-plugin-payments-wechatpay";
    public static String appKey;
    public static IWXAPI wxAPI;
    public static CallbackContext currentCallbackContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Wechatpay.appKey = preferences.getString("app_id", "");
        Log.d(TAG, "Init: " + Wechatpay.appKey);
        Wechatpay.wxAPI = WXAPIFactory.createWXAPI(cordova.getActivity(), Wechatpay.appKey, true);
        Wechatpay.wxAPI.registerApp(Wechatpay.appKey);
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.i(TAG, "Execute:" + action + " with :" + args.toString());
        if (action.equals("pay")) {
            JSONObject params = args.getJSONObject(0);
            this.sendPaymentRequest(params, callbackContext);
            return true;
        }
        return false;
    }

    protected boolean sendPaymentRequest(JSONObject params, CallbackContext callbackContext) {
        PayReq req = new PayReq();

        try {
            req.appId = Wechatpay.appKey;
            req.partnerId = params.has("mch_id") ? params.getString("mch_id") : params.getString("partnerid");
            req.prepayId = params.has("prepay_id") ? params.getString("prepay_id") : params.getString("prepayid");
            req.nonceStr = params.has("nonce") ? params.getString("nonce") : params.getString("noncestr");
            req.timeStamp = params.getString("timestamp");
            req.sign = params.getString("sign");
            req.packageValue = "Sign=WXPay";
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

            callbackContext.error("参数错误");
            return true;
        }


        if (Wechatpay.wxAPI.sendReq(req)) {
            Log.i(TAG, "Payment request has been sent successfully.");

            // send no result
            sendNoResultPluginResult(callbackContext);
        } else {
            Log.i(TAG, "Payment request has been sent unsuccessfully.");

            // send error
            callbackContext.error("发送请求失败");
        }

        return true;
    }

    private void sendNoResultPluginResult(CallbackContext callbackContext) {
        currentCallbackContext = callbackContext;

        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }
}