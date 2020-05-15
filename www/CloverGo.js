var exec = require('cordova/exec');

var clovergo = {
    init: function (configuration, successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'CloverGo', 'init', [configuration]);
    },
    connect: function (successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'CloverGo', 'connect', []);
    },
    sale: function (saleInfo, successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'CloverGo', 'sale', [saleInfo]);
    },
    sign: function (signInfo, successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'CloverGo', 'sign', [signInfo]);
    },
    voidPayment: function (paymentInfo, successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'CloverGo', 'voidPayment', [paymentInfo]);
    },
    disconnect: function (successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'CloverGo', 'disconnect', []);
    }
}
module.exports = clovergo;
