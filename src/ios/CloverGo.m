/********* CloverGo.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>

@interface CloverGo : CDVPlugin {
  // Member variables go here.
}

- (void)init:(CDVInvokedUrlCommand*)command;
- (void)sale:(CDVInvokedUrlCommand*)command;
- (void)connect:(CDVInvokedUrlCommand*)command;
- (void)disconnect:(CDVInvokedUrlCommand*)command;
@end

@implementation CloverGo

- (void)init:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    // TODO Dummy method to avoid code break
    /* NSDictionary* configObject = [command.arguments objectAtIndex:0];

    if (echo != nil && [echo length] > 0) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    } */
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)connect:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    // TODO Dummy method to avoid code break
    /* NSDictionary* configObject = [command.arguments objectAtIndex:0];

    if (echo != nil && [echo length] > 0) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    } */
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)disconnect:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    // TODO Dummy method to avoid code break
    /* NSDictionary* configObject = [command.arguments objectAtIndex:0];

    if (echo != nil && [echo length] > 0) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    } */
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)sale:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    // TODO Dummy method to avoid code break
    /* NSDictionary* configObject = [command.arguments objectAtIndex:0];

    if (echo != nil && [echo length] > 0) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    } */
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end
