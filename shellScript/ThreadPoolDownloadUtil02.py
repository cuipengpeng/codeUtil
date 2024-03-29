# -*- coding: utf-8 -*-

import asyncio
import httpx
import requests

# https://blog.csdn.net/as604049322/article/details/119847193
def calc_divisional_range(filesize, chuck=10):
    step = filesize//chuck
    arr = list(range(0, filesize, step))
    result = []
    for i in range(len(arr)-1):
        s_pos, e_pos = arr[i], arr[i+1]-1
        result.append([s_pos, e_pos])
    result[-1][-1] = filesize-1
    return result


# 下载方法
async def async_range_download(save_name, s_pos, e_pos):
    headers = {"Range": f"bytes={s_pos}-{e_pos}"}
    res = await client.get(url, headers=headers)
    with open(save_name, "rb+") as f:
        f.seek(s_pos)
        f.write(res.content)

#httpx该库支持http2.0能够爬取http2.0协议的网页。
#之后client对象与request库的API几乎完全一致，只需把之前代码中使用的requests改成这个client对象即可。
client = httpx.AsyncClient()

url="https://autoupdate.termius.com/windows/Termius.exe"
res = httpx.head(url)
filesize = int(res.headers['Content-Length'])
divisional_ranges = calc_divisional_range(filesize, 3)


save_name = "Termius.exe"
# 先创建空文件
with open(save_name, "wb") as f:
    pass

loop = asyncio.get_event_loop()
tasks = [async_range_download(save_name, s_pos, e_pos)
         for s_pos, e_pos in divisional_ranges]
# 等待所有协程执行完毕
loop.run_until_complete(asyncio.wait(tasks))




  
  
  



