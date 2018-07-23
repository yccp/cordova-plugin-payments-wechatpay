#import "Wechatpay.h"

@implementation Wechatpay
- (void)pluginInitialize
{
    self.appId = [[self.commandDelegate settings] objectForKey:@"payment_wechat_app_id"];
    [WXApi registerApp: self.appId];
}

- (void)pay:(CDVInvokedUrlCommand *)command
{
    // check arguments
    NSDictionary *params = [command.arguments objectAtIndex:0];
    if (!params)
    {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"参数格式错误"] callbackId:command.callbackId];
        return ;
    }
    
    // check required parameters
    NSArray *requiredParams;
    if ([params objectForKey:@"mch_id"])
    {
        requiredParams = @[@"mch_id", @"prepay_id", @"timestamp", @"nonce", @"sign", @"appid"];
    }
    else
    {
        requiredParams = @[@"partnerid", @"prepayid", @"timestamp", @"noncestr", @"sign", @"appid"];
    }
    
    for (NSString *key in requiredParams)
    {
        if (![params objectForKey:key])
        {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"参数格式错误"] callbackId:command.callbackId];
            return ;
        }
    }
    
    PayReq *req = [[PayReq alloc] init];
    
    NSString *appId = [params objectForKey:requiredParams[5]];
    if (appId && ![appId isEqualToString:self.appId]) {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"appid不匹配"] callbackId:command.callbackId];
        return;
    }
    
    req.partnerId = [params objectForKey:requiredParams[0]];
    req.prepayId = [params objectForKey:requiredParams[1]];
    req.timeStamp = [[params objectForKey:requiredParams[2]] intValue];
    req.nonceStr = [params objectForKey:requiredParams[3]];
    req.package = @"Sign=WXPay";
    req.sign = [params objectForKey:requiredParams[4]];
    
    if ([WXApi sendReq:req])
    {
        // save the callback id
        self.currentCallbackId = command.callbackId;
    }
    else
    {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"发送请求失败"] callbackId:command.callbackId];
    }
}

- (void)onReq:(BaseReq *)req
{
    NSLog(@"req:%@", req);
}

- (void)onResp:(BaseResp *)resp
{
    if(resp.errCode == WXSuccess) {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:self.currentCallbackId];
    } else {
        NSString* message;
        switch (resp.errCode)
        {
            case WXErrCodeCommon:
                message = @"普通错误";
                break;
                
            case WXErrCodeUserCancel:
                message = @"用户点击取消并返回";
                break;
                
            case WXErrCodeSentFail:
                message = @"发送失败";
                break;
                
            case WXErrCodeAuthDeny:
                message = @"授权失败";
                break;
                
            case WXErrCodeUnsupport:
                message = @"微信不支持";
                break;
                
            default:
                message = @"未知错误";
        }
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:message] callbackId:self.currentCallbackId];
    }
    self.currentCallbackId = nil;
}

- (void)handleOpenURL:(NSNotification *)notification
{
    NSURL* url = [notification object];
    
    if ([url isKindOfClass:[NSURL class]] && [url.scheme isEqualToString:self.appId] && [url.host isEqualToString:@"pay"])
    {
        [WXApi handleOpenURL:url delegate:self];
    }
}

@end
