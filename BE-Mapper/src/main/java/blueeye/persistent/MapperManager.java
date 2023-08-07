package blueeye.persistent;

import blueeye.config.BlueEyeConfig;
import blueeye.persistent.defaultImpl.JdbcMapperManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @Author SDJin
 * @CreationDate 2023/2/16 17:39
 * @Description ：主要是做数据持久化的初始化和终止工作
 */
public abstract class MapperManager {
    public final AlertRecordMapper alertRecordMapper;
    public final InstanceMapper instanceMapper;
    public final MetricDataMapper metricDataMapper;
    public final TaskMapper taskMapper;
    public final String properties;
    public MapperManager(AlertRecordMapper alertRecordMapper, InstanceMapper instanceMapper, MetricDataMapper metricDataMapper, TaskMapper taskMapper,String properties) {
        this.alertRecordMapper = alertRecordMapper;
        this.instanceMapper = instanceMapper;
        this.metricDataMapper = metricDataMapper;
        this.taskMapper = taskMapper;
        this.properties=properties;
    }



    /**
     * 主要做数据持久化的初始化工作
     */
    public abstract void init();

    /**
     * 主要做数据持久化的终止工作，例如释放资源
     */
    public abstract void destroy();

    /**
     * 初始化持久化数据读写服务
     *
     * @param config
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static MapperManager initMapperService(BlueEyeConfig config) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?> constructor = Class.forName(config.getManager()).getConstructor(AlertRecordMapper.class, InstanceMapper.class, MetricDataMapper.class, TaskMapper.class, String.class);
        MapperManager manager = (MapperManager) constructor.newInstance(Class.forName(config.getAlertMapper()).newInstance(), Class.forName(config.getInstanceMapper()).newInstance(), Class.forName(config.getMetricMapper()).newInstance(), Class.forName(config.getTaskMapper()).newInstance(), config.getProperties());
        manager.init();
        return manager;
    }

}
