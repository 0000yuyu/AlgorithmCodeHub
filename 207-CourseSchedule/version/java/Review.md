# 문제 풀이 회고

## 1. 문제 정보

- 문제: [207. Course Schedule](https://leetcode.com/problems/course-schedule/description/)
- 플랫폼 / 난이도: LeetCode / Medium
- 풀이 날짜: 2026/07/22
- 목표 시간: 45분
- 실제 시간: 약 51분
- 결과: 성공

---

## 2. 첫 접근

### 처음 생각한 풀이

- 떠올린 개념: 위상 정렬
- 그렇게 생각한 이유: 선수 과목이 없는 과목부터 차례대로 수강 처리하고, 아직 과목이 남았는데 더 이상 수강할 수 있는 과목이 없다면 순환 관계라고 판단할 수 있다고 생각했다.
- 예상 시간복잡도: O(V² + E)

    <details>
    <summary>처음 작성한 코드</summary>
    
    ```java
    import java.util.ArrayDeque;
    import java.util.Deque;
    import java.util.HashSet;
    import java.util.Set;
    
    class Solution {
        public boolean canFinish(int numCourses, int[][] prerequisites) {
            Set<Integer>[] prelist = new HashSet[numCourses];
            boolean[] isFinished = new boolean[numCourses];
            int finishCount = 0;
    
            for (int i = 0; i < numCourses; i++) {
                prelist[i] = new HashSet<>();
            }
    
            for (int[] pre : prerequisites) {
                prelist[pre[0]].add(pre[1]);
            }
    
            while (finishCount < numCourses) {
                Deque<Integer> queue = new ArrayDeque<>();
    
                for (int i = 0; i < numCourses; i++) {
                    if (!isFinished[i] && prelist[i].isEmpty()) {
                        queue.offer(i);
                    }
                }
    
                if (queue.isEmpty()) {
                    return false;
                }
    
                while (!queue.isEmpty()) {
                    int nowCourse = queue.poll();
                    isFinished[nowCourse] = true;
                    finishCount++;
    
                    for (int i = 0; i < numCourses; i++) {
                        if (!isFinished[i] && prelist[i].contains(nowCourse)) {
                            prelist[i].remove(nowCourse);
                        }
                    }
                }
            }
    
            return finishCount == numCourses;
        }
    }
    ```
    
    </details>

- 통과한 테스트: 전체 테스트 통과
- 실패한 테스트 또는 반례: 없음
- 놓친 조건: 없음
- 개선이 필요한 부분:
  - 선수 과목이 없는 과목을 찾기 위해 매 단계마다 전체 과목을 탐색한다.
  - 과목 하나를 수강할 때마다 해당 과목을 선수 과목으로 가지는 과목을 찾기 위해 다시 전체 과목을 탐색한다.
  - 정답은 통과하지만 최악의 경우 시간복잡도가 `O(V² + E)`까지 증가한다.

---

## 3. 새로 배운 내용

### 핵심 개념

- 진입 차수를 이용한 Kahn 위상 정렬
- 인접 리스트를 이용한 다음 과목 관리

### 개념 정리

#### 1. 기존 풀이가 동작하는 이유

선수 과목이 없는 과목은 바로 수강할 수 있다. 해당 과목을 수강한 뒤 다른 과목의 선수 목록에서 제거하는 과정을 반복하면 선수 관계를 만족하는 순서대로 과목을 처리할 수 있다.

아직 수강하지 않은 과목이 남았는데 선수 과목이 없는 과목을 하나도 찾을 수 없다면, 남은 과목 사이에 순환 관계가 존재한다는 의미이므로 모든 과목을 수강할 수 없다.

#### 2. 진입 차수란?

진입 차수는 특정 과목을 수강하기 전에 먼저 들어야 하는 선수 과목의 개수다.

```text
prerequisites[i] = [course, prerequisite]

prerequisite → course
```

각 과목의 진입 차수가 0이라면 현재 바로 수강할 수 있다.

#### 3. 왜 인접 리스트를 사용하는가?

현재 풀이에서는 한 과목을 수강할 때마다 모든 과목을 확인한다. 하지만 인접 리스트에 현재 과목을 선수 과목으로 가지는 다음 과목만 저장하면, 실제로 연결된 과목의 진입 차수만 감소시킬 수 있다.

```text
현재 과목 → 현재 과목을 선수 과목으로 가지는 다음 과목들
```

각 정점과 간선을 필요한 만큼만 방문하므로 시간복잡도를 `O(V + E)`로 줄일 수 있다.

#### 4. 사이클은 어떻게 판별하는가?

진입 차수가 0인 과목부터 큐에 넣고 처리한 과목 수를 센다.

- 처리한 과목 수가 `numCourses`와 같으면 모든 과목을 수강할 수 있다.
- 큐가 비었는데 처리한 과목 수가 부족하면 남은 과목 사이에 사이클이 존재한다.

---

## 4. 개선

### 풀이 과정

1. 선수 과목에서 다음 과목으로 향하는 인접 리스트를 만든다.
2. 각 과목의 진입 차수를 계산한다.
3. 진입 차수가 0인 과목을 큐에 넣는다.
4. 큐에서 과목을 꺼내 수강 처리한다.
5. 연결된 다음 과목의 진입 차수를 1씩 감소시킨다.
6. 진입 차수가 0이 된 과목을 큐에 추가한다.
7. 처리한 과목 수가 전체 과목 수와 같은지 확인한다.

### 코드

```java
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        List<Integer>[] graph = new ArrayList[numCourses];
        int[] indegree = new int[numCourses];

        for (int i = 0; i < numCourses; i++) {
            graph[i] = new ArrayList<>();
        }

        for (int[] prerequisite : prerequisites) {
            int course = prerequisite[0];
            int previousCourse = prerequisite[1];

            graph[previousCourse].add(course);
            indegree[course]++;
        }

        Deque<Integer> queue = new ArrayDeque<>();

        for (int course = 0; course < numCourses; course++) {
            if (indegree[course] == 0) {
                queue.offer(course);
            }
        }

        int completedCount = 0;

        while (!queue.isEmpty()) {
            int currentCourse = queue.poll();
            completedCount++;

            for (int nextCourse : graph[currentCourse]) {
                indegree[nextCourse]--;

                if (indegree[nextCourse] == 0) {
                    queue.offer(nextCourse);
                }
            }
        }

        return completedCount == numCourses;
    }
}
```
