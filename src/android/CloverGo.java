package co.hotwax.clovergo;

import android.widget.Toast;
import android.util.Log;
import android.text.TextUtils;
import android.Manifest;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import android.content.Context;
import android.content.Intent;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.clover.remote.client.clovergo.CloverGoConstants.TransactionType;
import com.clover.remote.client.clovergo.CloverGoConnector;
import com.clover.remote.client.ConnectorFactory;
import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.clovergo.CloverGoDeviceConfiguration;
import static com.clover.remote.client.clovergo.CloverGoDeviceConfiguration.ENV.LIVE;
import static com.clover.remote.client.clovergo.CloverGoDeviceConfiguration.ENV.SANDBOX;

import com.clover.remote.client.messages.*;
import com.clover.remote.client.Constants;

import com.clover.remote.client.MerchantInfo;
import com.clover.remote.client.clovergo.CloverGoConstants;
import com.clover.remote.client.clovergo.ICloverGoConnector;
import com.clover.remote.client.clovergo.ICloverGoConnectorListener;
import com.clover.remote.client.messages.AuthResponse;
import com.clover.remote.client.messages.BaseRequest;
import com.clover.remote.client.messages.CapturePreAuthResponse;
import com.clover.remote.client.messages.CardApplicationIdentifier;
import com.clover.remote.client.messages.CloseoutResponse;
import com.clover.remote.client.messages.CloverDeviceErrorEvent;
import com.clover.remote.client.messages.CloverDeviceEvent;
import com.clover.remote.client.messages.ConfirmPaymentRequest;
import com.clover.remote.client.messages.CustomActivityResponse;
import com.clover.remote.client.messages.ManualRefundResponse;
import com.clover.remote.client.messages.MessageFromActivity;
import com.clover.remote.client.messages.PreAuthResponse;
import com.clover.remote.client.messages.PrintJobStatusResponse;
import com.clover.remote.client.messages.PrintManualRefundDeclineReceiptMessage;
import com.clover.remote.client.messages.PrintManualRefundReceiptMessage;
import com.clover.remote.client.messages.PrintPaymentDeclineReceiptMessage;
import com.clover.remote.client.messages.PrintPaymentMerchantCopyReceiptMessage;
import com.clover.remote.client.messages.PrintPaymentReceiptMessage;
import com.clover.remote.client.messages.PrintRefundPaymentReceiptMessage;
import com.clover.remote.client.messages.ReadCardDataResponse;
import com.clover.remote.client.messages.RefundPaymentResponse;
import com.clover.remote.client.messages.ResetDeviceResponse;
import com.clover.remote.client.messages.RetrieveDeviceStatusResponse;
import com.clover.remote.client.messages.RetrievePaymentResponse;
import com.clover.remote.client.messages.RetrievePendingPaymentsResponse;
import com.clover.remote.client.messages.RetrievePrintersResponse;
import com.clover.remote.client.messages.SaleResponse;
import com.clover.remote.client.messages.TipAdjustAuthResponse;
import com.clover.remote.client.messages.TransactionRequest;
import com.clover.remote.client.messages.VaultCardResponse;
import com.clover.remote.client.messages.VerifySignatureRequest;
import com.clover.remote.client.messages.VoidPaymentResponse;
import com.clover.remote.message.TipAddedMessage;
import com.clover.sdk.v3.order.VoidReason;

import com.firstdata.clovergo.domain.model.Order;
import com.firstdata.clovergo.domain.model.Payment;
import com.firstdata.clovergo.domain.model.ReaderInfo;

import static com.firstdata.clovergo.domain.model.ReaderInfo.ReaderType.RP450;

/**
 * The class perform payment operations with Clover Go device
 */
public class CloverGo extends CordovaPlugin {

