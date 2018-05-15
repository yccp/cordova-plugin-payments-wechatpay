#import <Cordova/CDVPlugin.h>
#import "WXApi.h"
#import "WXApiObject.h"

@interface Wechatpay : CDVPlugin <WXApiDelegate>

@property (nonatomic, strong) NSString *currentCallbackId;
@property (nonatomic, strong) NSString *appId;

- (void)pay:(CDVInvokedUrlCommand *)command;

@end
