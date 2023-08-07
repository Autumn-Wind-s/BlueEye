import blueeye.rdb.KryoUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author SDJin
 * @CreationDate 2023/7/18 16:46
 * @Description ：
 */
public class test {
    public static void main(String[] args) throws Exception {

//        ExecuteCallback executeCallback = new ExecuteCallback() {
//            @Override
//            public String execute() throws Exception {
//                System.out.println("hh");
//                return "ss";
//            }
//        };
//        System.out.println(executeCallback);
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        String s1 = KryoUtil.writeToString(list);
        String s2 = KryoUtil.writeToString(list);
        System.out.println(s1);
        System.out.println(s2);
        String s3=s1+"\n"+s2;
        String[] split = s3.split("\n");
        System.out.println(split.length);
        System.out.println(s3);
//        System.out.println(s);
//        ExecuteCallback o = KryoUtil.readFromString(s);
//        o.execute();
//        System.out.println(o);
//        ArrayList<Integer> list=new ArrayList<>();
//        list.add(1);
//        list.add(2);
//        String result = list.stream().map(String::valueOf).collect(Collectors.joining(","));
//        System.out.println(result);
        String s="1,2,3";
        List<Integer> collect = Stream.of(s.split(",")).map(Integer::valueOf).collect(Collectors.toList());
        for (Integer integer : collect) {
            System.out.println(integer);
        }
        ThreadPoolExecutor a= (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    }
}
