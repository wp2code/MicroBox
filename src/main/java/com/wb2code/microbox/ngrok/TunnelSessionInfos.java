package com.wb2code.microbox.ngrok;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

/**
 * @author lwp
 * @date 2023-09-15
 **/
@Data
public class TunnelSessionInfos {
    @SerializedName("tunnel_sessions")
    private List<TunnelSessionInfo> tunnelSessions;
    @SerializedName("next_page_uri")
    private String nextPageUri;
    private String uri;
}
