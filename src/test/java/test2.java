import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 21:33
 * @Description ：
 */
public class test2 {
    public static void main(String[] args) throws Exception {
        Collections.sort(null, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return 0;
            }
        });
    }

    public static void sort(int[] nums) {
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            // 寻找元素 arr[i] 合适的插入位置
            for (int j = i; j > 0; j--) {
                if (nums[j] < nums[j - 1]) {
                    int temp=nums[j];
                    nums[j]=nums[j-1];
                    nums[j-1]=temp;
                } else {
                    break;
                }
            }
        }
    }

}
