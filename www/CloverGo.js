var exec = require('cordova/exec');

// CloverGo initialised
var CloverGo = function() {}

CloverGo.prototype.init = function (configuration, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'CloverGo', 'init', [configuration]);
};

CloverGo.prototype.connect = function (configuration, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'CloverGo', 'connect', [configuration]);
};


CloverGo.prototype.sale = function (configuration, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'CloverGo', 'sale', [configuration]);
};

if (typeof module != 'undefined' && module.exports) {
    module.exports = CloverGo;
}
