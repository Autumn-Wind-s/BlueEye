package blueeye.pojo.task.impl.intf;

import blueeye.pojo.task.impl.TimerTask;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author SDJin
 * @CreationDate 2023/2/7 19:21
 * @Description ：不同的接口任务区别在于调度的接口不同，该区别可以用属性体现，execute中的代码可以确定，可以用一个类确定
 */
@Data
public class InterfaceTask extends TimerTask {
    private String url;
    private String requestMode;
    private List<String> params;
    private Map<String, String> requestHeaders;



    @Override
    public String execute(int taskId,int instanceId) throws Exception {
        //使用HttpClient调用，将调用结果作为执行记录返回
        return null;
    }

}
