package com.wb2code.microbox.ngrok;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

/**
 * @author lwp
 * @date 2023-09-15
 **/
@Data
public class TunnelInfos {
    /**
     *
     */
    private List<TunnelInfo> tunnels;
    @SerializedName("next_page_uri")
    private String nextPageUri;
    private String uri;
}
