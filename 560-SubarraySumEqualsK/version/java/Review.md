# 문제 풀이 회고

## 1. 문제 정보

- 문제: [560. Subarray Sum Equals K](https://leetcode.com/problems/subarray-sum-equals-k/description/)
- 플랫폼 / 난이도: LeetCode / Medium
- 풀이 날짜: 2026/07/22
- 목표 시간: 35분
- 실제 시간: 1시간 이상
- 결과: 실패 / 해설 참고

---

## 2. 첫 접근

### 처음 생각한 풀이

- 떠올린 개념: 투 포인터, 슬라이딩 윈도우
- 그렇게 생각한 이유: 두 포인터를 옮기며 연속된 구간의 합을 계산하면 원하는 합을 찾을 수 있다고 생각했다.
- 예상 시간복잡도: O(n)

    <details>
    <summary>처음 작성한 코드</summary>
    
    ```java
    class Solution {
        public int subarraySum(int[] nums, int k) {
            int count = 0;
    
            int startIdx = 0;
            int endIdx = 0;
            int total = nums[endIdx];
    
            while (startIdx <= endIdx && endIdx < nums.length) {
                if (total == k) {
                    count++;
                }
    
                if (total >= k) {
                    total -= nums[startIdx];
                    startIdx++;
                } else {
                    endIdx++;
    
                    if (endIdx < nums.length) {
                        total += nums[endIdx];
                    }
                }
            }
    
            return count;
        }
    }
    ```
    
    </details>

- 통과한 테스트: `[1, 1, 1]`, `[1, 2, 3]`
- 실패한 테스트 또는 반례: `[-1, -1, 0]`
- 놓친 조건: 배열에 음수가 포함될 수 있다는 조건을 고려하지 못했다.
- 실패 이유:
  - 현재 알고리즘은 합이 `k` 이상이면 왼쪽 포인터를 이동한다.
  - 하지만 음수가 포함되면 왼쪽 값을 제거했을 때 합이 감소한다고 보장할 수 없다.
  - 오른쪽 값을 추가했을 때도 합이 증가한다고 보장할 수 없다.
  - 따라서 현재 합과 `k`의 크기만으로 어떤 포인터를 움직일지 결정할 수 없다.
  - 또한 `0`이 여러 개 포함되면 같은 위치에서 끝나는 여러 부분 배열을 놓칠 수 있다.

---

## 3. 새로 배운 내용

### 핵심 개념

- 누적합
- HashMap을 이용한 이전 누적합의 등장 횟수 저장

### 개념 정리

#### 1. 중간 구간의 합을 구하는 방법

현재 위치까지의 누적합을 `currentSum`, 어떤 이전 위치까지의 누적합을 `previousSum`이라고 하면 두 위치 사이의 구간 합은 다음과 같다.

```text
구간 합 = currentSum - previousSum
```

합이 `k`인 구간을 찾으려면 다음 조건을 만족하는 이전 누적합이 필요하다.

```text
currentSum - previousSum = k
previousSum = currentSum - k
```

따라서 현재 누적합이 `currentSum`일 때, 과거에 `currentSum - k`라는 누적합이 존재했는지 확인하면 된다.

#### 2. 왜 HashMap을 사용하는가?

HashMap에는 다음 정보를 저장한다.

```text
누적합 → 해당 누적합이 지금까지 등장한 횟수
```

같은 누적합이 여러 번 등장할 수 있으며, 각각 다른 부분 배열의 시작점이 될 수 있다. 따라서 존재 여부만 저장하는 `Set`이 아니라 등장 횟수를 저장하는 `Map`이 필요하다.

#### 3. 왜 `prefixCount.put(0, 1)`이 필요한가?

배열을 탐색하기 전의 누적합을 `0`이라고 보기 때문이다.

현재까지의 누적합이 정확히 `k`라면:

```text
currentSum - k = 0
```

이때 Map에 `0`이 있어야 배열의 첫 번째 원소부터 현재 위치까지의 구간도 정답으로 계산할 수 있다.

#### 4. 왜 조회한 뒤 현재 누적합을 저장하는가?

현재 누적합을 먼저 저장하면 현재 위치를 구간의 시작과 끝으로 동시에 사용하는 잘못된 경우가 포함될 수 있음

1. 현재 누적합 계산
2. 과거에 `현재 누적합 - k`가 몇 번 등장했는지 확인
3. 현재 누적합을 Map에 저장

---

## 4. 개선

### 풀이 과정

1. 배열을 순회하며 현재까지의 누적합을 구한다.
2. `현재 누적합 - k`를 계산한다.
3. 해당 값이 HashMap에 존재하면 그 등장 횟수를 정답에 더한다.
4. 현재 누적합의 등장 횟수를 HashMap에 추가한다.

### 코드

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    public int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> prefixCount = new HashMap<>();

        prefixCount.put(0, 1);

        int prefixSum = 0;
        int count = 0;

        for (int num : nums) {
            prefixSum += num;

            int target = prefixSum - k;
            count += prefixCount.getOrDefault(target, 0);

            prefixCount.put(
                prefixSum,
                prefixCount.getOrDefault(prefixSum, 0) + 1
            );
        }

        return count;
    }
}
