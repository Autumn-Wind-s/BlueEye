package blueeye.pojo.alert;


import blueeye.pojo.task.impl.alert.AlertTask;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/2/4 14:01
 * @Description ：
 */
@Data
public class AlertRule {
    private Condition condition;
    private List<Object> values;
    private Timestamp recentAlertTime;
    private Long intervalTime;
    private Integer currLevel;
    private Long switchTime;
    private List<AlertAction> actions;

    public Boolean judge(Object data) {
        if (data instanceof Integer || data instanceof Long || data instanceof Short) {
            if (condition.equals(Condition.Max)) {
                if ((Long) data > (Long) values.get(0)) {
                    return true;
                }
            } else if (condition.equals(Condition.Min)) {
                if ((Long) data < (Long) values.get(0)) {
                    return true;
                }
            } else if (condition.equals(Condition.Range)) {
                if ((Long) data < (Long) values.get(0) || (Long) data > (Long) values.get(1)) {
                    return true;
                }
            } else if (condition.equals(Condition.match)) {
                if (((Long) data).equals((Long) values.get(0))) {
                    return true;
                }
            }
            return false;
        } else if (data instanceof Float || data instanceof Double) {
            if (condition.equals(Condition.Max)) {
                if ((Double) data > (Double) values.get(0)) {
                    return true;
                }
            } else if (condition.equals(Condition.Min)) {
                if ((Double) data < (Double) values.get(0)) {
                    return true;
                }
            } else if (condition.equals(Condition.Range)) {
                if ((Double) data < (Double) values.get(0) || (Double) data > (Double) values.get(1)) {
                    return true;
                }
            } else if (condition.equals(Condition.match)) {
                if (((Double) data).equals((Double) values.get(0))) {
                    return true;
                }
            }
            return false;
        } else {
            return data.equals(values.get(0));
        }
    }

    public AlertTask getAlertTask(Object data) {
        long interval = System.currentTimeMillis() - recentAlertTime.getTime();
        if (interval < intervalTime) {
            //报警频率过高，无需判断
            return null;
        }
        if (!judge(data)) {
            //数据正常，不做报警
            return null;
        }
        if(interval<=switchTime){
            //上次报警未起效，升级报警级别
            upgrade();
        }else{
            //上次报警起效，降低报警级别
            degrade();
        }
        AlertAction alertAction = actions.get(currLevel);
        AlertTask task = new AlertTask();
        task.setContent(alertAction.getContent());
        task.setNotifier(alertAction.getNotifier());
        task.setMethod(alertAction.getMethod());
        task.setCreateTime(new Timestamp(System.currentTimeMillis()));
        task.setOrder(currLevel);
        return task;
    }

    public void upgrade(){
        int size=actions.size();
        if(currLevel<size){
            currLevel++;
        }
    }
    public void degrade(){
        if(currLevel>1){
            currLevel--;
        }
    }

}
