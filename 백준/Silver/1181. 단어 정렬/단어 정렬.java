import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class Main
{
    public static void main(String args[])
    {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        List<String> word_list = new ArrayList<>(N);
        for (int i = 0;i<N;i++){
            word_list.add(scanner.next());
        }
        // 중복 제거
        word_list = word_list.stream().distinct().collect(Collectors.toList());
        // 정렬
        word_list.sort((s1,s2) -> {
            if (s1.length() != s2.length()) {
                return s1.length() - s2.length();
            }
            return s1.compareTo(s2);
        });
        // 결과 출력
        for (String word: word_list) {
            System.out.println(word);
        }
        scanner.close();
    }
}