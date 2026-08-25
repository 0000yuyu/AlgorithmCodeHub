# 문제 풀이 회고

## 1. 문제 정보

- 문제: [표 병합](https://school.programmers.co.kr/learn/courses/30/lessons/150366)
- 플랫폼 / 난이도: 프로그래머스 / Level 3
- 유형: 구현, 시뮬레이션, 분리 집합(Union-Find)
- 목표 시간: 75분
- 실제 시간: 미기록
- 결과: 풀이 전 설계 및 해설 정리

---

## 2. 첫 접근

### 문제에서 관리해야 하는 상태

표는 `50 × 50`이며 각 좌표는 다음 두 정보를 가진다.

```text
1. 이 셀이 어느 병합 그룹에 포함되어 있는가?
2. 그 병합 그룹이 어떤 값을 가지고 있는가?
```

핵심은 병합된 셀을 각각 독립된 셀처럼 다루면 안 된다는 점이다. 예를 들어 `(1, 1)`, `(1, 2)`, `(1, 3)`이 병합되어 있다면 어느 좌표를 `UPDATE`하더라도 세 좌표가 가리키는 하나의 그룹 값이 변경되어야 한다.

### 단순하게 셀마다 값만 저장할 경우

```java
String[][] board = new String[51][51];
```

값만 저장하면 `MERGE` 이후 어떤 셀들이 같은 그룹인지 알 수 없다. 병합될 때마다 같은 값을 복사하는 방식도 다음 상황을 구분하지 못한다.

```text
(1,1)과 (1,2)가 우연히 같은 값인 경우
(1,1)과 (1,2)가 실제로 병합된 경우
```

값이 같다는 것과 같은 그룹이라는 것은 별개의 상태이다.

### 놓치기 쉬운 조건

- `MERGE`할 두 셀이 이미 같은 그룹이면 명령을 무시한다.
- 두 그룹 모두 값이 있으면 첫 번째 좌표가 속한 그룹의 값을 유지한다.
- `UNMERGE r c`는 해당 그룹 전체를 분리한다.
- 병합 해제 후 기존 값은 명령에서 선택한 `(r, c)`에만 남는다.
- `UPDATE value1 value2`는 좌표 하나가 아니라 모든 그룹의 값을 변경한다.
- 병합된 일반 셀이 아니라 그룹의 대표 셀에만 값을 저장해야 상태가 꼬이지 않는다.

### 초기 접근의 문제점

각 셀에 값과 그룹 번호를 따로 저장하고 `MERGE` 때마다 2,500개 셀의 그룹 번호를 모두 교체해도 제한 안에서는 통과할 수 있다. 하지만 그룹 병합과 대표 값 관리가 한 코드에 섞여 구현 실수가 발생하기 쉽다.

이 문제는 각 셀이 속한 그룹의 대표를 찾도록 만들면 명령의 의미가 단순해진다.

---

## 3. 새로 배운 내용

### 핵심 개념

- 2차원 좌표의 1차원 인덱스 변환
- Union-Find를 이용한 병합 그룹 관리
- 그룹의 값은 대표 노드에만 저장
- 병합 해제 전에 그룹 구성원을 먼저 수집
- 명령어 종류에 따른 상태 전이

### 1. 좌표를 하나의 번호로 변환한다

`50 × 50` 표의 좌표를 다음과 같이 1차원 번호로 바꾼다.

```java
int index = (row - 1) * 50 + (col - 1);
```

예를 들어 다음과 같다.

```text
(1,1) → 0
(1,2) → 1
(2,1) → 50
(50,50) → 2499
```

이제 각 셀은 `parent[0]`부터 `parent[2499]`까지 관리할 수 있다.

### 2. `parent[i]`의 의미

```text
parent[i] = i번 셀이 속한 병합 그룹의 부모
```

처음에는 모든 셀이 분리되어 있다.

```java
for (int i = 0; i < 2500; i++) {
    parent[i] = i;
}
```

대표 셀은 자신의 부모가 자기 자신이다.

```text
parent[i] == i
```

### 3. 값은 대표 셀에만 저장한다

```text
value[root] = 해당 그룹의 값
```

병합된 셀을 조회할 때는 먼저 대표를 찾은 뒤 대표의 값을 읽는다.

```java
int root = find(index);
String cellValue = value[root];
```

모든 구성원에게 값을 복사하지 않으므로 `UPDATE r c value`도 대표 셀 하나만 변경하면 된다.

### 4. `find`와 경로 압축

```java
private int find(int x) {
    if (parent[x] == x) {
        return x;
    }

    return parent[x] = find(parent[x]);
}
```

`find(x)`는 `x`가 속한 그룹의 대표 셀을 반환한다. 경로 압축을 적용하면 이후 같은 그룹을 조회할 때 대표에 빠르게 접근할 수 있다.

### 5. 명령별 처리 방식

#### `UPDATE r c value`

좌표가 속한 그룹의 대표 값을 변경한다.

```java
int root = find(toIndex(r, c));
values[root] = value;
```

#### `UPDATE value1 value2`

대표 노드가 가진 값만 검사하여 `value1`을 `value2`로 변경한다.

```java
for (int i = 0; i < SIZE; i++) {
    if (find(i) == i && values[i].equals(value1)) {
        values[i] = value2;
    }
}
```

#### `MERGE r1 c1 r2 c2`

두 좌표의 대표를 찾는다.

```java
int root1 = find(index1);
int root2 = find(index2);
```

이미 같은 그룹이면 아무 작업도 하지 않는다. 서로 다른 그룹이면 두 값을 비교해 병합 후 유지할 값을 먼저 결정한다.

```text
첫 번째 그룹에 값이 있음 → 첫 번째 값 유지
첫 번째 그룹이 비어 있음 → 두 번째 값 유지
```

두 그룹에 모두 값이 있더라도 문제 조건에 따라 첫 번째 그룹의 값을 유지한다. 따라서 `root2`를 `root1` 아래로 합치면 구현이 단순하다.

#### `UNMERGE r c`

이 명령이 가장 까다롭다.

```text
1. 선택한 셀의 대표와 기존 값을 저장한다.
2. 같은 대표를 가진 모든 셀을 찾는다.
3. 해당 셀들을 각각 독립된 그룹으로 되돌린다.
4. 모든 값을 비운다.
5. 선택한 (r,c)에만 기존 값을 복원한다.
```

그룹을 먼저 해제하면 어떤 셀이 원래 같은 그룹이었는지 잃어버린다. 따라서 반드시 구성원을 먼저 수집해야 한다.

#### `PRINT r c`

대표 셀의 값을 출력하며 값이 없으면 `EMPTY`를 출력한다.

---

## 4. 개선 및 최종 풀이

### 최종 설계

```text
parent[i] : i번 셀이 속한 그룹의 부모
values[i] : i가 대표 셀일 때 해당 그룹의 값
```

명령 파싱과 그룹 관리를 분리한다.

- `toIndex`: 좌표 변환
- `find`: 대표 조회
- `updateCell`: 좌표 기준 값 변경
- `updateValue`: 값 전체 변경
- `merge`: 두 그룹 병합
- `unmerge`: 그룹 전체 해제
- `print`: 대표 값 출력

### 최종 Java 코드

```java
import java.util.ArrayList;
import java.util.List;

class Solution {

    private static final int TABLE_SIZE = 50;
    private static final int CELL_COUNT = TABLE_SIZE * TABLE_SIZE;

    private final int[] parent = new int[CELL_COUNT];
    private final String[] values = new String[CELL_COUNT];

    public String[] solution(String[] commands) {
        initialize();

        List<String> answer = new ArrayList<>();

        for (String command : commands) {
            String[] tokens = command.split(" ");

            switch (tokens[0]) {
                case "UPDATE" -> {
                    if (tokens.length == 4) {
                        int row = Integer.parseInt(tokens[1]);
                        int col = Integer.parseInt(tokens[2]);
                        updateCell(row, col, tokens[3]);
                    } else {
                        updateValue(tokens[1], tokens[2]);
                    }
                }
                case "MERGE" -> merge(
                    Integer.parseInt(tokens[1]),
                    Integer.parseInt(tokens[2]),
                    Integer.parseInt(tokens[3]),
                    Integer.parseInt(tokens[4])
                );
                case "UNMERGE" -> unmerge(
                    Integer.parseInt(tokens[1]),
                    Integer.parseInt(tokens[2])
                );
                case "PRINT" -> answer.add(print(
                    Integer.parseInt(tokens[1]),
                    Integer.parseInt(tokens[2])
                ));
            }
        }

        return answer.toArray(new String[0]);
    }

    private void initialize() {
        for (int i = 0; i < CELL_COUNT; i++) {
            parent[i] = i;
            values[i] = "";
        }
    }

    private int toIndex(int row, int col) {
        return (row - 1) * TABLE_SIZE + (col - 1);
    }

    private int find(int x) {
        if (parent[x] == x) {
            return x;
        }

        return parent[x] = find(parent[x]);
    }

    private void updateCell(int row, int col, String newValue) {
        int root = find(toIndex(row, col));
        values[root] = newValue;
    }

    private void updateValue(String oldValue, String newValue) {
        for (int i = 0; i < CELL_COUNT; i++) {
            if (find(i) == i && values[i].equals(oldValue)) {
                values[i] = newValue;
            }
        }
    }

    private void merge(int row1, int col1, int row2, int col2) {
        int root1 = find(toIndex(row1, col1));
        int root2 = find(toIndex(row2, col2));

        if (root1 == root2) {
            return;
        }

        String mergedValue = !values[root1].isEmpty()
            ? values[root1]
            : values[root2];

        parent[root2] = root1;
        values[root1] = mergedValue;
        values[root2] = "";
    }

    private void unmerge(int row, int col) {
        int selected = toIndex(row, col);
        int root = find(selected);
        String savedValue = values[root];

        List<Integer> members = new ArrayList<>();

        for (int i = 0; i < CELL_COUNT; i++) {
            if (find(i) == root) {
                members.add(i);
            }
        }

        for (int member : members) {
            parent[member] = member;
            values[member] = "";
        }

        values[selected] = savedValue;
    }

    private String print(int row, int col) {
        int root = find(toIndex(row, col));

        return values[root].isEmpty()
            ? "EMPTY"
            : values[root];
    }
}
```

### 동작 예시

```text
UPDATE 1 1 menu
UPDATE 1 2 category
MERGE 1 1 1 2
```

두 그룹 모두 값이 있으므로 첫 번째 좌표의 값인 `menu`가 유지된다.

```text
대표: (1,1)
그룹 값: menu
(1,1) 출력: menu
(1,2) 출력: menu
```

이후 다음 명령을 실행한다.

```text
UNMERGE 1 2
```

병합 그룹 전체가 해제되고 기존 값은 명령에서 선택한 `(1,2)`에만 남는다.

```text
(1,1) 출력: EMPTY
(1,2) 출력: menu
```

### 시간복잡도

셀의 전체 개수를 `N = 2,500`, 명령어 개수를 `C`라고 한다.

| 명령 | 시간복잡도 |
|---|---:|
| `UPDATE r c value` | 거의 `O(1)` |
| `UPDATE value1 value2` | `O(N)` |
| `MERGE` | 거의 `O(1)` |
| `UNMERGE` | `O(N)` |
| `PRINT` | 거의 `O(1)` |

정확히는 `find`에 경로 압축이 적용되므로 단일 조회와 병합은 `O(α(N))`이다. `N`이 2,500으로 고정되어 있고 명령도 최대 1,000개이므로 전체 셀을 확인하는 명령이 있어도 충분하다.

### 공간복잡도

```text
parent 배열: O(N)
values 배열: O(N)
UNMERGE 구성원 목록: O(N)
```

따라서 전체 공간복잡도는 `O(N)`이다.

### 반드시 기억할 점

1. 같은 값과 같은 병합 그룹은 다른 개념이다.
2. 값은 그룹의 대표 셀에만 저장한다.
3. 두 그룹 모두 값이 있으면 첫 번째 좌표의 그룹 값을 유지한다.
4. `UNMERGE`에서는 그룹을 해제하기 전에 구성원을 먼저 찾는다.
5. 병합 해제 후 기존 값은 대표가 아니라 명령에서 선택한 셀에 남긴다.
6. 구현 문제는 명령별 상태 변화와 불변식을 먼저 정의하면 코드가 덜 꼬인다.

### 재풀이 체크리스트

- [ ] 좌표를 1차원 인덱스로 변환할 수 있는가?
- [ ] `parent[i]`와 `values[i]`의 의미를 설명할 수 있는가?
- [ ] 두 `UPDATE` 명령을 구분해 구현할 수 있는가?
- [ ] `MERGE`의 값 우선순위를 처리할 수 있는가?
- [ ] `UNMERGE`의 처리 순서를 코드 없이 설명할 수 있는가?
- [ ] 해설을 닫고 60분 안에 다시 구현할 수 있는가?
