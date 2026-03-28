from collections import deque

def get_sliding_min(arr, k):
    """길이 n인 배열 arr에서 크기 k인 윈도우의 최솟값들을 O(N)으로 구함"""
    res = []
    dq = deque()
    for i, val in enumerate(arr):
        # 나보다 큰 애들은 최솟값이 될 가능성이 없으니 퇴출
        while dq and arr[dq[-1]] >= val:
            dq.pop()
        dq.append(i)
        # 윈도우 범위를 벗어난 인덱스 퇴출
        if dq[0] <= i - k:
            dq.popleft()
        # 윈도우 크기가 k가 된 시점부터 결과 저장
        if i >= k - 1:
            res.append(arr[dq[0]])
    return res

def solution(m, n, h, w, drops):
    INF = len(drops) + 1
    grid = [[INF] * n for _ in range(m)]
    for seq, [x, y] in enumerate(drops):
        if grid[x][y] == INF:
            grid[x][y] = seq + 1

    # 1. 가로 압축 (각 행을 슬라이딩 윈도우로 처리)
    # array_row[m][n-w+1]
    array_row = []
    for r in range(m):
        array_row.append(get_sliding_min(grid[r], w))

    # 2. 세로 압축 (각 열을 슬라이딩 윈도우로 처리)
    # 가로 압축된 결과의 '열'을 하나씩 뽑아서 다시 슬라이딩 윈도우 적용
    final_min_grid = []
    for c in range(n - w + 1):
        col_data = [array_row[r][c] for r in range(m)]
        final_min_grid.append(get_sliding_min(col_data, h))

    # 3. 최댓값 찾기 (final_min_grid는 현재 [열][행] 구조임에 주의!)
    max_data = -1
    pos = [0, 0]
    
    # 문제의 우선순위(위쪽 행 -> 왼쪽 열)를 위해 r, c 순서로 체크
    for r in range(m - h + 1):
        for c in range(n - w + 1):
            # final_min_grid[열인덱스][행인덱스]
            data = final_min_grid[c][r]
            if data > max_data:
                max_data = data
                pos = [r, c]
                
    return pos