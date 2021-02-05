package com.example.onebatis.logging.stdout;

import com.example.onebatis.logging.Log;

/**
 * <p>
 *      StdOutImpl 日志实现
 * </p>
 *
 * @author zyred
 * @since v 0.1
 **/
public class StdOutImpl implements Log {

    public StdOutImpl(String clazz) {
        // Do Nothing
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void error(String s, Throwable e) {
        System.out.println(s);
        e.printStackTrace(System.err);
    }

    @Override
    public void error(String s) {
        System.out.println(s);
    }

    @Override
    public void debug(String s) {
        System.out.println(s);
    }

    @Override
    public void trace(String s) {
        System.out.println(s);
    }

    @Override
    public void warn(String s) {
        System.out.println(s);
    }
}
