'''
@author:Conyafer
@Time:2024-8-5
Get the animation data from the Internet
'''
import time
from tqdm import tqdm
import requests
from bs4 import BeautifulSoup
import sqlite3 as SQL
import datetime

def use_sql():
    s=SQL.connect('test.db')
    c=s.cursor()
    return s,c
def write_data(id,name,leader,actor,state,img_url):
    try:
        s,c=use_sql()
        c.execute(f'INSERT INTO film_data (id,name,state,leader,actor,img_url,agree) VALUES ({id},"{name}","{state}","{leader}","{actor}","{img_url}",0) ON CONFLICT (id) DO UPDATE SET state = "{state}";')
        s.commit()
        c.close()
        s.close()
    except Exception as e:
        print(e)
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
def get_all_updata():
    s,c=use_sql()
    c.execute("select id from week")
    data=c.fetchall()
    return data
def get_num_id(id:str):
    return id.split('/')[-1].split('.')[0]
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
def save_data(data):
    conn=SQL.connect('test.db')
    c=conn.cursor()
    c.execute('delete from week')
    conn.commit()
    for i in range(7):
        for x in data[i]:
            c.execute(f"insert into week values ('{x}','{i}')")
    conn.commit()
    conn.close()
if __name__ == '__main__':
    data = get_week_id()
    save_data(data)
    print("Now Updatting:\n", data)
    id_data=get_all_updata()
    while True:
        with tqdm(total=len(id_data), desc="Getting...", unit="it",initial=0, ascii=True,nrows=80) as pbar:
            for id in id_data:
                data=msg_getter(int(id[0]))
                if data:
                    pbar.update(1)
                    pbar.set_postfix({'msg':f"Updatting id:{id[0]}|Name:{data[0]}"})  # 进度条前加内容
                    write_data(id[0], data[0], data[1], data[2], data[3], data[4])
                else:
                    pbar.set_postfix({"id": id[0],'msg':'No Data'})  # 进度条前加内容
                    pbar.update(1)
                    continue
        time.sleep(14400)
