package blueeye.persistent.defaultImpl;

import blueeye.persistent.*;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author SDJin
 * @CreationDate 2023/7/24 21:49
 * @Description ：
 */
public class JdbcMapperManager extends MapperManager {
    private DataSource ds;

    public JdbcMapperManager(AlertRecordMapper alertRecordMapper, InstanceMapper instanceMapper, MetricDataMapper metricDataMapper, TaskMapper taskMapper,String properties) {
        super(alertRecordMapper, instanceMapper, metricDataMapper, taskMapper,properties);
    }


    @Override
    public void init() {
        InputStream is = JdbcMapperManager.class.getClassLoader().getResourceAsStream(properties);
        Properties properties = new Properties();
        try {
            properties.load(is);
            ds = DruidDataSourceFactory.createDataSource(properties);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((JdbcAlertRecordMapper)alertRecordMapper).setTemplate(new JdbcTemplate(ds));
        ((JdbcInstanceMapper)instanceMapper).setTemplate(new JdbcTemplate(ds));
        ((JdbcMetricDataMapper)metricDataMapper).setTemplate(new JdbcTemplate(ds));
        ((JdbcTaskMapper)taskMapper).setTemplate(new JdbcTemplate(ds));
    }




    @Override
    public void destroy() {
        ((DruidDataSource) ds).close();
    }
}
