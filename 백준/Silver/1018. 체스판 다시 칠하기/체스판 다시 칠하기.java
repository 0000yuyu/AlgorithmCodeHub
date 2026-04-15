import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        int M = scanner.nextInt();
        List<String> board = new ArrayList<String>();
        for (int i=0;i<N;i++){
            String word = scanner.next();
            board.add(word);
        }
        String checkW = "WBWBWBWB";
        String checkB = "BWBWBWBW";
        int minEraser = 99999999;
        for (int col=0;col<N-7;col++)
        {
            for (int row=0;row<M-7;row++)
            {
                int[] nowEraser = new int[2];
                for (int plus=0;plus<8;plus++)
                {
                    String check = board.get(col+plus).substring(row, row+8);
                    nowEraser[plus%2]+=getDiffCount(check, checkW);
                    nowEraser[1-plus%2]+=getDiffCount(check, checkB);
                    if (minEraser < Math.min(nowEraser[0],nowEraser[1])) {
                        break;
                    }
                }
                if (minEraser > Math.min(nowEraser[0],nowEraser[1])) {
                    minEraser = Math.min(nowEraser[0],nowEraser[1]);
                }
            }
        }
        System.out.println(minEraser);
    }
    public static int getDiffCount(String a, String b)
    {
        int diffCount = IntStream.range(0, Math.min(a.length(), b.length()))
            .map(i -> a.charAt(i) != b.charAt(i) ? 1 : 0)
            .sum() + Math.abs(a.length() - b.length());
        return diffCount;

    }
}
