import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Solution {
    public int solution(int[] elements) {
        List<Integer> sequence = new ArrayList<>();
        int totalSum = 0;

        for (int element : elements) {
            sequence.add(element);
            totalSum += element;
        }

        Set<Integer> sums = new HashSet<>();
        sums.add(totalSum);

        for (int i = 0; i < sequence.size(); i++) {
            if (i != 0) {
                int first = sequence.remove(0);
                sequence.add(first);
            }

            int total = 0;
            int idx = 0;

            while (idx != sequence.size() - 1) {
                total += sequence.get(idx);
                sums.add(total);
                idx++;
            }
        }

        return sums.size();
    }
}