x = open ('C:\\dev\\2023fa-420-Xterminators\\src\\dictionary.txt', 'r')

all_words = x.read()
arr = all_words.split() 
allowed = []

for i in arr:
    chars = []
    for c in i:
        if (c not in chars):
            chars.append(c)
    #if (len(chars) <= 7): # for getting all valid words
    if (len(chars) == 7): # for getting only valid starting words
        allowed.append(i)

y = open ('result.txt', 'w')

for i in allowed:
    y.write(i + '\r\n')
