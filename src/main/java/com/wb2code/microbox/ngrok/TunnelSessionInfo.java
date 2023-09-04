package com.wb2code.microbox.ngrok;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author lwp
 * @date 2023-09-15
 **/
@Data
public class TunnelSessionInfo {
    private String id;
    private String ip;
    private String os;
    private String region;
    @SerializedName("started_at")
    private String startedAt;
    private String transport;
    private String uri;
    @SerializedName("agent_version")
    private String agentVersion;
}
