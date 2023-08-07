package blueeye.persistent.defaultImpl;

import blueeye.persistent.InstanceMapper;

import blueeye.pojo.po.TaskInstancePo;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/7/24 21:42
 * @Description ：
 */
public class JdbcInstanceMapper extends InstanceMapper {
    private JdbcTemplate template;

    public JdbcTemplate getTemplate() {
        return template;
    }

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public boolean persistence(TaskInstancePo taskInstancePo) {
        String sql = null;
        int i;
        try {
            sql = "insert into TaskInstance(instanceId,taskId,state ,executionTime,finishTime,record) values(?,?,?,?,?,?)";
            i = template.update(sql, taskInstancePo.getInstanceId(), taskInstancePo.getTaskId(), taskInstancePo.getState(), taskInstancePo.getExecutionTime(), taskInstancePo.getFinishTime(),  taskInstancePo.getRecord());
        } catch (Exception e) {
            i = 0;
        }
        return i == 0;
    }

    @Override
    public boolean persistence(List<TaskInstancePo> taskInstancePo) {
        for (TaskInstancePo instancePo : taskInstancePo) {
            if (!persistence(taskInstancePo)){
                return false;
            }
        }
        return true;
    }

    @Override
    public List<TaskInstancePo> selectFromTo(int start, int to) {
        String sql = "select * from TaskInstance limit ?,?";
        try {
            return template.query(sql, new BeanPropertyRowMapper<TaskInstancePo>(TaskInstancePo.class), start-1, to-start+1);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<TaskInstancePo> selectFromToByTaskID(int taskId, int from, int to) {
        String sql = "select * from TaskInstance where taskId=?  limit ?,?";
        try {
            return template.query(sql, new BeanPropertyRowMapper<TaskInstancePo>(TaskInstancePo.class), taskId, from-1, to-from+1);
        } catch (DataAccessException e) {
            return null;
        }
    }
}
