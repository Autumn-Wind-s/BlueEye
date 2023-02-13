package com.rdb;

import com.container.DataCenter;
import com.util.KryoUtil;

import java.io.*;
import java.util.Optional;

/**
 * @Author SDJin
 * @CreationDate 2023/2/4 16:08
 * @Description ：
 */
public class Rdb {
    /**
     * 数据中心
     */
    private final DataCenter dataCenter;
    /**
     * 持久化路径,提供默认路径为资源路径的rdb目录下
     */
    private final String serializedPath;
    /**
     * 保存的文件个数，提供默认值：2
     */
    private final Integer nums;

    private Integer num = 0;

    /**
     * 项目初始化时创建Rdb服务类
     *
     * @param dataCenter     项目的数据中心
     * @param serializedPath 配置文件配置Rdb文件持久化路径
     */
    public Rdb(DataCenter dataCenter, Optional<String> serializedPath, Optional<Integer> nums) {
        this.dataCenter = dataCenter;
        this.serializedPath = serializedPath.orElse(this.getClass().getClassLoader().getResource("").getPath() + "rdb");
        this.nums = nums.orElse(2);
    }


    /**
     * 项目重启时创建Rdb
     *
     * @param filePath
     * @param serializedPath
     * @throws IOException
     */
    public Rdb(String filePath, Optional<String> serializedPath, Optional<Integer> nums) throws IOException {
        this.serializedPath = serializedPath.orElse(this.getClass().getClassLoader().getResource("").getPath() + "rdb");
        this.nums = nums.orElse(2);
        this.dataCenter=restore(filePath);
    }

    /**
     * 数据恢复
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public DataCenter restore(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream in = new FileInputStream(file);
        byte[] bytes = new byte[in.available()];
        in.read(bytes);
        in.close();
        DataCenter dataCenter = new DataCenter();
        dataCenter.restoreSnapshot(KryoUtil.readObjectFromByteArray(bytes, Snapshot.class));
        return dataCenter;
    }

    /**
     * 数据备份，序列化RDB文件，循环覆盖
     * @throws IOException
     */
    public void backup() throws IOException {
        byte[] bytes = KryoUtil.writeObjectToByteArray(this.dataCenter.createSnapshot());
        File file = new File(serializedPath+"RDB"+num+".txt");
        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(bytes);
        outputStream.close();
        num=(num+1)%nums;
    }

    public DataCenter getDataCenter() {
        return dataCenter;
    }
}
