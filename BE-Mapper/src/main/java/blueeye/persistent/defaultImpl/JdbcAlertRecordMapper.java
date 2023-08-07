package blueeye.persistent.defaultImpl;

import blueeye.persistent.AlertRecordMapper;
import blueeye.persistent.MapperManager;
import blueeye.pojo.po.AlertRecordPo;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/7/24 21:42
 * @Description ：
 */
public class JdbcAlertRecordMapper extends AlertRecordMapper {
    private JdbcTemplate template;

    public JdbcTemplate getTemplate() {
        return template;
    }

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public boolean persistence(AlertRecordPo alertRecordPo) {
        String sql = null;
        int i ;
        try {
           sql= "insert into AlertRecord(recordId,taskId,alertTime,content,notifier,alertMethod) values(?,?,?,?,?,?)";
            i=template.update(sql,alertRecordPo.getRecordId(),alertRecordPo.getTaskId(),alertRecordPo.getAlertTime(),alertRecordPo.getContent(),alertRecordPo.getNotifier(),alertRecordPo.getAlertMethod());
        } catch (Exception e){
            i=0;
        }
        return i==0;
    }

    @Override
    public boolean persistence(List<AlertRecordPo> alertRecordPo) {
        for (AlertRecordPo recordPo : alertRecordPo) {
            if(!persistence(recordPo)){
                return false;
            }
        }
        return true;
    }

    @Override
    public List<AlertRecordPo> selectFromToByTime(long startTime, long endTime, int from, int to) {
        String sql = "select * from AlertRecord where alertTime>=? and alertTime<= ? limit ?,?";
        try {
            return template.query(sql, new BeanPropertyRowMapper<AlertRecordPo>(AlertRecordPo.class), new Timestamp(startTime), new Timestamp(endTime), from-1,to-from+1);
        } catch (DataAccessException e) {
            return null;
        }
    }
}