    private String [] permissions = {
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.GET_ACCOUNTS,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private CloverGoDeviceConfiguration.ENV goEnv;
    private ICloverConnector cloverConnector;
    private ICloverGoConnectorListener ccGoListener;
    private HashMap<ReaderInfo.ReaderType, MerchantInfo> merchantInfoMap;
    private ArrayList<ReaderInfo> mArrayListReadersList;
    private String baseUrl;
    private String goApiKey, goSecret, accessToken;
    private String oAuthClientId, oAuthClientSecret, oAuthUrl, oAuthTokenUrl;
    private String appId, appVersion;
    private static final String TAG = CloverGo.class.getSimpleName();
    private Context context;
    private CallbackContext rootCallbackContext;
    private ICloverGoConnector cloverGo450Connector;
    private Map<String, ReaderInfo> cloverDeviceMap;
    private String mPreferred450Reader;
    private ReaderInfo cloverDevice;
    private ICloverGoConnectorListener.PaymentTypeSelection paymentTypeSelection;
    private ICloverGoConnectorListener.SignatureCapture signatureCaptureListener;
    private Payment curentPayment;


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
        cloverDeviceMap = new HashMap<String, ReaderInfo>();

        mArrayListReadersList = new ArrayList<>();

        context = this.cordova.getActivity().getApplicationContext();
 
    }

    public void showProgressDialog(String title, String message, boolean isCancelable) {
        // TODO
    }

    public void showAlertDialog(String title, String message) {
        // TODO
    }
    public void showToast(String message) {
        Toast.makeText(webView.getContext(), message, Toast.LENGTH_LONG).show();
    }

