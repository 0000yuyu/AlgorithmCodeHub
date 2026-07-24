// Last updated: 2026. 7. 24. 오후 6:16:28
1import java.util.Arrays;
2
3class Solution {
4    public int coinChange(int[] coins, int amount) {
5        int[] dp = new int[amount + 1];
6
7        Arrays.fill(dp, amount + 1);
8        dp[0] = 0;
9
10        for (int current = 1; current <= amount; current++) {
11            for (int coin : coins) {
12                if (current >= coin) {
13                    dp[current] = Math.min(
14                        dp[current],
15                        dp[current - coin] + 1
16                    );
17                }
18            }
19        }
20
21        return dp[amount] == amount + 1 ? -1 : dp[amount];
22    }
23}