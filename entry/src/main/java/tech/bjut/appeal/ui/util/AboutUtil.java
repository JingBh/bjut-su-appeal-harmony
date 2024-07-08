package tech.bjut.appeal.ui.util;

import ohos.app.Context;
import ohos.bundle.BundleInfo;
import ohos.bundle.IBundleManager;
import tech.bjut.appeal.ResourceTable;
import tech.bjut.appeal.data.service.WebService;
import tech.bjut.appeal.data.util.ValueCallback;

public class AboutUtil {

    private static String serviceVersion = null;

    public static String appVersion(Context context) {
        try {
            //noinspection PermissionCheck
            BundleInfo info = context.getBundleManager()
                .getBundleInfo(context.getBundleName(), IBundleManager.GET_BUNDLE_DEFAULT);
            return info.getVersionName();
        } catch (Exception e) {
            return context.getString(ResourceTable.String_profile_version_load_failed);
        }
    }

    public static void serviceVersion(Context context, ValueCallback<String> callback) {
        if (serviceVersion == null) {
            context.getMainTaskDispatcher().asyncDispatch(() -> WebService.getServiceVersion(version -> {
                if (version != null) {
                    serviceVersion = version;
                    context.getUITaskDispatcher().syncDispatch(() -> callback.call(serviceVersion));
                } else {
                    context.getUITaskDispatcher().syncDispatch(() -> {
                        callback.call(context.getString(ResourceTable.String_profile_version_load_failed));
                    });
                }
            }));
            return;
        }

        callback.call(serviceVersion);
    }
}
