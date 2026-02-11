import java.util.*;
import java.util.stream.*;
class Solution {
    public int solution(int n, int w, int num) {
        // 몇 겹으로 쌓았는지 확인
        int step = (n-1) / w + 1;
        // 반복문으로 일일이 쌓는 방법 밖에 생각이 안남
        int[][] result = new int[step][w];
        for (int i = 0; i < step; i++) {
            result[i] = getArray(i,w,n);
        }
        // 1. 스트림으로 num의 위치(row, col) 찾기
        int[] targetPos = IntStream.range(0, step)
            .boxed()
            .flatMap(r -> IntStream.range(0, w)
                .filter(c -> result[r][c] == num)
                .mapToObj(c -> new int[]{r, c}))
            .findFirst()
            .orElse(new int[]{-1, -1});

        int targetRow = targetPos[0];
        int targetCol = targetPos[1];

        // 2. 해당 열(targetCol)에서 targetRow부터 위쪽(마지막 행까지) 0이 아닌 상자 개수 세기
        long count = 0;
        if (targetRow != -1) {
            count = IntStream.range(targetRow, step) // 현재 층부터 맨 위층까지
                .filter(r -> result[r][targetCol] != 0) // 0(빈 공간)이 아닌 것만
                .count();
        }

        int answer = 0;
        return (int) count;
    }
    public int[] getArray (int i,int w,int n)
    {
        int start = i*w+1;
        int end = Math.min(start + w-1,n);
        int count = end-start + 1;
        int[] array;
        if (i%2==0)
            array = IntStream.rangeClosed(start, end).toArray();
        else
            array = IntStream.range(0,count)
                              .map(j -> end - j)
                              .toArray();
        if (count == w) return array;
        else
        {
            int[] rest = new int[w-count];
            if (i%2==0) return IntStream.concat(Arrays.stream(array), Arrays.stream(rest)).toArray();
            else return IntStream.concat(Arrays.stream(rest), Arrays.stream(array)).toArray();
        }
    }
}