package blueeye.persistent.defaultImpl;


import blueeye.persistent.TaskMapper;
import blueeye.pojo.po.TaskPo;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:01
 * @Description ：
 */
public class JdbcTaskMapper extends TaskMapper {

    private JdbcTemplate template;

    public JdbcTemplate getTemplate() {
        return template;
    }

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public boolean persistence(TaskPo taskPo) {
        String sql = null;
        int i;
        try {
            sql = "insert into Task(taskId,taskName,taskDescription,createTime,type,cycle,preTask,order,json) values(?,?,?,?,?,?,?,?,?)";
            i = template.update(sql, taskPo.getTaskId(), taskPo.getTaskName(), taskPo.getTaskDescription(), taskPo.getCreateTime(), taskPo.getType(), taskPo.getCycle(), taskPo.getPreTask(), taskPo.getOrder(), taskPo.getJson());
        } catch (Exception e) {
            i = 0;
        }
        return i == 0;
    }

    @Override
    public boolean persistence(List<TaskPo> taskPo) {
        for (TaskPo po : taskPo) {
           if (!persistence(po)){
               return false;
           }
        }
        return true;
    }

    @Override
    public List<Integer> selectPageByName(String name, int type, int page, int pageSize) {
        String sql = "select taskId from Task where taskName like ? and type = ? limit ?,? ";
        try {
            return template.query(sql, new BeanPropertyRowMapper<Integer>(Integer.class), "%" + name + "%", type, page * pageSize, pageSize);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Integer> selectPage(int type, int page, int pageSize) {
        String sql = "select taskId from Task where  type = ? limit ?,? ";
        try {
            return template.query(sql, new BeanPropertyRowMapper<Integer>(Integer.class), type, page * pageSize, pageSize);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<TaskPo> selectByIds(int[] ids) {
        String sql = "select * taskId from Task where  taskId  in (?) ";
        try {
            return template.query(sql, new BeanPropertyRowMapper<TaskPo>(TaskPo.class), Arrays.toString(ids));
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public TaskPo selectById(int id) {
        String sql = "select * taskId from Task where  taskId  = ? ";
        try {
            return template.queryForObject(sql, new BeanPropertyRowMapper<TaskPo>(TaskPo.class), id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public TaskPo selectByName(String name) {
        String sql = "select * taskId from Task where  taskName  = ? ";
        try {
            return template.queryForObject(sql, new BeanPropertyRowMapper<TaskPo>(TaskPo.class), name);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean update(TaskPo taskPo) {
        String sql = null;
        int i;
        try {
            sql = "update Task set taskName= ?,taskDescription = ?,createTime = ?,type = ?,cycle = ?,preTask = ?,order = ?,json = ?  where taskId = ? ";
            i = template.update(sql, taskPo.getTaskName(), taskPo.getTaskDescription(), taskPo.getCreateTime(), taskPo.getType(), taskPo.getCycle(), taskPo.getPreTask(), taskPo.getOrder(), taskPo.getJson(), taskPo.getTaskId());
        } catch (Exception e) {
            i = 0;
        }
        return i == 0;
    }

}
