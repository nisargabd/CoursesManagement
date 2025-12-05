package com.sanketika.mapper;


import com.sanketika.utils.ApiEnvelope;
import com.sanketika.utils.ApiResult;
import com.sanketika.utils.Params;

import java.time.Instant;
import java.util.UUID;

public class ResponseMapper {

    public static <T> ApiEnvelope<T> success(String id, String message, T result) {
        ApiEnvelope<T> envelope = new ApiEnvelope<>();
        envelope.setId(id);
        envelope.setVer("v1");
        envelope.setTs(Instant.now().toString());
        envelope.setResponseCode("OK");

        Params params = new Params();
        params.setMsgid(UUID.randomUUID().toString());
        params.setStatus("success");
        params.setErr(null);
        params.setErrmsg(null);
        envelope.setParams(params);

        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setMessage(message);
        apiResult.setData(result);
        envelope.setResult(apiResult);

        return envelope;
    }
}
