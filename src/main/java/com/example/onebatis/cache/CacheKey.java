package com.example.onebatis.cache;

/**
 * <p>
 *      白嫖一个类
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/17 15:43
 **/
public class CacheKey implements Cloneable {

    /** 乘法 hash , 质数，减少hash碰撞 **/
    private static final int DEFAULT_MULTIPLYER = 37;
    /** 加法 hash 质数，减少hash碰撞 **/
    private static final int DEFAULT_HASHCODE = 17;

    private final int multiplier;
    private int hashcode;
    private long checksum;
    private int count;

    public CacheKey(){
        this.multiplier = DEFAULT_MULTIPLYER;
        this.hashcode = DEFAULT_HASHCODE;
        this.count = 0;
    }


    public void update(Object object) {
        int baseHashCode = object == null ? 1 : object.hashCode();

        count++;
        checksum += baseHashCode;
        baseHashCode *= count;

        hashcode = multiplier * hashcode + baseHashCode;
    }

    public void updateAll(Object[] objects) {
        for (Object o : objects) {
            update(o);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof CacheKey)) {
            return false;
        }
        final CacheKey cacheKey = (CacheKey) object;

        if (hashcode != cacheKey.hashcode) {
            return false;
        }
        if (checksum != cacheKey.checksum) {
            return false;
        }
        if (count != cacheKey.count) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return hashcode;
    }
}
