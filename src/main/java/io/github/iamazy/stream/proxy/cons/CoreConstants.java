package io.github.iamazy.stream.proxy.cons;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.iamazy.stream.proxy.model.RtspProxy;
import org.bson.codecs.ObjectIdGenerator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author iamazy
 * @date 2019/11/28
 * @descrition
 **/
public interface CoreConstants {

    ObjectIdGenerator OBJECT_ID_GENERATOR=new ObjectIdGenerator();

    /**
     * 内网IP
     */
    String IP="localhost";

    /**
     * 外网IP
     */
    String OUTSIDE_IP ="localhost";

    DateFormat DATE_TIME_FORMAT=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


    Pattern RTSP_IP_PORT_PATTERN = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)");


    Map<String, RtspProxy> RTSP_PROXY_CACHE =new HashMap<>(0);

    Map<String,String> ORIGIN_URL_CACHE =new HashMap<>(0);

    Map<String,String> IP_PORT_SRC_URL_CACHE=new HashMap<>(0);
}
