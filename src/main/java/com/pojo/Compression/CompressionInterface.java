package com.pojo.Compression;

/**
 * @Author SDJin
 * @CreationDate 2023/3/12 19:27
 * @Description ：压缩接口，提供对象压缩抽象方法
 */
public interface CompressionInterface<T> {
    /**
     * 将对象压缩为对应的po对象
     * @return
     */
    public T compress();
}
