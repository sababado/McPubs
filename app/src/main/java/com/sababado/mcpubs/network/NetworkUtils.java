package com.sababado.mcpubs.network;

import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.sababado.mcpubs.BuildConfig;
import com.sababado.mcpubs.R;
import com.sababado.mcpubs.backend.device.Device;
import com.sababado.mcpubs.backend.pub.Pub;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by robert on 9/17/15.
 */
public class NetworkUtils {
    private static Map<Class<? extends AbstractGoogleJsonClient>, AbstractGoogleJsonClient> map = new HashMap<>(5);

    public static Device.DeviceEndpoint getDeviceService(Context context) {
        Device device = (Device) map.get(Device.class);
        if (device != null) {
            return device.deviceEndpoint();
        }
        Device.Builder builder = new Device.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null);
        initBuilder(builder, context);
        device = builder.build();
        map.put(Device.class, device);
        return device.deviceEndpoint();
    }

    public static Pub.PubEndpoint getPubService(Context context) {
        Pub pub = (Pub) map.get(Pub.class);
        if (pub != null) {
            return pub.pubEndpoint();
        }
        Pub.Builder builder = new Pub.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null);
        initBuilder(builder, context);
        pub = builder.build();
        map.put(Pub.class, pub);
        return pub.pubEndpoint();
    }

    private static <T extends AbstractGoogleJsonClient.Builder> void initBuilder(T builder, Context context) {
        // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
        // otherwise they can be skipped
        builder.setApplicationName(context.getString(R.string.app_engine_name));
        if (BuildConfig.DEBUG) {
            builder.setRootUrl("http://" + BuildConfig.LOCAL_IP + ":8080/_ah/api");
        } else {
            builder.setRootUrl("https://voltaic-flag-141523.appspot.com/_ah/api/");
        }
//                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//                    @Override
//                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
//                            throws IOException {
//                        abstractGoogleClientRequest.setDisableGZipContent(true);
//                    }
//                });
    }
}
