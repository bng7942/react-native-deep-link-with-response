package com.reactnative.andersonfrfilho.deeplinkwithresponse;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.Nullable;
import android.util.SparseArray;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import java.util.HashMap;
import java.util.Map;

public class DeepLinkWithResponseModule extends ReactContextBaseJavaModule implements RNCActivityEventListener {
    final SparseArray<Promise> mPromises;
    private final ReactApplicationContext reactContext;

    public DeepLinkWithResponseModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mPromises = new SparseArray<>();
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "DeepLinkWithResponse";
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        HashMap<String, Object> constants = new HashMap<>();
        constants.put("OK", Activity.RESULT_OK);
        constants.put("CANCELED", Activity.RESULT_CANCELED);
        return constants;
    }
    @Override
    public void initialize() {
        super.initialize();
        getReactApplicationContext().addActivityEventListener(this);
    }

    @Override
    public void onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy();
        getReactApplicationContext().removeActivityEventListener(this);
    }

    @ReactMethod
    public void startActivityForResult(int requestCode, String deep, ReadableMap data, Promise promise) {
        Activity activity = getCurrentActivity();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(deep));
        intent.putExtras(ArgumentsModuleDeepLinkResponse.toBundle(data));
        activity.startActivityForResult(intent, requestCode);
        mPromises.put(requestCode, promise);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Promise promise = mPromises.get(requestCode);
        if (promise != null) {
            WritableMap result = new WritableNativeMap();
            result.putInt("resultCode", resultCode);
            result.putMap("data", ArgumentsModuleDeepLinkResponse.makeNativeMap(data.getExtras()));
            promise.resolve(result);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        //
    }
}
