// Author: Yu Chen <yu.chen@live.ie>
// License: Apache License 2.0

'use strict';

module.exports = {
  pay: function (params, onSuccess, onError) {
    cordova.exec(onSuccess, onError, "Wechatpay", "pay", [params]);
  }
};