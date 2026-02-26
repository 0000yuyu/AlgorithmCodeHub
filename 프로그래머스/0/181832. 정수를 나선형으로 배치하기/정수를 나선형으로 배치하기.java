import java.util.*;

class Solution {
    public int[][] solution(int n) {
        // 1. 초기화 (False 대신 0으로 채워진 배열 생성)
        int[][] answer = new int[n][n];
        int[] idx = {0, 0};
        
        // idx1, idx2를 구분하기 위한 매핑
        Map<String, Integer> ansIdx = new HashMap<>();
        ansIdx.put("idx1", 0);
        ansIdx.put("idx2", 1);
        
        Map<String, Boolean> boolIdx = new HashMap<>();
        boolIdx.put("idx1", true);
        boolIdx.put("idx2", true);
        
        int cal = 1;
        String key = "idx1";
        String notKey = "idx2";
        int now = 0;
        int count = 0;

        while (true) {
            int idx1 = idx[ansIdx.get("idx1")];
            int idx2 = idx[ansIdx.get("idx2")];
            now++;

            // 종료 조건
            if (count >= (n * 2 - 1)) {
                break;
            }

            try {
                // 범위를 벗어나거나 이미 숫자가 채워진 경우 예외 발생 (raise 역할)
                if (idx1 < 0 || idx1 >= n || idx2 < 0 || idx2 >= n || answer[idx1][idx2] != 0) {
                    throw new Exception();
                }
                
                answer[idx1][idx2] = now;
                idx[ansIdx.get(notKey)] += cal;
                
            } catch (Exception e) {
                // 방향 전환 로직
                boolIdx.put(key, !boolIdx.get(key));
                
                // idx[ans_idx[not_key]] = abs(idx[ans_idx[not_key]]-cal)
                idx[ansIdx.get(notKey)] = Math.abs(idx[ansIdx.get(notKey)] - cal);
                
                // key, not_key 스왑
                String temp = key;
                key = notKey;
                notKey = temp;
                
                if (boolIdx.get(key)) {
                    cal = 1;
                } else {
                    cal = -1;
                }
                
                // idx[ans_idx[not_key]] = abs(idx[ans_idx[not_key]]+cal)
                idx[ansIdx.get(notKey)] = Math.abs(idx[ansIdx.get(notKey)] + cal);
                
                now--;
                count++;
            }
        }
        return answer;
    }
}