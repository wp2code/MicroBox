package com.wb2code.microbox.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import com.github.alexdlaird.http.DefaultHttpClient;
import com.github.alexdlaird.http.Response;
import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Tunnel;
import com.wb2code.microbox.annotation.entity.Result;
import com.wb2code.microbox.ngrok.TunnelInfo;
import com.wb2code.microbox.ngrok.TunnelInfos;
import com.wb2code.microbox.ngrok.TunnelSessionInfo;
import com.wb2code.microbox.ngrok.TunnelSessionInfos;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lwp
 * @date 2023-08-18
 **/
@UtilityClass
public class NgrokUtil {

    /**
     * @param auth
     * @return
     */
    public NgrokClient getNgrokClient(String auth) {
        final JavaNgrokConfig javaNgrokConfig = new JavaNgrokConfig.Builder().withAuthToken(auth).build();
        final NgrokClient ngrokClient = new NgrokClient.Builder().withJavaNgrokConfig(javaNgrokConfig).build();
        return ngrokClient;

    }

    /**
     * @param auth
     * @param port
     */
    public Result<String> createHttpNg(String auth, String name, String port) {
        try {
            final NgrokClient ngrokClient = getNgrokClient(auth);
            final CreateTunnel tunnel = new CreateTunnel.Builder().withProto(Proto.HTTP).withAddr(port).withName(name).build();
            final Tunnel httpTunnel = ngrokClient.connect(tunnel);
            final String publicUrl = httpTunnel.getPublicUrl();
            return Result.success(publicUrl);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }


    /**
     * @param auth
     * @param publicUrl
     */
    public boolean stopHttpNg(String auth, String publicUrl) {
        try {
            final NgrokClient ngrokClient = getNgrokClient(auth);
            ngrokClient.disconnect(publicUrl);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param apiKeyAuth
     * @param sessionId
     * @return
     */
    public boolean stopSession(String apiKeyAuth, String sessionId) {
        try {
            final Map<String, String> hashMap = new HashMap<>(3);
            hashMap.put("Authorization", "Bearer " + apiKeyAuth);
            hashMap.put("Content-Type", "application/json");
            hashMap.put("Ngrok-Version", "2");
            final HttpRequest request = HttpUtil.createRequest(Method.POST, "https://api.ngrok.com/tunnel_sessions/" + sessionId + "/stop");
            request.addHeaders(hashMap);
            request.body(JSONUtil.toJsonStr(new HashMap<>()));
            final HttpResponse response = request.execute();
            return response.isOk();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param apiKeyAuth
     * @return
     */
    public List<TunnelInfo> getTunnelList(String apiKeyAuth) {
        try {
            final Map<String, String> hashMap = new HashMap<>(2);
            hashMap.put("Authorization", "Bearer " + apiKeyAuth);
            hashMap.put("Ngrok-Version", "2");
            final DefaultHttpClient httpClient = new DefaultHttpClient.Builder().build();
            final Response<TunnelInfos> tunnelsResponse = httpClient.get("https://api.ngrok.com/tunnels", null, hashMap, TunnelInfos.class);
            final TunnelInfos body = tunnelsResponse.getBody();
            final List<TunnelInfo> tunnels = body.getTunnels();
            return tunnels;
        } catch (Exception e) {
        }
        return null;
    }

    public Set<String> getTunnelSessionIds(String apiKeyAuth) {
        final List<TunnelSessionInfo> tunnelSessionList = getTunnelSessionList(apiKeyAuth);
        if (CollUtil.isNotEmpty(tunnelSessionList)) {
            return tunnelSessionList.stream().map(TunnelSessionInfo::getId).collect(Collectors.toSet());
        }
        return null;
    }

    /**
     * @param apiKeyAuth
     * @return
     */
    public List<TunnelSessionInfo> getTunnelSessionList(String apiKeyAuth) {
        try {
            final Map<String, String> hashMap = new HashMap<>(2);
            hashMap.put("Authorization", "Bearer " + apiKeyAuth);
            hashMap.put("Ngrok-Version", "2");
            final DefaultHttpClient httpClient = new DefaultHttpClient.Builder().build();
            final Response<TunnelSessionInfos> tunnelsResponse = httpClient.get("https://api.ngrok.com/tunnel_sessions", null, hashMap, TunnelSessionInfos.class);
            final TunnelSessionInfos body = tunnelsResponse.getBody();
            final List<TunnelSessionInfo> tunnels = body.getTunnelSessions();
            return tunnels;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param apiKeyAuth
     * @param tunnelId
     * @return
     */
    public TunnelInfo getTunnel(String apiKeyAuth, String tunnelId) {
        try {
            final Map<String, String> hashMap = new HashMap<>(2);
            hashMap.put("Authorization", "Bearer " + apiKeyAuth);
            hashMap.put("Ngrok-Version", "2");
            final DefaultHttpClient httpClient = new DefaultHttpClient.Builder().build();
            final Response<TunnelInfo> tunnelsResponse = httpClient.get("https://api.ngrok.com/tunnels/" + tunnelId, null, hashMap, TunnelInfo.class);
            final TunnelInfo body = tunnelsResponse.getBody();
            return body;
        } catch (Exception e) {
        }
        return null;
    }
}
