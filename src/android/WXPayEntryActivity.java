package __PACKAGE_NAME__;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.json.JSONException;
import org.json.JSONObject;

import news.chen.yu.ionic.Wechatpay;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Wechatpay.wxAPI == null) {
            startMainActivity();
        } else {
            Wechatpay.wxAPI.handleIntent(getIntent(), this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        if (Wechatpay.wxAPI == null) {
            startMainActivity();
        } else {
            Wechatpay.wxAPI.handleIntent(intent, this);
        }

    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d(Wechatpay.TAG, resp.toString());

        if (Wechatpay.currentCallbackContext == null) {
            startMainActivity();
            return ;
        }

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case ConstantsAPI.COMMAND_SENDAUTH:
                        auth(resp);
                        break;

                    case ConstantsAPI.COMMAND_PAY_BY_WX:
                    default:
                        Wechatpay.currentCallbackContext.success();
                        break;
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Wechatpay.currentCallbackContext.error("用户取消操作");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Wechatpay.currentCallbackContext.error("授权失败");
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                Wechatpay.currentCallbackContext.error("发送失败");
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                Wechatpay.currentCallbackContext.error("不支持此类型操作");
                break;
            case BaseResp.ErrCode.ERR_COMM:
                Wechatpay.currentCallbackContext.error("通常错误：" + resp.errStr);
                break;
            default:
                Wechatpay.currentCallbackContext.error("未知错误：" + resp.errCode);
                break;
        }

        finish();
    }

    @Override
    public void onReq(BaseReq req) {
        finish();
    }

    protected void auth(BaseResp resp) {
        SendAuth.Resp res = ((SendAuth.Resp) resp);

        Log.i(Wechatpay.TAG, res.toString());

        if (Wechatpay.currentCallbackContext == null) {
            return ;
        }

        JSONObject response = new JSONObject();
        try {
            response.put("code", res.code);
            response.put("state", res.state);
            response.put("country", res.country);
            response.put("lang", res.lang);
        } catch (JSONException e) {
            Log.e(Wechatpay.TAG, e.getMessage());
        }

        Wechatpay.currentCallbackContext.success(response);
    }

    protected void startMainActivity() {
        Log.i(Wechatpay.TAG, "start main activity");
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(getApplicationContext().getPackageName());
        getApplicationContext().startActivity(intent);
    }
}