package tech.bjut.appeal.data.service;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.bjut.appeal.data.model.*;
import tech.bjut.appeal.data.util.CacheUtil;
import tech.bjut.appeal.data.util.TokenUtil;
import tech.bjut.appeal.data.util.ValueCallback;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

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

    public static Call getUser(ValueCallback<User> callback) {
        Request request = new Request.Builder()
            .url(BASE_URL + "/user")
            .addHeader("Authorization", "Bearer " + TokenUtil.getToken())
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
                if (response.isSuccessful() && response.body() != null) {
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

    public static Call getUserCount(ValueCallback<UserCountResponseDto> callback) {
        Request request = new Request.Builder()
            .url(BASE_URL + "/questions/count")
            .addHeader("Authorization", "Bearer " + TokenUtil.getToken())
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
                if (response.isSuccessful() && response.body() != null) {
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

    public static Call getAnnouncements(@Nullable String cursor, ValueCallback<CursorPaginationDto<Announcement>> callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "/announcements").newBuilder();
        if (cursor != null) {
            urlBuilder.addQueryParameter("cursor", cursor);
        }
        Request request = new Request.Builder()
            .url(urlBuilder.build())
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
                if (response.isSuccessful() && response.body() != null) {
                    Moshi moshi = new Moshi.Builder().build();
                    Type adapterType = Types.newParameterizedType(CursorPaginationDto.class, Announcement.class);
                    JsonAdapter<CursorPaginationDto<Announcement>> jsonAdapter = moshi.adapter(adapterType);
                    CursorPaginationDto<Announcement> announcements = jsonAdapter.fromJson(response.body().source());
                    callback.call(announcements);
                    return;
                }
                callback.call(null);
            }
        });
        return call;
    }

    public static Call getQuestions(@Nullable String cursor, boolean history, ValueCallback<CursorPaginationDto<Question>> callback) {
        HttpUrl.Builder urlBuilder = HttpUrl
            .parse(BASE_URL + (history ? "/questions/history" : "/questions"))
            .newBuilder();
        if (cursor != null) {
            urlBuilder.addQueryParameter("cursor", cursor);
        }
        Request.Builder requestBuilder = new Request.Builder()
            .url(urlBuilder.build());
        if (history) {
            requestBuilder.addHeader("Authorization", "Bearer " + TokenUtil.getToken());
        }
        Call call = client.newCall(requestBuilder.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                HiLog.error(LOG_LABEL, e.getMessage());
                callback.call(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    Moshi moshi = new Moshi.Builder().build();
                    Type adapterType = Types.newParameterizedType(CursorPaginationDto.class, Question.class);
                    JsonAdapter<CursorPaginationDto<Question>> jsonAdapter = moshi.adapter(adapterType);
                    CursorPaginationDto<Question> questions = jsonAdapter.fromJson(response.body().source());
                    callback.call(questions);
                    return;
                }
                callback.call(null);
            }
        });
        return call;
    }

    public static Call postQuestion(QuestionRequestDto data, Callback callback) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<QuestionRequestDto> jsonAdapter = moshi.adapter(QuestionRequestDto.class);

        Request.Builder requestBuilder = new Request.Builder()
            .post(RequestBody.create(jsonAdapter.toJson(data), MediaType.get("application/json")))
            .url(BASE_URL + "/questions");
        if (TokenUtil.getToken() != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + TokenUtil.getToken());
        }

        Call call = client.newCall(requestBuilder.build());
        call.enqueue(callback);
        return call;
    }

    public static Call deleteQuestion(long id, ValueCallback<Boolean> callback) {
        Request request = new Request.Builder()
            .delete()
            .url(BASE_URL + "/questions/" + id)
            .addHeader("Authorization", "Bearer " + TokenUtil.getToken())
            .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                HiLog.error(LOG_LABEL, e.getMessage());
                callback.call(false);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                callback.call(response.isSuccessful());
            }
        });
        return call;
    }

    public static Call getAttachment(String id, ValueCallback<InputStream> callback) {
        OkHttpClient cachedClient = client.newBuilder()
            .cache(new Cache(CacheUtil.getCacheDir(), 100 * 1024 * 1024))
            .build();
        Request request = new Request.Builder()
            .url(BASE_URL + "/attachments/" + id)
            .build();
        Call call = cachedClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                HiLog.error(LOG_LABEL, e.getMessage());
                callback.call(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    callback.call(response.body().byteStream());
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
