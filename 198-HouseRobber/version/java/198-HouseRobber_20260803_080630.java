// Last updated: 2026. 8. 3. 오전 8:06:30
1class Solution {
2    public int rob(int[] nums) {
3        // dp 배열 만들기
4        int[] dp = new int[nums.length];
5        dp[0] = nums[0];
6        if (nums.length == 1) return dp[0];
7        dp[1] = Math.max(dp[0],nums[1]);
8        for (int i = 2; i< nums.length ; i++) {
9            // 현재 값 구하기
10            int now =  dp[i-2] + nums[i];
11            // 값 갱신
12            dp[i] = Math.max(dp[i-1],now);
13        }
14        return dp[nums.length-1];
15    }
16}