    private void dismissDialog() {
        // TODO
    }
    public void hideKeyboard() {
        // TODO
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        rootCallbackContext = callbackContext;
        if (action.equals("init")) {
            // Initialise the Clover SDK
            this.init(args.getJSONObject(0), callbackContext);
            return true;
        } if (action.equals("connect")) {
            this.connect(args, callbackContext);
            return true;
        } if (action.equals("sale")) {
            this.sale(args.getJSONObject(0), callbackContext);
            return true;
        }  if (action.equals("sign")) {
            this.sign(args.getJSONObject(0), callbackContext);
            return true;
        }  if (action.equals("voidPayment")) {
            this.voidPayment(args.getJSONObject(0), callbackContext);
            return true;
        } if (action.equals("disconnect")) {
            this.disconnect(args, callbackContext);
            return true;
        }
        return false;
    }
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent intent)
    {
        // TODO
    }


    /**
    * The method initialised the Clover SDK
    */
    private void init(JSONObject configObject, CallbackContext callbackContext) {

        if(!hasPermisssion()) {
            PermissionHelper.requestPermissions(this, 0, permissions);
        }

        try {
            String environment = configObject.getString("environment");

            /** Environment configuration */
            if (environment.equals(LIVE.toString())) {
                goEnv = LIVE;
            } else {
                goEnv = SANDBOX;;
            }

            appId = configObject.getString("appId");
            appVersion = configObject.getString("appVersion");
            goApiKey = configObject.getString("goApiKey");
            goSecret = configObject.getString("goSecret");
            accessToken = configObject.getString("accessToken");
            oAuthClientId = configObject.getString("oAuthClientId");
            oAuthClientSecret = configObject.getString("oAuthClientSecret");
            baseUrl = configObject.getString("baseUrl");
        } catch (JSONException e) {
            sendExceptionCallback(e.toString(), true);
        }

        oAuthUrl = "https://" + baseUrl + "/oauth/authorize?client_id=" + oAuthClientId + "&response_type=code";
        oAuthTokenUrl = "https://" + baseUrl + "/oauth/authorize?client_id=" + oAuthClientId + "&response_type=token";


        ccGoListener = new GoConnectorListener() {

            @Override
            public void onDeviceActivityStart(CloverDeviceEvent deviceEvent) {

            }

            @Override
            public void onDeviceActivityEnd(CloverDeviceEvent deviceEvent) {

            }

            @Override
            public void onDisplayMessage(String message) {
                showToast(message);
            }

            @Override
            public void onSendReceipt(Order order, SendReceipt sendReceipt) {
                // TODO Add a separate method to send receipt
                // Currently, it does not support sending the receipt.
                sendReceipt.noReceipt();
            }

            @Override
            public void onVoidPayment(com.firstdata.clovergo.domain.model.Payment payment, String reason) {}

            @Override
            public void onSignatureRequired(com.firstdata.clovergo.domain.model.Payment payment, SignatureCapture signatureCapture) {
                signatureCaptureListener = signatureCapture;
                curentPayment = payment;
                try {
                    JSONObject resObj = new JSONObject();
                    resObj.put("type", "SIGNATURE_REQUIRED");
                    resObj.put("message", "Signature required");
                    resObj.put("paymentId", payment.getPaymentId());
                    resObj.put("transactionType", payment.getCard().getTransactionType());
                    resObj.put("entryType", payment.getCard().getEntryType());
                    resObj.put("cardFirst6", payment.getCard().getFirst6());
                    resObj.put("cardLast4", payment.getCard().getLast4());
                    sendCallback(PluginResult.Status.OK, resObj, true);
            } catch (JSONException e) {
                sendExceptionCallback(e.toString(), true);
            }
            }

            @Override
            public void onDeviceDisconnected(ReaderInfo readerInfo) {
                // TODO
            }

            @Override
            public void onDeviceConnected() {
                // TODO
            }

            @Override
            public void onCloverGoDeviceActivity(final CloverDeviceEvent deviceEvent) {
                String messageType = "TOAST";
                // TODO
                switch (deviceEvent.getEventState()) {
                    case CARD_SWIPED: // Card Swiped
                    case CARD_TAPPED: // Contactless Payment Started
                    case EMV_COMPLETE: // EMV Transaction Completed
                    case CARD_INSERTED: // Card Inserted
                    case UPDATE_STARTED: // Reader Update
                        messageType =  "PROGRESS";
                        break;
                    case UPDATE_COMPLETED: // Reader Update", "Please disconnect and reconnect your reader.
                    case READ_CARD_DATA_COMPLETED: // CARD DATA
                        messageType =  "ALERT";
                        break;
                    case CANCEL_CARD_READ:
                    case CARD_REMOVED:
                    case PLEASE_SEE_PHONE_MSG:
                    case READER_READY:
                        messageType =  "TOAST";
                        break;
                }
                // TODO Use observable and uncomment this code
                /* try {
                    JSONObject resObj = new JSONObject();
                    resObj.put("type", deviceEvent.getEventState());
                    resObj.put("message", deviceEvent.getMessage() != null ? deviceEvent.getMessage() : "");
                    resObj.put("messageType", messageType);
                    sendCallback(PluginResult.Status.OK, resObj, true);
                } catch (JSONException e) {
                    sendExceptionCallback(e.toString(), true);
                } */
            }

            @Override
            public void onGetMerchantInfo() {
                showProgressDialog("Getting Merchant Info", "Please wait", false);
            }

            @Override
            public void onGetMerchantInfoResponse(MerchantInfo merchantInfo) {
                try {
                    if (merchantInfo != null) {
                        JSONObject resObj = new JSONObject();
                        resObj.put("type", "CLOVER_SDK_INITIALISED");
                        resObj.put("message", "Clover SDK initialised");
                        resObj.put("merchantName", merchantInfo.getMerchantName());
                        sendCallback(PluginResult.Status.OK, resObj, true);
                    } else {
                        JSONObject resObj = new JSONObject();
                        resObj.put("type", "CLOVER_SDK_INITIALIZE_FAILED");
                        resObj.put("message", "Could not initialize the SDK. Please try again later.");
                        sendCallback(PluginResult.Status.ERROR, resObj, true);
                    }
                } catch (JSONException e) {
                    sendExceptionCallback(e.toString(), true);
                }
                
            }

            @Override
            public void onDeviceDiscovered(ReaderInfo readerInfo) {

                // Currently we considered single device stored in cloverDevice variable
                // TODO allow user to choose from the multiple devices
                // Assign only when it is Clover RP450 Bluetooth device 
                if (readerInfo != null && readerInfo.getReaderType() == RP450) {
                   cloverDevice = readerInfo;
                }

            }

            @Override
            public void onDeviceReady(final MerchantInfo merchantInfo) {
                try {
                    JSONObject resObj = new JSONObject();
                    resObj.put("type", "CLOVER_DEVICE_READY");
                    resObj.put("message", "Clover Device Ready");
                    sendCallback(PluginResult.Status.OK, resObj, true);
                } catch (JSONException e) {
                    sendExceptionCallback(e.toString(), true);
                }
            }
            @Override
            public void onAidMatch(final List<CardApplicationIdentifier> applicationIdentifiers, final AidSelection aidSelection) {}

            @Override
            public void onPaymentTypeRequired(final int cardEntryMethods, List<ReaderInfo> connectedReaders, final PaymentTypeSelection paymentTypeSelection) {
                // This is necessary as if selectPaymentType is set while doing sale, device is not accesed
                CloverGo.this.paymentTypeSelection = paymentTypeSelection;
            }

            @Override
            public void onManualCardEntryRequired(TransactionType transactionType, BaseRequest baseRequest,
                                                  ICloverGoConnector.GoPaymentType goPaymentType, ReaderInfo.ReaderType readerType,
                                                  boolean allowDuplicate, ManualCardEntry manualCardEntry) {}


            void showGoPaymentTypes(List<ReaderInfo> connectedReaders, int cardEntryMethods) {}


            @Override
            public void notifyOnProgressDialog(String title, String message, boolean isCancelable) {
                showProgressDialog(title, message, isCancelable);
            }

            @Override
            public void onDeviceError(CloverDeviceErrorEvent deviceErrorEvent) {
                // TODO Handle this
                switch (deviceErrorEvent.getErrorType()) {
                    case READER_ERROR:
                    case CARD_ERROR:
                    case READER_TIMEOUT:
                    case COMMUNICATION_ERROR:
                    case LOW_BATTERY:
                    case PARTIAL_AUTH_REJECTED:
                    case DUPLICATE_TRANSACTION_REJECTED:
                        Log.d(TAG, "**CloverGo****************************************" + deviceErrorEvent.getMessage());
                        break;
                    case MULTIPLE_CONTACT_LESS_CARD_DETECTED_ERROR:
                    case CONTACT_LESS_FAILED_TRY_CONTACT_ERROR:
                    case EMV_CARD_SWIPED_ERROR:
                    case DIP_FAILED_ALL_ATTEMPTS_ERROR:
                    case DIP_FAILED_ERROR:
                    case SWIPE_FAILED_ERROR:
                        Log.d(TAG, "**CloverGo****************************************" + deviceErrorEvent.getMessage());
                        break;
                    default:
                        Log.d(TAG, "**CloverGo****************************************" + deviceErrorEvent.getMessage());
                        break;
                }
                try {
                    JSONObject resObj = new JSONObject();
                    resObj.put("type", deviceErrorEvent.getErrorType().name());
                    resObj.put("message", deviceErrorEvent.getMessage());
                    sendCallback(PluginResult.Status.ERROR, resObj, true);
                } catch (JSONException e) {
                    sendExceptionCallback(e.toString(), true);
                }
            }

            @Override
            public void onSaleResponse(final SaleResponse response) {
                // TODO
                try {
                    if (response.isSuccess()) {
                        com.clover.sdk.v3.payments.Payment payment = response.getPayment();
                        JSONObject resObj = new JSONObject();
                        resObj.put("type", "PAYMENT_SUCCESSFUL");
                        resObj.put("message", response.getMessage() != null ? response.getMessage() : "Sale successfully processed");
                        resObj.put("paymentId", payment.getId());
                        resObj.put("externalPaymentId", payment.getExternalPaymentId());
                        resObj.put("cardholderName", payment.getCardTransaction().getCardholderName());
                        resObj.put("orderId", payment.getOrder().getId());
                        resObj.put("transactionType", payment.getCardTransaction().getType());
                        resObj.put("cardType", payment.getCardTransaction().getCardType());
                        resObj.put("authCode", payment.getCardTransaction().getAuthCode());
                        resObj.put("entryType", payment.getCardTransaction().getEntryType());
                        resObj.put("cardFirst6", payment.getCardTransaction().getFirst6());
                        resObj.put("cardLast4", payment.getCardTransaction().getLast4());
                        sendCallback(PluginResult.Status.OK, resObj, true);

                    } else {
                        JSONObject resObj = new JSONObject();
                        resObj.put("type", "PAYMENT_FAILED");
                        resObj.put("message", response.getMessage());
                        resObj.put("reason", response.getReason());
                        sendCallback(PluginResult.Status.ERROR, resObj, true);
                    }
                } catch (JSONException e) {
                    sendExceptionCallback(e.toString(), true);
                }
                
            }

            @Override
            public void onAuthResponse(final AuthResponse response) {}

            @Override
            public void onPreAuthResponse(final PreAuthResponse response) {}

            @Override
            public void onTipAdjustAuthResponse(TipAdjustAuthResponse response) {}

            @Override
            public void onCapturePreAuthResponse(CapturePreAuthResponse response) {}

            @Override
            public void onConfirmPaymentRequest(ConfirmPaymentRequest request) {
                // TODO Handle confirm payment case like when duplicate payment
                // specific methods
                // Also, handle multiple challenges
                cloverGo450Connector.acceptPayment(request.getPayment());
            }

            @Override
            public void onCloseoutResponse(CloseoutResponse response) {}

            @Override
            public void onRefundPaymentResponse(final RefundPaymentResponse response) {}

            @Override
            public void onTipAdded(TipAddedMessage message) {}

            @Override
            public void onVoidPaymentResponse(VoidPaymentResponse response) {
                // TODO
                try {
                    if (response.isSuccess()) {
                        com.clover.sdk.v3.payments.Payment payment = response.getPayment();
                        JSONObject resObj = new JSONObject();
                        resObj.put("type", "PAYMENT_VOID_SUCCESSFUL");
                        resObj.put("message", response.getMessage() != null ? response.getMessage() : "Payment successfully voided");
                        if (payment != null) resObj.put("paymentId", payment.getId());
                        sendCallback(PluginResult.Status.OK, resObj, true);
                    } else {
                        JSONObject resObj = new JSONObject();
                        resObj.put("type", "PAYMENT_VOID_FAILED");
                        resObj.put("message", response.getMessage());
                        resObj.put("reason", response.getReason() != null ? response.getReason() : "");
                        sendCallback(PluginResult.Status.ERROR, resObj, true);
                    }
                } catch (JSONException e) {
                    sendExceptionCallback(e.toString(), true);
                }
            }

            @Override
            public void onManualRefundResponse(final ManualRefundResponse response) {}

            @Override
            public void onReadCardDataResponse(final ReadCardDataResponse response) {}

            @Override
            public void onVaultCardResponse(final VaultCardResponse response) {}

            @Override
            public void onRetrievePaymentResponse(RetrievePaymentResponse response) {}
        };

        // TODO Make credentials configurable
        if (ccGoListener != null) {
            CloverGoDeviceConfiguration config = new CloverGoDeviceConfiguration.Builder(context, accessToken, goEnv, goApiKey, goSecret, appId, appVersion).deviceType(ReaderInfo.ReaderType.RP450).allowAutoConnect(true).build();
            cloverGo450Connector = (CloverGoConnector) ConnectorFactory.createCloverConnector(config);
            cloverGo450Connector.addCloverGoConnectorListener(ccGoListener);
            cloverGo450Connector.initializeConnection();
        }        
    }

    /**
    * The method initiates the sale
    */
    private void sale(JSONObject configObject, CallbackContext callbackContext) {
        if (configObject.isNull("orderId") || configObject.isNull("amount")) {
            callbackContext.error("orderId and amount is a required field for sale.");
        } else {
            try {    
                String orderId = configObject.getString("orderId");
                // TODO Handle if the parsing fails
                long amount = Long.parseLong(configObject.getString("amount"));
            
                // TODO Add all related fields to Sale Request
                if (cloverGo450Connector != null) {
                    // TODO Make it configurable
                    SaleRequest request = new SaleRequest(amount, orderId);
                    // This is required to identify the device, else when making the a sale gives reader not found error
                    request.setCardEntryMethods((Constants.CARD_ENTRY_METHOD_MANUAL | Constants.CARD_ENTRY_METHOD_ICC_CONTACT | Constants.CARD_ENTRY_METHOD_NFC_CONTACTLESS));
                    cordova.setActivityResultCallback(this);
                    cloverGo450Connector.sale(request);
                    // This is required to access device using bluetooth and accessing it's functionality
                    if (paymentTypeSelection != null) {
                        // Value of paymentTypeSelection is initialised only after initiating the sale 
                        paymentTypeSelection.selectPaymentType(ICloverGoConnector.GoPaymentType.RP450, RP450);
                    }
                    
                } else {
                    callbackContext.error("SDK is not initialised");
                }
            } catch (JSONException e) {
                sendExceptionCallback(e.toString(), true);
            }

        }
        
    }
    /**
    * The method connects to the available Clover Device
    */
    private void connect(JSONArray args, CallbackContext callbackContext) {
        if (cloverGo450Connector != null && cloverDevice != null) {
            // TODO Handle case for multiple devices connected
            cloverGo450Connector.connectToBluetoothDevice(cloverDevice);
        }
    }
    /**
    * The method passes the signature if required
    */
    private void sign(JSONObject signObject, CallbackContext callbackContext) {
        try {
            if (signObject.isNull("signature")) {
                callbackContext.error("signature is the required field.");
            } else {
                JSONArray jsonSignature = signObject.optJSONArray("signature");
                int[][] signature = new int[jsonSignature.length()][];
                for (int i = 0; i < jsonSignature.length(); ++i) {
                    signature[i] = convertJSONArrayToJavaArray(jsonSignature.getJSONArray(i));
                }
                // TODO For paymentId, it can also be passed as parameter
                signatureCaptureListener.captureSignature(curentPayment.getPaymentId(), signature);
            }
        } catch (JSONException e) {
            sendExceptionCallback(e.toString(), true);
        }

    }
    /**
    * The method voids the payment
    */
    private void voidPayment(JSONObject paymentObject, CallbackContext callbackContext) {
        try {
            if (paymentObject.isNull("paymentId") || paymentObject.isNull("orderId")) {
                callbackContext.error("paymentId and orderId are the required field.");
            } else {
                VoidPaymentRequest request = new VoidPaymentRequest();
                request.setPaymentId(paymentObject.getString("paymentId"));
                request.setOrderId(paymentObject.getString("orderId"));
                request.setVoidReason(VoidReason.USER_CANCEL.name());
                request.setDisablePrinting(false);
                request.setDisableReceiptSelection(false);
                if (cloverGo450Connector != null) {
                    cloverGo450Connector.voidPayment(request);
                }
            }
        } catch (JSONException e) {
            sendExceptionCallback(e.toString(), true);
        }

    }
    /**
    * The method disconnects to the available Clover Device
    */
    private void disconnect(JSONArray args, CallbackContext callbackContext) {
        if (cloverGo450Connector != null) {
            // TODO Check when no device connected
            cloverGo450Connector.disconnectDevice();
        }
    }

    /* Utility methods */

    /**
    * The method checks if there are all the permissions required for the Clover Go SDK
    */
    public boolean hasPermisssion() {
        for(String permission : permissions) {
            if(!PermissionHelper.hasPermission(this, permission)) {
                return false;
            }
        }
        return true;
    }

    void sendCallback(PluginResult.Status status, JSONObject obj, Boolean isKeepCallBack) {
        PluginResult result = new PluginResult(status, obj);
        result.setKeepCallback(isKeepCallBack);
        rootCallbackContext.sendPluginResult(result);
    }
    void sendExceptionCallback(String errorMsg, Boolean isKeepCallBack) {
        PluginResult result = new PluginResult(PluginResult.Status.ERROR, errorMsg);
        result.setKeepCallback(isKeepCallBack);
        rootCallbackContext.sendPluginResult(result);
    }
    int[] convertJSONArrayToJavaArray(JSONArray jsonArray) {
        int[] javaArray = new int[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); ++i) {
            javaArray[i] = jsonArray.optInt(i);
        }
        return javaArray;
    }
}