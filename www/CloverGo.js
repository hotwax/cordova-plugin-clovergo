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
    disconnect: function (successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'CloverGo', 'disconnect', []);
    }
}
module.exports = clovergo;
