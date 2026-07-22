// Last updated: 2026. 7. 22. 오후 5:20:59
1import java.util.HashMap;
2import java.util.Map;
3
4class Solution {
5    public int subarraySum(int[] nums, int k) {
6        Map<Integer, Integer> prefixCount = new HashMap<>();
7
8        prefixCount.put(0,1);
9
10        int prefixSum = 0;
11        int count = 0;
12
13        for(int num:nums) {
14            prefixSum+=num;
15
16            int target = prefixSum - k;
17            count+=prefixCount.getOrDefault(target,0);
18
19            prefixCount.put(
20                prefixSum,
21                prefixCount.getOrDefault(prefixSum,0)+1
22            );
23        }
24        return count;
25    }
26}