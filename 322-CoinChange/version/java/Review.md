# 문제 풀이 회고

## 1. 문제 정보

- 문제: [322. Coin Change](https://leetcode.com/problems/coin-change/description/)
- 플랫폼 / 난이도: LeetCode / Medium
- 풀이 날짜: 2026/07/24
- 목표 시간: 45분
- 실제 시간: 약 55분
- 결과: 실패 / DFS 정답 도출 후 시간 초과 / DP 학습

---

## 2. 첫 접근

### 처음 생각한 풀이

- 떠올린 개념: DFS, 백트래킹, 그리디
- 그렇게 생각한 이유:
  - 큰 동전부터 최대한 사용하면 동전 개수를 줄일 수 있다고 생각했다.
  - 각 동전을 사용하는 경우와 사용하지 않는 경우로 나누어 재귀적으로 탐색하려 했다.
- 예상 시간복잡도: 정확히 계산하지 못함

<details>
<summary>처음 작성한 코드</summary>

```java
import java.util.Arrays;

class Solution {

    int[] coins;
    int minCount = 999999999;

    public int coinChange(int[] coins, int amount) {
        Arrays.sort(coins);
        this.coins = coins;

        if (amount >= coins[coins.length - 1]) {
            charge(
                amount % coins[coins.length - 1],
                amount / coins[coins.length - 1],
                coins.length - 2
            );
        }

        charge(amount, 0, coins.length - 2);

        if (minCount == 999999999) {
            return -1;
        }

        return minCount;
    }

    public void charge(int amount, int count, int nowIdx) {
        if (amount == 0) {
            if (count < minCount) {
                minCount = count;
            }
            return;
        }

        if (nowIdx < 0) {
            return;
        }

        if (amount >= coins[nowIdx]) {
            count += amount / coins[nowIdx];
            amount = amount % coins[nowIdx];
            charge(amount, count, nowIdx - 1);
        }

        charge(amount, count, nowIdx - 1);
    }
}
```

</details>

### 실패한 테스트 또는 반례

```text
coins = [1, 3, 4]
amount = 10
```

처음 작성한 코드는 다음 두 경우만 탐색한다.

```text
4원을 최대한 사용: 4 + 4 + 1 + 1 = 4개
4원을 사용하지 않음: 3 + 3 + 3 + 1 = 4개
```

하지만 실제 최소 조합은 다음과 같다.

```text
4 + 3 + 3 = 3개
```

### 놓친 조건

- 한 종류의 동전을 사용할 때 `0개` 또는 `가능한 최대 개수`만 고려했다.
- 최소 조합을 만들기 위해 동전을 중간 개수만 사용해야 하는 경우를 고려하지 못했다.
- 큰 동전부터 최대한 사용하는 그리디 선택이 항상 최적해를 보장하지 않는다.

### 실패 이유

처음 작성한 코드는 현재 동전을 다음 두 가지 방식으로만 탐색했다.

```text
1. 현재 동전을 가능한 만큼 최대한 사용한다.
2. 현재 동전을 전혀 사용하지 않는다.
```

그러나 정답에는 현재 동전을 `1개`, `2개`, `3개`처럼 중간 개수만 사용하는 경우가 포함될 수 있다. 따라서 각 동전의 가능한 사용 개수를 모두 탐색해야 한다.

---

## 3. 새로 배운 내용

### 핵심 개념

- 그리디 선택이 성립하는 조건
- DFS를 이용한 모든 조합 탐색
- 백트래킹과 가지치기
- 중복 부분 문제
- 동적 계획법(Dynamic Programming)

### 개념 정리

#### 1. 큰 동전부터 사용하는 것이 항상 최소 개수를 보장하지 않는다

동전의 값이 임의로 주어지기 때문에 큰 동전을 최대한 사용하는 선택이 항상 최적해가 되지는 않는다.

예를 들어 다음과 같은 경우가 있다.

```text
coins = [1, 3, 4]
amount = 6
```

큰 동전부터 사용하면 다음과 같다.

```text
4 + 1 + 1 = 3개
```

하지만 최소 조합은 다음과 같다.

```text
3 + 3 = 2개
```

따라서 이 문제에는 단순한 그리디 방식을 적용할 수 없다.

#### 2. 모든 사용 개수를 탐색해야 한다

현재 동전으로 사용할 수 있는 최대 개수가 `maxUse`라면 다음 경우를 모두 확인해야 한다.

```text
maxUse개, maxUse - 1개, ..., 1개, 0개
```

이를 DFS로 표현하면 다음과 같다.

```java
for (int use = maxUse; use >= 0; use--) {
    charge(
        amount - coin * use,
        count + use,
        nowIdx - 1
    );
}
```

#### 3. `use == 0`도 반복문에 포함된다

반복문이 `0`까지 실행되므로 현재 동전을 사용하지 않는 경우도 이미 탐색한다.

```java
for (int use = maxUse; use >= 0; use--)
```

따라서 반복문 뒤에서 다음 코드를 다시 호출하면 같은 경우를 중복 탐색한다.

```java
charge(amount, count, nowIdx - 1);
```

이 호출은 제거해야 한다.

#### 4. DFS가 시간 초과되는 이유

모든 동전의 가능한 사용 개수를 조합하면 탐색해야 하는 경우의 수가 매우 많아진다.

또한 서로 다른 선택 과정을 거쳤더라도 같은 남은 금액에 도달할 수 있다.

```text
여러 탐색 경로 → 남은 금액 100 → 100원을 만드는 방법을 다시 계산
```

DFS에서는 이러한 계산을 반복하기 때문에 입력이 커지면 시간 초과가 발생한다.

#### 5. 가지치기로 불필요한 탐색을 줄일 수 있다

현재 사용한 동전 개수가 이미 발견한 최소 개수 이상이라면 더 탐색할 필요가 없다.

```java
if (count >= minCount) {
    return;
}
```

현재 동전을 추가했을 때도 최소 개수 이상이면 해당 분기를 생략할 수 있다.

```java
if (count + use >= minCount) {
    continue;
}
```

가지치기는 DFS를 빠르게 만들지만, 최악의 경우 모든 조합을 탐색할 가능성은 여전히 남아 있다.

#### 6. DP로 중복 계산을 제거한다

다음과 같이 DP 배열을 정의한다.

```text
dp[i] = 금액 i를 만드는 데 필요한 최소 동전 개수
```

현재 금액이 `current`이고 동전 하나의 값이 `coin`이라면, 해당 동전을 마지막으로 사용했을 때의 동전 개수는 다음과 같다.

```text
dp[current - coin] + 1
```

따라서 점화식은 다음과 같다.

```text
dp[current] = min(dp[current], dp[current - coin] + 1)
```

#### 7. `dp[0] = 0`인 이유

금액 `0`을 만드는 데 필요한 동전은 `0개`이기 때문이다.

```java
dp[0] = 0;
```

이 값이 이후 계산의 시작점이 된다.

예를 들어 동전 `3`으로 금액 `3`을 만들 때:

```text
dp[3] = dp[3 - 3] + 1
      = dp[0] + 1
      = 1
```

#### 8. 도달할 수 없는 상태의 초기값

각 금액의 초기값은 실제 정답으로 나올 수 없는 큰 값으로 설정한다.

```java
Arrays.fill(dp, amount + 1);
```

금액 `amount`를 만들 때 필요한 동전 개수는 동전의 최솟값이 `1`인 경우에도 최대 `amount`개이므로, `amount + 1`은 도달하지 못한 상태를 나타내기에 충분하다.

---

## 4. 개선

### 1차 개선: 모든 사용 개수를 탐색하는 DFS

각 동전을 최대 개수부터 0개까지 모두 사용해 본다.

<details>
<summary>모든 조합을 탐색하는 DFS 코드</summary>

```java
import java.util.Arrays;

class Solution {

    int[] coins;
    int minCount = 999999999;

    public int coinChange(int[] coins, int amount) {
        Arrays.sort(coins);
        this.coins = coins;

        charge(amount, 0, coins.length - 1);

        if (minCount == 999999999) {
            return -1;
        }

        return minCount;
    }

    public void charge(int amount, int count, int nowIdx) {
        if (amount == 0) {
            minCount = Math.min(minCount, count);
            return;
        }

        if (nowIdx < 0) {
            return;
        }

        int coin = coins[nowIdx];
        int maxUse = amount / coin;

        for (int use = maxUse; use >= 0; use--) {
            charge(
                amount - coin * use,
                count + use,
                nowIdx - 1
            );
        }
    }
}
```

</details>

#### 개선 결과

- 중간 개수의 동전을 사용하는 경우까지 탐색하여 정답을 구할 수 있게 되었다.
- 그러나 가능한 조합을 전부 탐색하므로 시간 초과가 발생했다.

### 2차 개선: 가지치기를 추가한 DFS

이미 구한 최소 개수보다 많은 동전을 사용하는 경로는 더 이상 탐색하지 않는다.

<details>
<summary>가지치기를 적용한 DFS 코드</summary>

```java
import java.util.Arrays;

class Solution {

    private int[] coins;
    private int minCount;

    public int coinChange(int[] coins, int amount) {
        Arrays.sort(coins);

        this.coins = coins;
        this.minCount = Integer.MAX_VALUE;

        charge(amount, 0, coins.length - 1);

        return minCount == Integer.MAX_VALUE ? -1 : minCount;
    }

    private void charge(int amount, int count, int nowIdx) {
        if (amount == 0) {
            minCount = Math.min(minCount, count);
            return;
        }

        if (nowIdx < 0 || count >= minCount) {
            return;
        }

        int coin = coins[nowIdx];
        int maxUse = amount / coin;

        for (int use = maxUse; use >= 0; use--) {
            if (count + use >= minCount) {
                continue;
            }

            int remaining = amount - coin * use;

            if (nowIdx == 0) {
                if (remaining % coins[0] == 0) {
                    minCount = Math.min(
                        minCount,
                        count + use + remaining / coins[0]
                    );
                }
                continue;
            }

            charge(remaining, count + use, nowIdx - 1);
        }
    }
}
```

</details>

#### 개선 결과

- 이미 최소 개수가 될 수 없는 경로를 제거해 탐색량을 줄였다.
- 하지만 최악의 경우 조합을 많이 탐색해야 한다는 DFS의 근본적인 한계는 남아 있다.

### 3차 개선: Bottom-Up DP

각 금액을 만드는 최소 동전 개수를 한 번만 계산해 저장한다.

<details open>
<summary>최종 DP 코드</summary>

```java
import java.util.Arrays;

class Solution {

    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];

        Arrays.fill(dp, amount + 1);
        dp[0] = 0;

        for (int current = 1; current <= amount; current++) {
            for (int coin : coins) {
                if (current >= coin) {
                    dp[current] = Math.min(
                        dp[current],
                        dp[current - coin] + 1
                    );
                }
            }
        }

        return dp[amount] == amount + 1 ? -1 : dp[amount];
    }
}
```

</details>

### DP 풀이 과정

1. `dp[i]`를 금액 `i`를 만드는 최소 동전 개수로 정의한다.
2. 모든 값을 도달 불가능한 상태인 `amount + 1`로 초기화한다.
3. 금액 `0`은 동전이 필요하지 않으므로 `dp[0] = 0`으로 설정한다.
4. `1`부터 `amount`까지 각 금액을 순회한다.
5. 현재 금액보다 작거나 같은 모든 동전을 확인한다.
6. 해당 동전을 마지막으로 사용했다고 가정하여 `dp[current - coin] + 1`을 계산한다.
7. 기존 값과 비교해 더 작은 값을 `dp[current]`에 저장한다.
8. `dp[amount]`가 초기값 그대로라면 만들 수 없는 금액이므로 `-1`을 반환한다.

### 복잡도

```text
시간복잡도: O(amount × coins.length)
공간복잡도: O(amount)
```

### 최종 정리

```text
최초 접근:
큰 동전부터 0개 또는 최대 개수만 사용
→ 중간 사용 개수를 놓쳐 오답

1차 개선:
각 동전의 가능한 사용 개수를 모두 DFS로 탐색
→ 정답은 구하지만 시간 초과

2차 개선:
현재 최소 개수를 이용해 가지치기
→ 탐색량은 감소하지만 최악의 경우 여전히 느림

최종 개선:
금액별 최소 동전 개수를 DP에 저장
→ 중복 계산을 제거하여 O(amount × coins.length)에 해결
```
