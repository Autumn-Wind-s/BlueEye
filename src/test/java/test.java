import com.container.BlueEyeContext;
import com.container.DataCenter;
import com.pojo.instance.InstanceState;
import com.pojo.metric.TagData;

import java.util.*;

/**
 * @Author SDJin
 * @CreationDate 2022/11/7 20:45
 * @Description ：
 */
public class test {
    String a;
    public static void main(String[] args) throws Exception {
     EnumUtil.addEnum(InstanceState.class,new Class[]{String.class},new String[]{"Te"});
        for (InstanceState value : InstanceState.values()) {
            System.out.println(value.name());
        }
        TreeSet<int[]> set=new TreeSet<>(new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return 0;
            }
        });
        StringBuffer s=new StringBuffer();
        String c=new String();
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

