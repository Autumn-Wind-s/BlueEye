import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 21:33
 * @Description ：
 */
public class test2 {
    test s;
    public static void main(String[] args) throws Exception {
        test2 test2 = new test2();
        test2.s=new test();
        System.out.println(test2.so());
    }
    public String so(){
        return s.run();
    }
}
