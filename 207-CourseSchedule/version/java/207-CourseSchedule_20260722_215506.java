// Last updated: 2026. 7. 22. 오후 9:55:06
1import java.util.ArrayDeque;
2import java.util.Deque;
3
4class Solution {
5    public boolean canFinish(int numCourses, int[][] prerequisites) {
6        // 위상 정렬로 풀기
7        // 행렬로 일단 나타내기
8        Set<Integer>[] prelist = new HashSet[numCourses];
9        boolean isFinished[] = new boolean[numCourses];
10        int finishCount = 0;
11        for (int i = 0; i < numCourses; i++) {
12            prelist[i] = new HashSet<>();
13        }
14        for (int[] pre : prerequisites) {
15            prelist[pre[0]].add(pre[1]);
16        }
17        // 반복문으로 수강 확인
18        while (finishCount < numCourses) {
19            Deque<Integer> queue = new ArrayDeque<>();
20            // 수강할 수 있는 과목 찾기
21            for (int i = 0; i < numCourses; i++) {
22                if (isFinished[i] == false && prelist[i].isEmpty()) {
23                    queue.offer(i);
24                }
25            }
26            if (queue.isEmpty()) {
27                return false;
28            }
29            // 수강하기
30            while (!queue.isEmpty()) {
31                int nowCourse = queue.poll();
32                isFinished[nowCourse] = true;
33                finishCount++;
34                for (int i = 0; i < numCourses; i++) {
35                    if (isFinished[i] == false && prelist[i].contains(nowCourse)) {
36                        prelist[i].remove(nowCourse);
37                    }
38                }
39            }
40        }
41        return finishCount == numCourses;
42    }
43}