package com.example.onebatis.logging;

/**
 * <p>
 *      日志操作顶层接口
 * </p>
 *
 * @author zyred
 * @since v 0.1
 **/
public interface Log {

    boolean isDebugEnabled();

    boolean isTraceEnabled();

    void error(String s, Throwable e);

    void error(String s);

    void debug(String s);

    void trace(String s);

    void warn(String s);

}
