package tech.bjut.appeal.data.service;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import tech.bjut.appeal.ResourceTable;
import tech.bjut.appeal.data.model.*;
import tech.bjut.appeal.data.util.ValueCallback;

import java.io.IOException;

public class WebService {

    private static final HiLogLabel LOG_LABEL = new HiLogLabel(HiLog.LOG_APP, 0, "WebService");

    public static final String BASE_URL = "https://appeal.bjut.tech/api";

    private static final OkHttpClient client = new OkHttpClient();

    public static Call postToken(LoginRequestDto data, Callback callback) {
        Request request = new Request.Builder()
            .post(data.toFormBody())
            .url(BASE_URL + "/token")
            .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call getUser(String token, ValueCallback<User> callback) {
        Request request = new Request.Builder()
            .url(BASE_URL + "/user")
            .addHeader("Authorization", "Bearer " + token)
            .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                HiLog.error(LOG_LABEL, e.getMessage());
                callback.call(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200 && response.body() != null) {
                    Moshi moshi = new Moshi.Builder().build();
                    JsonAdapter<AuthenticationResponseDto> jsonAdapter = moshi.adapter(AuthenticationResponseDto.class);
                    AuthenticationResponseDto authentication = jsonAdapter.fromJson(response.body().source());
                    if (authentication != null && authentication.getUser() != null) {
                        callback.call(authentication.getUser());
                        return;
                    }
                }
                callback.call(null);
            }
        });
        return call;
    }

    public static Call getUserCount(String token, ValueCallback<UserCountResponseDto> callback) {
        Request request = new Request.Builder()
            .url(BASE_URL + "/questions/count")
            .addHeader("Authorization", "Bearer " + token)
            .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                HiLog.error(LOG_LABEL, e.getMessage());
                callback.call(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200 && response.body() != null) {
                    Moshi moshi = new Moshi.Builder().build();
                    JsonAdapter<UserCountResponseDto> jsonAdapter = moshi.adapter(UserCountResponseDto.class);
                    UserCountResponseDto userCount = jsonAdapter.fromJson(response.body().source());
                    callback.call(userCount);
                    return;
                }
                callback.call(null);
            }
        });
        return call;
    }

    public static Call getServiceVersion(ValueCallback<String> callback) {
        Request request = new Request.Builder()
            .url(BASE_URL + "/actuator/info")
            .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                HiLog.error(LOG_LABEL, e.getMessage());
                callback.call(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() == null) {
                    callback.call(null);
                    return;
                }

                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<ActuatorInfoResponseDto> adapter = moshi.adapter(ActuatorInfoResponseDto.class);
                ActuatorInfoResponseDto info = adapter.fromJson(response.body().source());
                if (info == null || info.getBuild() == null) {
                    callback.call(null);
                    return;
                }

                callback.call(info.getBuild().getVersion());
            }
        });
        return call;
    }
}
