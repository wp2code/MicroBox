package com.wb2code.microbox.ngrok;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Map;

/**
 * @author lwp
 * @date 2023-09-15
 **/
@Data
public class TunnelInfo {
    private String id;
    private String proto;
    @SerializedName("public_url")
    private String publicUrl;
    @SerializedName("forwards_to")
    private String forwardsTo;
    @SerializedName("tunnel_session")
    private Uri tunnelSession;
    @SerializedName("started_at")
    private String startedAt;
    private String region;
    private Uri endpoint;
    private Uri backends;
    private Map<String,String> labels;

    @Data
    public static class Uri {
        private String id;
        private String uri;
    }

}
