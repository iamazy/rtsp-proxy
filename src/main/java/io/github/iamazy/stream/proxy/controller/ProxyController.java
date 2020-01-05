package io.github.iamazy.stream.proxy.controller;

import io.github.iamazy.stream.proxy.cons.CoreConstants;
import io.github.iamazy.stream.proxy.model.RtspProxy;
import io.github.iamazy.stream.proxy.proxy.IceProxy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author iamazy
 * @date 2019/11/28
 * @descrition
 **/
@RestController
@RequestMapping("/stream")
public class ProxyController {

    @PostMapping("/proxy")
    public ResponseEntity<Map<String, Object>> proxy2(@RequestBody Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>(0);
        if (params.containsKey("url") && params.containsKey("username") && params.containsKey("password")) {
            String originUrl = params.get("url").toString();
            if (CoreConstants.ORIGIN_URL_CACHE.containsKey(originUrl)) {
                String streamId = CoreConstants.ORIGIN_URL_CACHE.get(originUrl);
                map.put("url", CoreConstants.RTSP_PROXY_CACHE.get(streamId).getProxyUrl());
                map.put("streamId", CoreConstants.RTSP_PROXY_CACHE.get(streamId).getStreamId());
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            RtspProxy rtspProxy = new RtspProxy(params);
            CoreConstants.RTSP_PROXY_CACHE.put(rtspProxy.getStreamId(), rtspProxy);
            CoreConstants.ORIGIN_URL_CACHE.put(originUrl, rtspProxy.getStreamId());
            IceProxy iceProxy = new IceProxy(rtspProxy);
            map.put("url", rtspProxy.getProxyUrl());
            map.put("streamId", rtspProxy.getStreamId());
            iceProxy.start();
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            map.put("error", "参数不完整");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list")
    public Map<String, Object> list() {
        Map<String, Object> cache = new HashMap<>(0);
        for (Map.Entry<String, RtspProxy> entry : CoreConstants.RTSP_PROXY_CACHE.entrySet()) {
            cache.put(entry.getKey(), entry.getValue().getProxyUrl());
        }
        return cache;
    }

    @DeleteMapping("/{id}")
    public String removeStream(@PathVariable("id") String threadName) {
        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
        int numThreads = currentGroup.activeCount();
        Thread[] threads = new Thread[numThreads];
        currentGroup.enumerate(threads);
        for (int i = 0; i < numThreads; i++) {
            String thrName = threads[i].getName();
            if (threadName.equalsIgnoreCase(thrName)) {
                threads[i].interrupt();
            }
        }
        String idRouting = null;
        for (Map.Entry<String, String> entry : CoreConstants.ORIGIN_URL_CACHE.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(threadName)) {
                idRouting = entry.getKey();
                break;
            }
        }
        if (idRouting != null) {
            CoreConstants.ORIGIN_URL_CACHE.remove(idRouting);
        }
        CoreConstants.RTSP_PROXY_CACHE.remove(threadName);
        return "流成功关闭";
    }
}
