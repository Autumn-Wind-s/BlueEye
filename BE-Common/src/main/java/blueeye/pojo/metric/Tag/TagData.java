package blueeye.pojo.metric.Tag;


import blueeye.pojo.metric.MetricData;
import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/2/6 21:06
 * @Description ：
 */
@Data
public class TagData<T> extends MetricData {
    T data;

    public TagData(T data) {
        this.data = data;
    }


    public TagData() {

    }
}
