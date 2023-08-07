package blueeye.persistent.defaultImpl;

import blueeye.persistent.MapperManager;
import blueeye.persistent.MetricDataMapper;
import blueeye.pojo.po.AlertRecordPo;
import blueeye.pojo.po.MetricDataPo;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/7/24 21:44
 * @Description ：
 */
public class JdbcMetricDataMapper extends MetricDataMapper {
    private JdbcTemplate template;

    public JdbcTemplate getTemplate() {
        return template;
    }

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public boolean persistence(MetricDataPo metricDataPo) {
        String sql = null;
        int i;
        try {
            sql = "insert into MetricData(id,taskId,instanceId,createTime,dataType,json) values(?,?,?,?,?,?)";
            i = template.update(sql, metricDataPo.getId(), metricDataPo.getTaskId(), metricDataPo.getInstanceId(), metricDataPo.getCreateTime(), metricDataPo.getDataType(), metricDataPo.getJson());
        } catch (Exception e) {
            i = 0;
        }
        return i == 0;
    }

    @Override
    public boolean persistence(List<MetricDataPo> metricDataPo) {
        for (MetricDataPo dataPo : metricDataPo) {
            if(!persistence(dataPo)){
                return false;
            }
        }
        return true;
    }

    @Override
    public List<MetricDataPo> selectByTimeAndId(int taskId, long startTime, long endTime) {
        String sql = "select * from MetricData where taskId = ? and createTime >= ? and createTime <= ? ";
        try {
            return template.query(sql, new BeanPropertyRowMapper<MetricDataPo>(MetricDataPo.class), taskId, new Timestamp(startTime), new Timestamp(endTime));
        } catch (DataAccessException e) {
            return null;
        }
    }
}
