/* Copyright (c) 2016 HoNKoT.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.honkot.android.billingcontroller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;

/**
 * Controller of IInAppBillingService.<br />
 * You need IInAppBillingService.aidl and complete build including it before use this class.
 */
public class BillingController {

    /** Test product ID which can be purchased */
    public static final String TESTPRODUCT_PURCHASED = "android.test.purchased";
    /** Test product ID which can be canceled */
    public static final String TESTPRODUCT_CANCELED = "android.test.canceled";
    /** Test product ID which can be refunded */
    public static final String TESTPRODUCT_REFUNDED = "android.test.refunded";
    /** Test product ID which cannot find any product ID */
    public static final String TESTPRODUCT_ITEM_UNAVAILABLE = "android.test.item_unavailable";

    /** 0 if the purchase was success, error otherwise. */
    public static final String RESPONSE_CODE = "RESPONSE_CODE";
    /** A String in JSON format that contains details about the purchase order.<br />
     *  See table 4 for a description of the JSON fields. */
    public static final String INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    /** String containing the signature of the purchase data that was signed with the private key of the developer. */
    public static final String INAPP_DATA_SIGNATURE = "INAPP_DATA_SIGNATURE";

    /**
     * From Android Developer webpage.
     * @link https://developer.android.com/google/play/billing/billing_reference.html#billing-codes
     */
    /** Success */
    public static final Integer BILLING_RESPONSE_RESULT_OK     = 0;
    /** User pressed back or canceled a dialog */
    public static final Integer BILLING_RESPONSE_RESULT_USER_CANCELED  = 1;
    /** Network connection is down */
    public static final Integer BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE = 2;
    /** Billing API version is not supported for the type requested */
    public static final Integer BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
    /** Requested product is not available for purchase */
    public static final Integer BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
    /** Invalid arguments provided to the API.
     *  This error can also indicate that the application was not correctly signed or properly set up
     *  for In-app Billing in Google Play, or does not have the necessary permissions in its manifest */
    public static final Integer BILLING_RESPONSE_RESULT_DEVELOPER_ERROR  = 5;
    /** Fatal error during the API action */
    public static final Integer BILLING_RESPONSE_RESULT_ERROR    = 6;
    /** Failure to purchase since item is already owned */
    public static final Integer BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
    /** Failure to consume since item is not owned */
    public static final Integer BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED  = 8;

    private IInAppBillingService mBillingService = null;
    private ServiceConnection mServiceConnection = null;
    private Context mContext;
    private OnServiceResponseListener mListener;

    private final String TAG = "BuillingController";
    public static final int API_VERSION = 3;
    public static final int ACTIVITY_RESULT_CODE = 999;

    /**
     * Listener for watching service state.
     */
    public interface OnServiceResponseListener {
        void onServiceConnected();
        void onServiceDisconnected();
    }

    public BillingController(Context context, OnServiceResponseListener listener) {
        // error check
        if (context == null || listener == null) return;

        // save valuable
        mContext = context;
        mListener = listener;

        // get service connection for IInAppBillingService
        mServiceConnection = new ServiceConnection(){
            @Override
            public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder){
                // save stub
                Log.i(TAG, "Connected to service.");
                mBillingService = IInAppBillingService.Stub.asInterface(paramIBinder);
                mListener.onServiceConnected();
            }
            @Override
            public void onServiceDisconnected(ComponentName paramComponentName){
                // put null into stub holder when service is disconnected
                Log.e(TAG, "Service is disconnected. (Called onServiceDisconnected)");
                mBillingService = null;
                mListener.onServiceDisconnected();
            }
        };

        // bind service
        Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage("com.android.vending");
        mContext.bindService(
                intent,
                this.mServiceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    /**
     * Check something error caused or not.
     * @return true: ERROR
     */
    public boolean isError() {
        return mBillingService == null || mServiceConnection == null;
    }

    /**
     * This class should be called finalize faze such as onDestroy().
     */
    public void release() {
        if( mServiceConnection != null ){
            mContext.unbindService(mServiceConnection);
        }
        mBillingService = null;
        mContext = null;
    }

    public void finalize() {
        // just in case
        release();
    }
}
