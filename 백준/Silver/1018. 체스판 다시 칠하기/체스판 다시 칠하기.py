def getStartStr(start):
    if (start == "W"):
        return "WB"*4
    return "BW"*4
def diffStr(str_a,str_b):
    return sum(1 for a, b in zip(str_a, str_b) if a != b)
min_eraser = 99999
# broute - force 로 풀기
N,M = map(int,input().split())
board = [input() for _ in range(N)]
# 정사각형 8*8 찾는 반복문
for col in range(N-7):
    for row in range(M-7):
        start = board[col][row]
        start_char = ["W","B"]
        now_eraser = [0,0]
        for plus in range(8):
            check = board[col+plus][row:row+8]
            now_eraser[0] += diffStr(check,getStartStr(start_char[0]))
            now_eraser[1] += diffStr(check,getStartStr(start_char[1]))
            
            start_char = start_char[::-1]
            if (min_eraser < min(now_eraser)):
                break
        else:
            if (min_eraser > min(now_eraser)):
                min_eraser = min(now_eraser)
print(min_eraser)