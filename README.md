# Cordova/Ionic CloverGo plugin
Cordova/Ionic plugin for using Clover Remote Pay Go SDKs for Android & iOS. It communicates with Clover Go  device via bluetooth.  

Based upon [ Android ](https://github.com/clover/remote-pay-android-go) and [ iOS ](https://github.com/clover/remote-pay-android-go) POS integration. Please refer these integration links to know how to generate the configuration credentials.

## Platform Support
* android

## Package links
* [ NPM ](https://www.npmjs.com/package/cordova-plugin-clovergo)


# Usage

## How to add plugin?
Type following command from CLI to add this plugin

```
    cordova plugin add cordova-plugin-clovergo
```


```
    ionic cordova plugin add cordova-plugin-clovergo
```

The plugin creates the global object `clovergo`.

## How to use?

### Ionic 3

```
    import { Component } from '@angular/core';
    ...
    declare var clovergo: any;
    ...

    clovergo.init(cloverGoConfig, successCallback, errorCallback);

```



## Methods


- [clovergo.init](#init)
- [clovergo.connect](#connect)
- [clovergo.sale](#sale)
- [clovergo.sign](#sign)
- [clovergo.voidPayment](#voidPayment)
- [clovergo.disconnect](#disconnect)


## init

### Description

This method initialises the Clover Go SDK. It should call once while platform get ready and only once in per application lifecycle.

### Usage

```
init(
    configuration: CloverGoConfig, 
    callbackSuccess: (res: any) => void, 
    callbackError: (err: any) => void
    ): void;
```

## connect

### Description

This method connects to the available clover device.

### Usage

```
connect(
    callbackSuccess: (res: any) => void,
    callbackError: (err: any) => void
    ): void;
```

## sale

### Description

This method is reponsible to initiate a transaction via Clover Go device.

### Usage

```
sale(
    saleInfo: SaleInfo,
    callbackSuccess: (res: any) => void,
    callbackError: (err: any) => void
    ): void;
```
## sign

### Description

This method is used to pass signature as two dimensional number array that represents points of signature on screen. The list is passed as signature in SignInfo object.

### Usage

```
sign(
    SignInfo: SignInfo,
    callbackSuccess: (res: any) => void,
    callbackError: (err: any) => void
    ): void;
```

## voidPayment

### Description

This method is used to void specific payment. Payment info should be passed paymentId and orderId generated from the Clover API

### Usage

```
voidPayment(
    paymentInfo: PaymentInfo,
    callbackSuccess: (res: any) => void,
    callbackError: (err: any) => void
    ): void;
```

## disconnect

### Description

This method disconnects to the connected clover device.

### Usage

```
disconnect(
    callbackSuccess: (res: any) => void,
    callbackError: (err: any) => void
    ): void;
```

## Contributing
Pull requests are most welcomed.  
If you discover any bug or have a suggestion, please feel free to create an issue.

## The license

cordova-plugin-clovergo is available under the [Apache License 2.0](https://github.com/hotwax/cordova-plugin-clovergo/blob/master/LICENSE).
