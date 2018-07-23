# 微信支付 cordova 插件

> 支持ios, android
开通服务: [https://open.weixin.qq.com](https://open.weixin.qq.com)

## 安装

```
cordova plugin add cordova-plugin-payments-wechatpay --variable PAYMENT_WECHAT_APP_ID=你的ID --save
```
或
```
ionic cordova plugin add cordova-plugin-payments-wechatpay --variable PAYMENT_WECHAT_APP_ID=你的ID
```

> 相关依赖
[cordova-plugin-cocoapod-support](https://www.npmjs.com/package/cordova-plugin-cocoapod-support)
```
cordova plugin add cordova-plugin-cocoapod-support --save
```
或
```
ionic cordova plugin add cordova-plugin-cocoapod-support
```

## 使用方法
>打开支付页面
```js
window.Wechatpay.pay({
  // ... 后端生成的Object
}, () => {
  console.log('成功');
}, e => {
  console.error(e);
});

```

## IONIC Wrap
[https://github.com/yc-ionic/wechatpay](https://github.com/yc-ionic/wechatpay)

## 代码捐献

非常期待您的代码捐献。