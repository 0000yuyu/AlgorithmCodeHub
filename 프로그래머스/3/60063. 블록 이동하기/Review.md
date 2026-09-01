# 블록 이동하기 Review

- 플랫폼: 프로그래머스
- 문제 번호: 60063
- 난이도: Level 3
- 언어: Java
- 핵심: 두 칸짜리 로봇의 상태 모델링, 이동·회전 후보 생성, BFS
- 문제 링크: https://school.programmers.co.kr/learn/courses/30/lessons/60063

## 1. 문제에서 구해야 하는 값

로봇은 `(0, 0)`과 `(0, 1)`에서 출발한다. 상·하·좌·우 이동과 90도 회전은 모두 1초가 걸리며, 로봇의 두 칸 중 하나가 `(n - 1, n - 1)`에 도착할 때까지 필요한 최소 시간을 구한다.

일반적인 미로 탐색처럼 현재 좌표 하나만 저장하면 로봇이 차지한 나머지 칸과 방향을 알 수 없다. 이 문제의 첫 단계는 BFS를 작성하는 것이 아니라 로봇 하나를 정확히 표현할 상태를 정하는 것이다.

## 2. 상태 정의

이 문제에서 한 칸의 좌표만으로는 로봇의 상태를 표현할 수 없다. 로봇은 항상 인접한 두 칸을 차지하므로 상태를 두 좌표의 쌍으로 둔다.

```text
State = ((r1, c1), (r2, c2))
```

같은 로봇을 `((0,0),(0,1))`과 `((0,1),(0,0))`처럼 두 번 방문하지 않도록 좌표 순서를 항상 정규화한다. 두 좌표 중 사전순으로 작은 좌표를 앞에 두면 된다.

## 3. BFS를 사용하는 이유

모든 동작의 비용은 정확히 1이다.

- 상·하·좌·우 평행 이동: 1
- 한쪽 칸을 축으로 90도 회전: 1

따라서 상태를 정점, 가능한 한 번의 동작을 간선으로 보면 가중치가 모두 같은 그래프가 된다. BFS에서 목표 상태를 처음 만난 거리가 최소 시간이 된다.

목표 판정은 두 칸 중 하나가 `(n - 1, n - 1)`에 도착했는지 확인한다.

큐에서는 `State`와 해당 상태까지의 이동 횟수를 함께 관리한다. 시작 상태의 거리는 0이고, 현재 상태에서 만든 다음 상태는 `현재 거리 + 1`로 큐에 넣는다. BFS는 이동 횟수가 작은 상태부터 처리하므로 목표 상태를 처음 꺼낸 시점의 거리가 최소 이동 횟수다.

```text
0초: 시작 상태
1초: 시작 상태에서 한 번 움직여 도달한 상태
2초: 두 번 움직여 도달한 상태
...
```

## 4. 다음 상태 만들기

### 4.1 평행 이동

네 방향에 대해 두 칸이 모두 보드 안이고 빈칸이면 함께 이동한다.

```text
(r1, c1), (r2, c2)
→ (r1 + dr, c1 + dc), (r2 + dr, c2 + dc)
```

### 4.2 가로 상태의 회전

로봇이 `(r, c1)`, `(r, c2)`에 가로로 놓였다고 하자. 위쪽 또는 아래쪽의 두 칸이 모두 비어 있어야 회전할 수 있다.

```text
검사: board[r + d][c1] == 0 && board[r + d][c2] == 0
d = -1 또는 1
```

조건을 만족하면 왼쪽 칸과 오른쪽 칸을 각각 축으로 하는 두 상태가 생긴다.

```text
왼쪽 축 : (r, c1), (r + d, c1)
오른쪽 축: (r, c2), (r + d, c2)
```

### 4.3 세로 상태의 회전

로봇이 `(r1, c)`, `(r2, c)`에 세로로 놓였다면 왼쪽 또는 오른쪽의 두 칸을 검사한다.

```text
검사: board[r1][c + d] == 0 && board[r2][c + d] == 0
d = -1 또는 1
```

조건을 만족하면 위쪽 칸과 아래쪽 칸을 각각 축으로 하는 두 상태가 생긴다.

```text
위쪽 축 : (r1, c), (r1, c + d)
아래쪽 축: (r2, c), (r2, c + d)
```

## 5. 방문 처리

`visited`에는 정규화한 `State`를 저장한다. 다음 상태를 만들 때 `visited.add(next)`가 `true`인 경우에만 큐에 넣으면 방문 확인과 추가를 한 번에 처리할 수 있다.

방문 처리가 필요한 이유는 두 가지다.

- 오른쪽으로 이동했다가 다시 왼쪽으로 오는 경로처럼 같은 상태가 반복될 수 있다.
- 두 끝점의 순서만 반대인 상태가 별개의 객체로 만들어질 수 있다.

첫 번째 문제는 `visited`, 두 번째 문제는 좌표 정규화로 해결한다.

## 6. 구현에서 자주 틀리는 부분

