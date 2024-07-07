package tech.bjut.appeal.ui.util;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import ohos.app.Context;
import ohos.bundle.BundleInfo;
import ohos.bundle.IBundleManager;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import tech.bjut.appeal.ResourceTable;
import tech.bjut.appeal.data.model.ActuatorInfoResponseDto;
import tech.bjut.appeal.data.service.WebService;
import tech.bjut.appeal.data.util.ValueCallback;

import java.io.IOException;

public class AboutUtil {

    private static final HiLogLabel LOG_LABEL = new HiLogLabel(HiLog.LOG_APP, 0, "AboutUtil");

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
            context.getMainTaskDispatcher().asyncDispatch(() -> WebService.getServiceVersion(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    HiLog.error(LOG_LABEL, e.getMessage());
                    context.getUITaskDispatcher().syncDispatch(() -> {
                        callback.call(context.getString(ResourceTable.String_profile_version_load_failed));
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.body() == null) {
                        context.getUITaskDispatcher().syncDispatch(() -> {
                            callback.call(context.getString(ResourceTable.String_profile_version_load_failed));
                        });
                        return;
                    }

                    Moshi moshi = new Moshi.Builder().build();
                    JsonAdapter<ActuatorInfoResponseDto> adapter = moshi.adapter(ActuatorInfoResponseDto.class);
                    ActuatorInfoResponseDto info = adapter.fromJson(response.body().source());
                    if (info == null || info.getBuild() == null) {
                        context.getUITaskDispatcher().syncDispatch(() -> {
                            callback.call(context.getString(ResourceTable.String_profile_version_load_failed));
                        });
                        return;
                    }

                    serviceVersion = info.getBuild().getVersion();
                    context.getUITaskDispatcher().syncDispatch(() -> callback.call(info.getBuild().getVersion()));
                }
            }));
            return;
        }

        callback.call(serviceVersion);
    }
}
