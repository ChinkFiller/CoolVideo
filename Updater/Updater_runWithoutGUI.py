'''
@author:Conyafer
@Time:2024-8-5
Get the animation data from the Internet
'''
import time
import requests
from bs4 import BeautifulSoup
import sqlite3 as SQL
import datetime
import sys
import pymysql
import io


#使用数据库的函数
def use_sql():
    if type=='mysql':
        ip=position.split(":")[0]
        port=int(position.split(":")[1])
        s=pymysql.connect(host=ip,port=port,user=username,password=password,db=database,charset='utf8mb4')
        c=s.cursor()
        return s,c
    else:
        s = SQL.connect(position)
        c = s.cursor()
        return s,c

#写入数据库的函数
def write_data(id,name,leader,actor,state,img_url):
    s,c=use_sql()
    if type=='mysql':
        c.execute(f"INSERT INTO film_data (id,name,state,leader,actor,img_url,agree) VALUES ({id},'{name}','{state}','{leader}','{actor}','{img_url}',0) ON DUPLICATE KEY UPDATE state = '{state}';")
    else:
        c.execute(f'INSERT INTO film_data (id,name,state,leader,actor,img_url,agree) VALUES ({id},"{name}","{state}","{leader}","{actor}","{img_url}",0) ON CONFLICT (id) DO UPDATE SET state = "{state}";')
    s.commit()
    c.close()
    s.close()

#获取视频信息的函数
def msg_getter(id:int):
    res=requests.get(f"https://www.mutean.com/voddetail/{id}.html")
    data=res.text
    soup=BeautifulSoup(data,'html.parser')
    title=soup.find('title')
    if "错误提示 - MuteFun动漫网站-无声乐趣-(゜-゜)つロ 干杯~"==title.get_text():
        return False
    name=title.get_text().split("详情介绍-")[0]
    msgs=soup.find_all("div",class_='module-info-item-content')
    leader=msgs[0].get_text()
    actor=msgs[1].get_text()
    state=msgs[3].get_text()
    if not '集' in state and not "版" in state and not "OVA" in state and not "完结" in state and not "话" in state and not '电影' in state:
        try:
           state=msgs[4].get_text()
           actor=msgs[2].get_text()
        except:
            actor = msgs[1].get_text()
            state = msgs[3].get_text()
    img_url=soup.find('img',class_='ls-is-cached lazy lazyload').get('data-original')
    return name,leader,actor,state,img_url

#解析函数
def get_num_id(id:str):
    return id.split('/')[-1].split('.')[0]

#获取更新周期的函数
def get_week_id():
    res=requests.get("https://www.mutean.com/label/week.html")
    soup=BeautifulSoup(res.text,"html.parser")
    back={}
    one_day_data=[]
    count=0
    ac_day=BeautifulSoup(str(soup.find('div',class_='module-main tab-list active')),'html.parser')
    for i in ac_day.find_all('a',class_='module-poster-item module-item'):
        one_day_data.append(get_num_id(i.get('href')))
    back[datetime.datetime.now().weekday()]=one_day_data
    for i in soup.find_all('div',class_="module-main tab-list"):
        one_day_data=[]
        if int(count==datetime.datetime.now().weekday()) and count+1!=7:
            count+=1
        day=BeautifulSoup(str(i),'html.parser')
        for x in day.find_all('a',class_='module-poster-item module-item'):
            one_day_data.append(get_num_id(x.get('href')))
        back[count]=one_day_data
        count+=1
    return back

#存储周期表的函数
def save_data(data):
    s,c=use_sql()
    c.execute('delete from week')
    s.commit()
    for i in range(7):
        for x in data[i]:
            c.execute(f"insert into week values ('{x}','{i}')")
    s.commit()
    s.close()
if __name__ == '__main__':
    type=sys.argv[1]
    position=sys.argv[2]
    database=sys.argv[3]
    username=sys.argv[4]
    password=sys.argv[5]
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', line_buffering=True)
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8', line_buffering=True)
    print("running...")
    try:
        while True:
            id_data = get_week_id()
            save_data(id_data)
            for date in id_data.keys():
                oneDayData=id_data[date]
                for id in oneDayData:
                    data=msg_getter(int(id))
                    write_data(id, data[0], data[1].replace("\n",''), data[2].replace("\n",''), data[3], data[4])
            time.sleep(14400)
    except Exception as e:
        print(e)