1. 회전축 옆 한 칸만 검사한다.
   - 회전하는 동안 지나가는 2×2 영역의 두 칸이 모두 비어야 한다.
2. 두 끝점의 순서를 구분한다.
   - 좌표를 정규화하지 않으면 같은 상태가 반대 순서로 중복 방문된다.
3. 방향만 방문 배열에 저장한다.
   - 위치까지 포함해야 하므로 두 좌표 전체 또는 `(기준 좌표, 방향)`을 저장해야 한다.
4. 목표 칸에 두 칸이 모두 들어와야 한다고 생각한다.
   - 문제 조건은 로봇의 어느 한 칸이라도 목표에 도착하면 끝이다.
5. DFS를 사용한다.
   - 최소 동작 수가 필요하므로 별도의 최솟값 갱신보다 BFS가 자연스럽다.

## 7. 최종 Java 코드

```java
import java.util.*;

class Solution {
    private int n;
    private int[][] board;

    public int solution(int[][] board) {
        this.board = board;
        this.n = board.length;

        Queue<Node> queue = new ArrayDeque<>();
        Set<State> visited = new HashSet<>();

        State start = State.of(0, 0, 0, 1);
        queue.offer(new Node(start, 0));
        visited.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            State state = current.state;

            if (isGoal(state)) {
                return current.distance;
            }

            for (State next : getNextStates(state)) {
                if (visited.add(next)) {
                    queue.offer(new Node(next, current.distance + 1));
                }
            }
        }

        return -1;
    }

    private boolean isGoal(State state) {
        return (state.r1 == n - 1 && state.c1 == n - 1)
            || (state.r2 == n - 1 && state.c2 == n - 1);
    }

    private List<State> getNextStates(State s) {
        List<State> result = new ArrayList<>();
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        // 상·하·좌·우 이동
        for (int i = 0; i < 4; i++) {
            int nr1 = s.r1 + dr[i];
            int nc1 = s.c1 + dc[i];
            int nr2 = s.r2 + dr[i];
            int nc2 = s.c2 + dc[i];

            if (isEmpty(nr1, nc1) && isEmpty(nr2, nc2)) {
                result.add(State.of(nr1, nc1, nr2, nc2));
            }
        }

        if (s.r1 == s.r2) { // 가로
            for (int d : new int[]{-1, 1}) {
                int nr = s.r1 + d;
                if (isEmpty(nr, s.c1) && isEmpty(nr, s.c2)) {
                    result.add(State.of(s.r1, s.c1, nr, s.c1));
                    result.add(State.of(s.r2, s.c2, nr, s.c2));
                }
            }
        } else { // 세로
            for (int d : new int[]{-1, 1}) {
                int nc = s.c1 + d;
                if (isEmpty(s.r1, nc) && isEmpty(s.r2, nc)) {
                    result.add(State.of(s.r1, s.c1, s.r1, nc));
                    result.add(State.of(s.r2, s.c2, s.r2, nc));
                }
            }
        }

        return result;
    }

    private boolean isEmpty(int r, int c) {
        return 0 <= r && r < n && 0 <= c && c < n && board[r][c] == 0;
    }

    private static class Node {
        final State state;
        final int distance;

        Node(State state, int distance) {
            this.state = state;
            this.distance = distance;
        }
    }

    private static class State {
        final int r1, c1, r2, c2;

        private State(int r1, int c1, int r2, int c2) {
            this.r1 = r1;
            this.c1 = c1;
            this.r2 = r2;
            this.c2 = c2;
        }

        static State of(int r1, int c1, int r2, int c2) {
            if (r1 > r2 || (r1 == r2 && c1 > c2)) {
                return new State(r2, c2, r1, c1);
            }
            return new State(r1, c1, r2, c2);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof State)) return false;
            State other = (State) obj;
            return r1 == other.r1 && c1 == other.c1
                && r2 == other.r2 && c2 == other.c2;
        }

        @Override
        public int hashCode() {
            return Objects.hash(r1, c1, r2, c2);
        }
    }
}
```

## 8. 복잡도

보드의 각 칸과 방향 조합에서 만들 수 있는 로봇 상태 수는 `O(n²)`이다. 각 상태에서 확인하는 다음 동작 수는 상수이므로:

- 시간복잡도: `O(n²)`
- 공간복잡도: `O(n²)`

## 9. 재풀이 체크리스트

- [ ] 한 상태를 두 좌표로 정의했는가?
- [ ] 두 좌표의 순서를 정규화했는가?
- [ ] 평행 이동 시 두 칸을 모두 검사했는가?
- [ ] 회전 시 2×2 공간의 두 칸을 모두 검사했는가?
- [ ] 한쪽 끝점이 목표에 도착하면 종료하는가?
- [ ] 방문 처리 시 거리와 무관한 상태만 키로 사용하는가?
- [ ] BFS 큐에 거리를 함께 넣거나 레벨 단위로 처리하는가?

풀이 흐름은 `두 좌표로 상태 정의 → 이동·회전 후보 생성 → 중복 상태 제거 → BFS` 순서로 정리할 수 있다.
