import java.util.*;

class Solution {
    public int solution(int[][] land) {
        int n = land.length;
        int m = land[0].length;
        
        // 1. 방문 여부를 체크할 2차원 배열 (Set<String> 대체)
        boolean[][] visited = new boolean[n][m];
        
        // 2. 각 열별로 얻을 수 있는 총 석유량을 저장할 배열 (amount_dic 대체)
        int[] columnOil = new int[m];
        
        // 이동 방향 (상, 하, 좌, 우)
        int[] dx = {0, 0, -1, 1};
        int[] dy = {1, -1, 0, 0};

        // 3. 전체 격자 탐색
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                // 석유가 있고 아직 방문하지 않은 경우 (새로운 덩어리 발견)
                if (land[i][j] == 1 && !visited[i][j]) {
                    
                    // BFS/DFS를 통해 덩어리 크기와 포함된 열(Column)들을 찾음
                    int size = 0;
                    // 현재 덩어리가 어떤 열들에 걸쳐 있는지 체크 (중복 방지용)
                    Set<Integer> columnsInCluster = new HashSet<>();
                    Queue<int[]> queue = new ArrayDeque<>();

                    queue.offer(new int[]{i, j});
                    visited[i][j] = true;

                    while (!queue.isEmpty()) {
                        int[] cur = queue.poll();
                        int r = cur[0];
                        int c = cur[1];
                        
                        size++;
                        columnsInCluster.add(c); // 현재 칸의 열 위치 저장

                        for (int k = 0; k < 4; k++) {
                            int nr = r + dx[k];
                            int nc = c + dy[k];

                            if (nr >= 0 && nr < n && nc >= 0 && nc < m 
                                && land[nr][nc] == 1 && !visited[nr][nc]) {
                                visited[nr][nc] = true;
                                queue.offer(new int[]{nr, nc});
                            }
                        }
                    }

                    // 4. 찾은 덩어리의 크기를 해당되는 모든 열에 더해줌
                    for (int col : columnsInCluster) {
                        columnOil[col] += size;
                    }
                }
            }
        }

        // 5. 각 열의 결과 중 최댓값 찾기
        int maxOil = 0;
        for (int total : columnOil) {
            maxOil = Math.max(maxOil, total);
        }

        return maxOil;
    }
}