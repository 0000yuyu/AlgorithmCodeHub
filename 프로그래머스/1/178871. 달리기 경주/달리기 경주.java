import java.util.*;

class Solution {
    public String[] solution(String[] players, String[] callings) {
        // 1. 선수의 이름으로 현재 등수를 바로 찾기 위한 Map 생성
        Map<String, Integer> rankMap = new HashMap<>();
        for (int i = 0; i < players.length; i++) {
            rankMap.put(players[i], i);
        }

        // 2. 해설진이 이름을 부를 때마다 순서 교체
        for (String calling : callings) {
            // 추월한 선수의 현재 등수
            int currentRank = rankMap.get(calling);
            
            // 바로 앞 선수의 등수와 이름 찾기
            int frontRank = currentRank - 1;
            String frontPlayer = players[frontRank];

            // 3. 실제 순서(배열) 교체 (Swap)
            players[frontRank] = calling;
            players[currentRank] = frontPlayer;

            // 4. Map 정보 갱신 (추월한 사람과 추월당한 사람 모두)
            rankMap.put(calling, frontRank);
            rankMap.put(frontPlayer, currentRank);
        }

        return players;
    }
}