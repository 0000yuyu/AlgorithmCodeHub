# 문제 풀이 회고

## 1. 문제 정보

- 문제: [55. Jump Game](https://leetcode.com/problems/jump-game/description/)
- 플랫폼 / 난이도: LeetCode / Medium
- 풀이 날짜: 2026/08/11
- 목표 시간: 40분
- 실제 시간: 미기록
- 결과: 해설 참고 / 그리디 풀이 학습

---

## 2. 첫 접근

### 풀이 전 상태

직접 작성한 최초 코드와 실제 풀이 시간은 기록되지 않았다. 가능한 이동 경로를 모두 탐색하는 방식부터 정답에 필요한 정보만 남기는 그리디 방식까지 비교한다.

### 완전탐색으로 생각하면

현재 인덱스의 값이 `nums[index]`라면 다음 위치를 모두 시도할 수 있다.

```text
index + 1
index + 2
...
index + nums[index]
```

각 위치에서 다시 가능한 모든 점프를 시도하면 마지막 인덱스에 도착할 수 있는지 판단할 수 있다.

<details>
<summary>완전탐색 코드</summary>

```java
class Solution {

    public boolean canJump(int[] nums) {
        return search(0, nums);
    }

    private boolean search(int index, int[] nums) {
        if (index >= nums.length - 1) {
            return true;
        }

        int farthest = Math.min(
            nums.length - 1,
            index + nums[index]
        );

        for (int next = index + 1; next <= farthest; next++) {
            if (search(next, nums)) {
                return true;
            }
        }

        return false;
    }
}
```

</details>

### 완전탐색의 문제

같은 인덱스에 여러 경로로 도착하면서 이후의 결과를 반복해서 계산한다.

```text
0번에서 1번으로 이동한 뒤 3번 도착
0번에서 2번으로 이동한 뒤 3번 도착
→ 3번 이후의 이동 가능성을 반복 계산
```

최악의 경우 탐색할 경로가 매우 많아 시간 초과가 발생할 수 있다.

---

## 3. 새로 배운 내용

### 핵심 개념

- 도달 가능한 범위
- 그리디
- 경로가 아닌 최선의 상태만 저장
- 불필요한 분기 제거

### 1. 실제 점프 경로는 중요하지 않다

이 문제는 어떤 순서로 점프했는지를 반환하지 않는다. 마지막 인덱스에 도달할 수 있는지만 확인하면 된다.

따라서 각 위치에서 다음 점프 위치를 하나씩 선택할 필요가 없다. 지금까지 확인한 위치들로부터 도달할 수 있는 가장 먼 인덱스만 관리하면 된다.

```text
maxReach = 지금까지 도달할 수 있는 가장 먼 인덱스
```

### 2. 현재 위치에 도달할 수 있는지 먼저 확인한다

현재 인덱스가 `maxReach`보다 크다면 현재 위치에 도달할 방법이 없다는 뜻이다.

```text
i > maxReach
→ i번 위치까지 올 수 없음
→ 이후 위치에도 갈 수 없음
→ false
```

### 3. 도달 가능한 범위를 확장한다

현재 위치 `i`에 도달할 수 있다면, 여기에서 새로 갈 수 있는 가장 먼 위치는 다음과 같다.

```text
i + nums[i]
```

기존 범위와 비교해 더 먼 값을 저장한다.

```java
maxReach = Math.max(maxReach, i + nums[i]);
```

### 4. 왜 그리디인가

각 위치에서 모든 점프 경로를 보존하지 않고, 미래에 가장 유리한 정보인 최대 도달 범위만 선택해 남긴다.

더 짧게 도달한 경로는 더 멀리 도달한 경로에 포함되므로 따로 기억할 필요가 없다.

```text
어떤 경로 A가 3번까지 도달 가능
어떤 경로 B가 5번까지 도달 가능

→ 0번부터 3번까지의 도달 가능성은 B의 범위에 포함됨
→ 최대 범위 5만 저장
```

### 5. 동작 예시

```text
nums = [2, 3, 1, 1, 4]
```

```text
i = 0
maxReach = max(0, 0 + 2) = 2

i = 1
1 <= 2이므로 도달 가능
maxReach = max(2, 1 + 3) = 4

마지막 인덱스 4까지 도달 가능
→ true
```

도달할 수 없는 예시는 다음과 같다.

```text
nums = [3, 2, 1, 0, 4]
```

```text
i = 0 → maxReach = 3
i = 1 → maxReach = 3
i = 2 → maxReach = 3
i = 3 → maxReach = 3
i = 4 → 4 > maxReach
```

4번 인덱스에 도달할 수 없으므로 `false`이다.

---

## 4. 개선

### 1차 개선: Top-Down 메모이제이션

각 인덱스에서 마지막 위치까지 도달할 수 있는지를 저장하면 동일한 상태의 중복 계산을 제거할 수 있다.

<details>
<summary>메모이제이션 코드</summary>

```java
class Solution {

    private Boolean[] memo;

    public boolean canJump(int[] nums) {
        memo = new Boolean[nums.length];
        return search(0, nums);
    }

    private boolean search(int index, int[] nums) {
        if (index >= nums.length - 1) {
            return true;
        }

        if (memo[index] != null) {
            return memo[index];
        }

        int farthest = Math.min(
            nums.length - 1,
            index + nums[index]
        );

        for (int next = farthest; next > index; next--) {
            if (search(next, nums)) {
                memo[index] = true;
                return true;
            }
        }

        memo[index] = false;
        return false;
    }
}
```

</details>

#### 개선 결과

- 동일한 인덱스의 결과를 재사용한다.
- 완전탐색보다 중복 계산은 줄지만, 각 인덱스에서 여러 다음 위치를 확인하므로 최악의 경우 시간복잡도는 `O(n²)`이다.
- 메모 배열과 재귀 스택 때문에 공간복잡도는 `O(n)`이다.

### 2차 개선: 그리디

실제 경로와 각 인덱스의 성공 여부를 모두 저장하지 않고 최대 도달 범위 하나만 관리한다.

<details open>
<summary>최종 그리디 코드</summary>

```java
class Solution {

    public boolean canJump(int[] nums) {
        int maxReach = 0;

        for (int i = 0; i < nums.length; i++) {
            if (i > maxReach) {
                return false;
            }

            maxReach = Math.max(
                maxReach,
                i + nums[i]
            );

            if (maxReach >= nums.length - 1) {
                return true;
            }
        }

        return true;
    }
}
```

</details>

#### 풀이 과정

1. `maxReach`를 0으로 초기화한다.
2. 왼쪽부터 각 인덱스를 확인한다.
3. `i > maxReach`이면 현재 위치에 도달할 수 없으므로 `false`를 반환한다.
4. 현재 위치에 도달할 수 있다면 `i + nums[i]`로 최대 도달 범위를 확장한다.
5. `maxReach`가 마지막 인덱스 이상이면 `true`를 반환한다.

#### 복잡도

```text
시간복잡도: O(n)
공간복잡도: O(1)
```

### 최종 정리

```text
완전탐색:
각 위치에서 가능한 모든 점프를 시도
→ 경로 수가 많아짐

메모이제이션:
각 인덱스의 도달 가능 여부를 저장
→ 중복 계산은 제거하지만 여러 다음 위치를 확인

그리디:
지금까지 도달 가능한 가장 먼 위치만 저장
→ O(n) 시간, O(1) 공간
```

### 이번 문제에서 기억할 점

```text
경로 자체를 요구하지 않는 문제라면
모든 선택을 저장해야 하는지 먼저 확인한다.

여러 경로 중 더 멀리 도달한 경로가
더 짧게 도달한 경로의 가능성을 전부 포함한다면
최대 도달 범위 하나만 유지할 수 있다.
```

Jump Game의 핵심은 다음 한 줄이다.

```text
현재 인덱스가 최대 도달 범위 안에 있다면,
현재 위치를 이용해 그 범위를 더 멀리 확장한다.
```
