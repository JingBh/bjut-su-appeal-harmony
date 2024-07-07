package tech.bjut.appeal.data.service;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import tech.bjut.appeal.data.model.LoginRequestDto;

public class WebService {

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

    public static Call getServiceVersion(Callback callback) {
        Request request = new Request.Builder()
            .url(BASE_URL + "/actuator/info")
            .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
}
