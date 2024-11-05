//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class DiffUtil {

    public static int findSmallestDiff(int[] a) {
        if (a.length < 2) {
            return -1;
        }

        int diff = Math.abs(a[0]-a[1]);

        for (int i = 0; i < a.length; i++) {
            for (int j = i + 1; j < a.length; j++) {
                int tmp_diff = Math.abs(a[i]-a[j]) ;
                if (tmp_diff < Math.abs(diff))
                    diff = tmp_diff;
            }
        }

        return diff;
    }
}
