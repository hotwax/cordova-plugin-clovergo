var exec = require('cordova/exec');

// CloverGo initialised
var CloverGo = function() {}

CloverGo.prototype.init = function (configuration, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'CloverGo', 'init', [configuration]);
};

CloverGo.prototype.connect = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'CloverGo', 'connect', []);
};


CloverGo.prototype.sale = function (saleInfo, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'CloverGo', 'sale', [saleInfo]);
};

if (typeof module != 'undefined' && module.exports) {
    module.exports = CloverGo;
}
