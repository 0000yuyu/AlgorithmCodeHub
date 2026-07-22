// Last updated: 2026. 7. 22. 오후 10:14:57
1import java.util.ArrayDeque;
2import java.util.ArrayList;
3import java.util.Deque;
4import java.util.List;
5
6class Solution {
7    public boolean canFinish(int numCourses, int[][] prerequisites) {
8        List<Integer>[] graph = new ArrayList[numCourses];
9        int[] indegree = new int[numCourses];
10
11        for (int i = 0; i < numCourses; i++) {
12            graph[i] = new ArrayList<>();
13        }
14
15        for (int[] prerequisite : prerequisites) {
16            int course = prerequisite[0];
17            int previousCourse = prerequisite[1];
18
19            graph[previousCourse].add(course);
20            indegree[course]++;
21        }
22
23        Deque<Integer> queue = new ArrayDeque<>();
24
25        for (int course = 0; course < numCourses; course++) {
26            if (indegree[course] == 0) {
27                queue.offer(course);
28            }
29        }
30
31        int completedCount = 0;
32
33        while (!queue.isEmpty()) {
34            int currentCourse = queue.poll();
35            completedCount++;
36
37            for (int nextCourse : graph[currentCourse]) {
38                indegree[nextCourse]--;
39
40                if (indegree[nextCourse] == 0) {
41                    queue.offer(nextCourse);
42                }
43            }
44        }
45
46        return completedCount == numCourses;
47    }
48}