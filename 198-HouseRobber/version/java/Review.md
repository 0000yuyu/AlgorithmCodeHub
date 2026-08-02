# 문제 풀이 회고

## 1. 문제 정보

- 문제: [198. House Robber](https://leetcode.com/problems/house-robber/description/)
- 플랫폼 / 난이도: LeetCode / Medium
- 풀이 날짜: 2026/08/03
- 목표 시간: 35분
- 실제 시간: 미기록
- 결과: 실패 / 짝수·홀수 합 비교 접근 후 완전탐색과 DP 학습

---

## 2. 첫 접근

### 처음 생각한 풀이

- 떠올린 개념: 짝수 인덱스의 합과 홀수 인덱스의 합 비교
- 그렇게 생각한 이유:
  - 인접한 두 집을 동시에 털 수 없으므로 한 칸씩 건너뛰어야 한다고 생각했다.
  - 짝수 번째 집만 터는 경우와 홀수 번째 집만 터는 경우 중 큰 값을 선택하려 했다.
- 예상 시간복잡도: `O(n)`

### 실패한 테스트 또는 반례

```text
nums = [2, 1, 1, 2]
```

짝수 인덱스만 선택하면 다음과 같다.

```text
0번 + 2번 = 2 + 1 = 3
```

홀수 인덱스만 선택하면 다음과 같다.

```text
1번 + 3번 = 1 + 2 = 3
```

하지만 실제 최적 조합은 다음과 같다.

```text
0번 + 3번 = 2 + 2 = 4
```

0번 집과 3번 집은 인접하지 않으므로 함께 선택할 수 있다. 따라서 최적 조합은 반드시 짝수 인덱스 또는 홀수 인덱스로만 구성되지 않는다.

### 놓친 조건

- 현재 집을 털지 않으면 바로 다음 집을 선택할 수도 있다.
- 선택 가능한 집의 조합은 짝수 또는 홀수 인덱스로 고정되지 않는다.
- 각 집에서 `현재 집을 턴다`와 `현재 집을 털지 않는다`를 모두 비교해야 한다.

### 실패 이유

짝수 집과 홀수 집을 나누는 방식은 가능한 조합 중 일부만 검사한다. 이 문제의 선택 조건은 인덱스의 홀짝이 아니라 두 집이 서로 인접했는지 여부이다.

따라서 각 위치에서 다음 두 경우를 모두 확인해야 한다.

```text
1. 현재 집을 턴다.
2. 현재 집을 털지 않는다.
```

---

## 3. 새로 배운 내용

### 핵심 개념

- 선택 기반 완전탐색
- 중복 부분 문제
- 메모이제이션
- Bottom-Up DP
- DP 상태 정의와 점화식

### 개념 정리

#### 1. 완전탐색에서도 `턴다 / 안 턴다`를 비교해야 한다

현재 `index`번 집에서 선택할 수 있는 경우는 두 가지이다.

```text
현재 집을 턴다
→ nums[index]를 얻는다.
→ 인접한 집은 털 수 없으므로 index + 2로 이동한다.

현재 집을 털지 않는다
→ 현재 집의 돈은 얻지 않는다.
→ index + 1로 이동한다.
```

따라서 현재 위치에서 얻을 수 있는 최대 금액은 다음과 같다.

```text
max(
    현재 집의 돈 + 두 칸 뒤에서 얻을 수 있는 최대 금액,
    한 칸 뒤에서 얻을 수 있는 최대 금액
)
```

#### 2. DP는 완전탐색과 별개의 풀이가 아니다

완전탐색에서는 같은 `index`에서 얻을 수 있는 최대 금액을 여러 번 계산한다.

```text
search(0)
├─ 0번 집을 턴다 → search(2)
└─ 0번 집을 안 턴다 → search(1)
                         └─ 1번 집을 안 턴다 → search(2)
```

`search(2)`가 반복된다. 이 결과를 저장해 재사용한 것이 메모이제이션 DP이다.

#### 3. Bottom-Up DP의 상태 정의

다음과 같이 DP 배열을 정의한다.

```text
dp[i] = 0번 집부터 i번 집까지 고려했을 때 훔칠 수 있는 최대 금액
```

`dp[i]`는 반드시 `i`번 집을 털었다는 뜻이 아니다. `i`번 집까지 고려한 모든 유효한 조합 중 가장 큰 금액만 의미한다.

#### 4. 현재 집을 털지 않는 경우

현재 집을 털지 않으면 `i - 1`번 집까지 구한 최댓값을 그대로 사용한다.

```text
dp[i - 1]
```

#### 5. 현재 집을 터는 경우

현재 집을 털면 바로 전 집은 털 수 없다. 따라서 두 칸 전까지의 최댓값에 현재 집의 돈을 더한다.

```text
dp[i - 2] + nums[i]
```

#### 6. 점화식

두 선택 중 더 큰 값을 현재 상태에 저장한다.

```text
dp[i] = max(dp[i - 1], dp[i - 2] + nums[i])
```

```java
dp[i] = Math.max(
    dp[i - 1],
    dp[i - 2] + nums[i]
);
```

#### 7. 이전에 선택한 조합은 고정되지 않는다

다음 예시를 확인한다.

```text
nums = [4, 5, 4, 10]
```

2번 집까지 고려하면 다음 조합이 최적이다.

```text
0번 + 2번 = 4 + 4 = 8
dp[2] = 8
```

그러나 3번 집까지 고려하면 두 후보를 다시 비교한다.

```text
3번 집을 털지 않음
→ dp[2] = 8
→ 0번 + 2번

3번 집을 털음
→ dp[1] + nums[3]
→ 5 + 10 = 15
→ 1번 + 3번
```

따라서 다음과 같이 선택 조합이 바뀐다.

```text
dp[3] = max(8, 15) = 15
```

`dp[2]`에서 0번과 2번 집을 선택했더라도 `dp[3]`에서 그 선택을 유지할 필요는 없다. 현재 집을 포함한 새로운 조합이 더 크다면 기존 조합을 버리고 새로운 최댓값을 저장한다.

#### 8. 초기값

집이 하나만 있다면 그 집을 터는 것이 최댓값이다.

```text
dp[0] = nums[0]
```

집이 두 개라면 인접해서 동시에 털 수 없으므로 둘 중 큰 값을 선택한다.

```text
dp[1] = max(nums[0], nums[1])
```

---

## 4. 개선

### 1차 개선: 턴다 / 안 턴다 완전탐색

각 집에서 두 선택을 모두 탐색하고 더 큰 결과를 반환한다.

<details>
<summary>완전탐색 코드</summary>

```java
class Solution {

    public int rob(int[] nums) {
        return search(0, nums);
    }

    private int search(int index, int[] nums) {
        if (index >= nums.length) {
            return 0;
        }

        int take = nums[index] + search(index + 2, nums);
        int skip = search(index + 1, nums);

        return Math.max(take, skip);
    }
}
```

</details>

#### 개선 결과

- 짝수·홀수 조합에 제한되지 않고 가능한 선택을 모두 확인하므로 정답을 구할 수 있다.
- 같은 `index`의 결과를 반복해서 계산하므로 시간복잡도가 `O(2^n)`까지 증가할 수 있다.

### 2차 개선: Top-Down 메모이제이션

각 `index`에서 얻을 수 있는 최대 금액을 배열에 저장한다.

<details>
<summary>메모이제이션 코드</summary>

```java
import java.util.Arrays;

class Solution {

    private int[] memo;

    public int rob(int[] nums) {
        memo = new int[nums.length];
        Arrays.fill(memo, -1);

        return search(0, nums);
    }

    private int search(int index, int[] nums) {
        if (index >= nums.length) {
            return 0;
        }

        if (memo[index] != -1) {
            return memo[index];
        }

        int take = nums[index] + search(index + 2, nums);
        int skip = search(index + 1, nums);

        memo[index] = Math.max(take, skip);
        return memo[index];
    }
}
```

</details>

#### 개선 결과

- 같은 위치의 결과를 한 번만 계산한다.
- 시간복잡도는 `O(n)`, 공간복잡도는 재귀 호출 스택을 포함해 `O(n)`이다.

### 3차 개선: Bottom-Up DP

작은 범위의 정답부터 차례대로 계산한다.

<details open>
<summary>Bottom-Up DP 코드</summary>

```java
class Solution {

    public int rob(int[] nums) {
        int n = nums.length;

        if (n == 1) {
            return nums[0];
        }

        int[] dp = new int[n];

        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);

        for (int i = 2; i < n; i++) {
            int skip = dp[i - 1];
            int take = dp[i - 2] + nums[i];

            dp[i] = Math.max(skip, take);
        }

        return dp[n - 1];
    }
}
```

</details>

#### 풀이 과정

1. `dp[i]`를 `0번부터 i번 집까지 고려했을 때의 최대 금액`으로 정의한다.
2. `dp[0]`에는 첫 번째 집의 돈을 저장한다.
3. `dp[1]`에는 첫 번째 집과 두 번째 집 중 큰 금액을 저장한다.
4. `i = 2`부터 현재 집을 털지 않는 경우와 터는 경우를 비교한다.
5. 더 큰 값을 `dp[i]`에 저장한다.
6. 마지막 집까지 고려한 최댓값인 `dp[n - 1]`을 반환한다.

#### 복잡도

```text
시간복잡도: O(n)
공간복잡도: O(n)
```

### 4차 개선: 공간 최적화

현재 상태를 계산할 때는 `dp[i - 1]`과 `dp[i - 2]`만 필요하므로 배열 전체를 저장하지 않아도 된다.

<details>
<summary>공간 최적화 코드</summary>

```java
class Solution {

    public int rob(int[] nums) {
        int twoBack = 0;
        int oneBack = 0;

        for (int money : nums) {
            int current = Math.max(
                oneBack,
                twoBack + money
            );

            twoBack = oneBack;
            oneBack = current;
        }

        return oneBack;
    }
}
```

</details>

#### 복잡도

```text
시간복잡도: O(n)
공간복잡도: O(1)
```

### 최종 정리

```text
최초 접근:
짝수 인덱스 합과 홀수 인덱스 합을 비교
→ 홀짝이 섞인 최적 조합을 놓쳐 오답

1차 개선:
각 집에서 턴다 / 안 턴다를 완전탐색
→ 정답은 구하지만 같은 상태를 반복 계산

2차 개선:
index별 최대 금액을 메모이제이션
→ 중복 계산을 제거해 O(n)에 해결

3차 개선:
작은 범위부터 Bottom-Up DP로 계산
→ dp[i - 1]과 dp[i - 2]를 비교해 현재 최댓값 결정

4차 개선:
직전 두 상태만 변수로 저장
→ O(n) 시간, O(1) 공간에 해결
```

### 이번 문제에서 기억할 점

```text
DP를 처음부터 점화식으로 떠올리려고 하지 않는다.

1. 현재 위치에서 가능한 선택을 나눈다.
2. 각 선택 후 이동할 위치를 정한다.
3. 완전탐색 재귀식을 만든다.
4. 같은 상태가 반복되는지 확인한다.
5. 반복되는 결과를 저장해 DP로 바꾼다.
```

House Robber의 핵심은 다음 한 줄이다.

```text
현재 집까지의 최대 금액
= max(현재 집을 포기한 이전 최댓값, 현재 집을 포함한 두 칸 전 최댓값)
```
