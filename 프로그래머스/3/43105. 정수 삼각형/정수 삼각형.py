def solution(triangle):
    dp = [col for col in triangle]
    for col in range(1,len(triangle)):
        for row in range(len(triangle[col])):
            dp[col][row] = max(max(dp[col-1][max(row-1,0)],dp[col-1][(min(row,len(triangle[col-1])-1))]) + triangle[col][row],dp[col][row])
    return max(dp[-1])