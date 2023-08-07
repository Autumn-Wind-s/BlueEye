package blueeye.pojo.task.impl.script;


import blueeye.pojo.po.TaskPo;
import blueeye.pojo.task.impl.TimerTask;
import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/2/7 19:22
 * @Description ：不同脚本任务的区别在于执行的脚本，该区别可以用属性体现,execute中逻辑是不变的，可以用一个类确定
 */
@Data
public class ScriptTask extends TimerTask {
    /**
     * 执行的shell命令
     */
    private  String shell;



    @Override
    public String execute(int taskId,int instanceId) throws Exception {
        //用java.lang.Runtime类的exec()方法执行shell脚本
        return null;
    }


}
