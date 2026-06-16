import java.util.LinkedList;
import java.util.Queue;

class Solution {
    // 1. 프로세스의 원래 위치(index)와 우선순위(priority)를 같이 저장할 클래스 선언
    static class Process {
        int index;
        int priority;

        public Process(int index, int priority) {
            this.index = index;
            this.priority = priority;
        }
    }

    public int solution(int[] priorities, int location) {
        // 2. 일반 큐(LinkedList 구현체) 생성 후 데이터 삽입
        Queue<Process> queue = new LinkedList<>();
        for (int i = 0; i < priorities.length; i++) {
            queue.add(new Process(i, priorities[i]));
        }

        int answer = 0; // 실행 순서 카운트

        // 3. 큐가 빌 때까지 반복
        while (!queue.isEmpty()) {
            Process current = queue.poll(); // 맨 앞의 프로세스를 일단 꺼냄
            boolean hasHigherPriority = false;

            // 4. 현재 꺼낸 프로세스보다 우선순위가 높은 프로세스가 큐에 남아있는지 확인
            for (Process p : queue) {
                if (p.priority > current.priority) {
                    hasHigherPriority = true;
                    break;
                }
            }

            // 5. 조건에 따른 처리
            if (hasHigherPriority) {
                // 뒤에 더 높은 우선순위가 있다면, 방금 꺼낸 걸 다시 맨 뒤로 보냄
                queue.add(current);
            } else {
                // 현재 프로세스가 가장 우선순위가 높다면 실행 처리
                answer++;
                
                // 실행한 프로세스가 내가 찾던 location의 프로세스라면 정답 리턴
                if (current.index == location) {
                    return answer;
                }
            }
        }

        return answer;
    }
}