from  selenium import webdriver

driver = None


def get_driver(is_new=False):
    global driver
    if is_new or driver is None:
        driver = webdriver.Firefox()
    return driver


a = [1, 2, 3, 5, 6, 8]

ret = []
tr = []
for i in range(len(a)):
    if i % 3 == 2:
        tr.append(a[i])
        ret.append(tr)
        tr = []
    else:
        tr.append(a[i])


print(len(tr))
print(len(ret))
for i in range(len(ret)):
    print(ret[i][0], ret[i][1], ret[i][2])
