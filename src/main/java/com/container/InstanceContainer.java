package com.container;

import com.pojo.instance.TaskInstance;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:06
 * @Description ：
 */
public class InstanceContainer {
    /**
     * 使用List是为了方便分页查询
     */
    private ConcurrentSkipListMap<Integer, List<Integer> > mapping;
    private ConcurrentSkipListMap<Integer, TaskInstance> map;


}
