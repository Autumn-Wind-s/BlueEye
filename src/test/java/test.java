import com.container.BlueEyeContext;
import com.container.DataCenter;
import com.pojo.metric.TagData;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @Author SDJin
 * @CreationDate 2022/11/7 20:45
 * @Description ：
 */
public class test {
    String a;
    public static void main(String[] args) throws IOException {
        TagData integerTagData = DataCenter.getTagMetric(String.class, "12");
        System.out.println(integerTagData.getData().getClass());
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        Stack<Object> objects = new Stack<>();
        Deque<Integer> stack=new ArrayDeque<>();

    }

    public String run() {
        LinkedList<Integer> list=new LinkedList<>();
        try {
            if (1 / 0 == 0) {

            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return "123";
        }


    }
}

