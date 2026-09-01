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