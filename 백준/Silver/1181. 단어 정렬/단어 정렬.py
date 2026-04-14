N = int(input())
word_list = list(set(input() for _ in range(N)))
word_list.sort(key=lambda x : (len(x),x))
for word in word_list:
    print(word)