import java.util.*;

class Solution {

    public int solution(String[] want, int[] number, String[] discount) {

        Map<String, Integer> target = new HashMap<>();

        for (int i = 0; i < want.length; i++) {
            target.put(want[i], number[i]);
        }

        Map<String, Integer> window = new HashMap<>();

        // 최초 10일
        for (int i = 0; i < 10; i++) {
            window.put(
                discount[i],
                window.getOrDefault(discount[i], 0) + 1
            );
        }

        int answer = 0;

        if (isValid(target, window)) {
            answer++;
        }

        // 슬라이딩
        for (int right = 10; right < discount.length; right++) {

            int left = right - 10;

            // 왼쪽 상품 제거
            String remove = discount[left];

            window.put(remove, window.get(remove) - 1);

            if (window.get(remove) == 0) {
                window.remove(remove);
            }

            // 오른쪽 상품 추가
            String add = discount[right];

            window.put(
                add,
                window.getOrDefault(add, 0) + 1
            );

            if (isValid(target, window)) {
                answer++;
            }
        }

        return answer;
    }

    private boolean isValid(
            Map<String, Integer> target,
            Map<String, Integer> window) {

        for (String product : target.keySet()) {

            if (window.getOrDefault(product, 0)
                    != target.get(product)) {

                return false;
            }
        }

        return true;
    }
}