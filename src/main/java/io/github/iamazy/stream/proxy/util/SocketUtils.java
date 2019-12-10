package io.github.iamazy.stream.proxy.util;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author iamazy
 * @date 2019/11/28
 * @descrition
 **/
public class SocketUtils {


    public static int randomPort(){
        try {
            ServerSocket socket=new ServerSocket(0);
            int port=socket.getLocalPort();
            socket.close();
            return port;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
