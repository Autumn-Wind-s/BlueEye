import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 21:33
 * @Description ：
 */
public class test2 {
    test s;

    public static void main(String[] args) throws Exception {
        int[] ints = {1, 3, 5, 9, 4};
        sort(ints);
        for (int l : ints) {
            System.out.println(l);
        }
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
