// Last updated: 2026. 9. 11. 오전 10:47:48
1import java.util.ArrayList;
2import java.util.Arrays;
3import java.util.Comparator;
4import java.util.List;
5import java.util.PriorityQueue;
6
7class Solution {
8
9    public int networkDelayTime(int[][] times, int n, int k) {
10        List<List<Edge>> graph = new ArrayList<>();
11
12        for (int i = 0; i <= n; i++) {
13            graph.add(new ArrayList<>());
14        }
15
16        for (int[] time : times) {
17            int from = time[0];
18            int to = time[1];
19            int cost = time[2];
20
21            graph.get(from).add(new Edge(to, cost));
22        }
23
24        int[] dist = new int[n + 1];
25        Arrays.fill(dist, Integer.MAX_VALUE);
26        dist[k] = 0;
27
28        PriorityQueue<Node> pq = new PriorityQueue<>(
29            Comparator.comparingInt(node -> node.cost)
30        );
31
32        pq.offer(new Node(k, 0));
33
34        while (!pq.isEmpty()) {
35            Node current = pq.poll();
36
37            if (current.cost > dist[current.node]) {
38                continue;
39            }
40
41            for (Edge edge : graph.get(current.node)) {
42                int next = edge.to;
43                int nextCost = current.cost + edge.cost;
44
45                if (nextCost < dist[next]) {
46                    dist[next] = nextCost;
47                    pq.offer(new Node(next, nextCost));
48                }
49            }
50        }
51
52        int answer = 0;
53
54        for (int node = 1; node <= n; node++) {
55            if (dist[node] == Integer.MAX_VALUE) {
56                return -1;
57            }
58
59            answer = Math.max(answer, dist[node]);
60        }
61
62        return answer;
63    }
64
65    private static class Edge {
66        private final int to;
67        private final int cost;
68
69        private Edge(int to, int cost) {
70            this.to = to;
71            this.cost = cost;
72        }
73    }
74
75    private static class Node {
76        private final int node;
77        private final int cost;
78
79        private Node(int node, int cost) {
80            this.node = node;
81            this.cost = cost;
82        }
83    }
84}