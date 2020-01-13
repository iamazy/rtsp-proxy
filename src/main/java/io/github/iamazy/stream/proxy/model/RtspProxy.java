package io.github.iamazy.stream.proxy.model;

import io.github.iamazy.stream.proxy.cons.CoreConstants;
import io.github.iamazy.stream.proxy.util.SocketUtils;

import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author iamazy
 * @date 2019/11/28
 * @descrition
 **/
public class RtspProxy {

    private String srcUrl;

    /**
     * 真实的rtsp ip
     */
    private String remoteIp;

    /**
     * 真实的rtsp port
     */
    private int remotePort;

    private String localIp;

    private int localPort;

    private String streamId;

    private String username;

    private String password;

    public RtspProxy(Map<String, Object> source) {
        this.srcUrl = source.get("url").toString();
        this.username = source.get("username").toString();
        this.password = source.get("password").toString();
        this.localIp = CoreConstants.IP;
        this.localPort = SocketUtils.randomPort();
        CoreConstants.IP_PORT_SRC_URL_CACHE.put(CoreConstants.OUTSIDE_IP+":"+localPort,srcUrl);
        this.streamId = CoreConstants.OBJECT_ID_GENERATOR.generate().toString();
        Matcher matcher = CoreConstants.RTSP_IP_PORT_PATTERN.matcher(srcUrl);
        while (matcher.find()) {
            this.remoteIp = matcher.group(1);
            this.remotePort = Integer.parseInt(matcher.group(2));
        }

    }

    public int getLocalPort() {
        return localPort;
    }

    public String getLocalIp() {
        return localIp;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public String getSrcUrl() {
        return srcUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getStreamId() {
        return streamId;
    }

    public String getProxyUrl() {
        return "rtsp://" + CoreConstants.OUTSIDE_IP + ":" + this.localPort + "/" + this.streamId;
    }
}
