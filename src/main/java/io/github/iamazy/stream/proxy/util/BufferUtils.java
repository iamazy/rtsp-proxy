package io.github.iamazy.stream.proxy.util;

import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author iamazy
 * @date 2019/11/28
 * @descrition
 **/
public class BufferUtils {

    public static String buffer2String(ByteBuffer buffer) {
        try {
            CharBuffer charBuffer = StandardCharsets.UTF_8.newDecoder().decode(buffer);
            buffer.flip();
            return charBuffer.toString();
        } catch (CharacterCodingException e) {
            e.printStackTrace();
            return StringUtils.EMPTY;
        }
    }
}
