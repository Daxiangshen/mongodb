#关于Mongodb
>本篇简单介绍Mongodb的安装与踩坑记录，包括图片的上传下载查看以及GFS存储桶大文件的存储，下载，删除
***

##首先是Mongodb的安装（linux）
```shell script
1.下载
wget https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-rhel70-4.0.5.tgz
```
```shell script
2.解压软件包
tar xzvf mongodb-linux-x86_64-rhel70-4.0.5.tgz -C /opt/
```
```shell script
3.将解压后的目录移动到/usr/local下重命名为mongodb
mv mongodb-linux-x86_64-rhel70-4.0.5/ /usr/local/mongodb
```
```shell script
4.创建数据存储目录和日志文件目录
mkdir -p /usr/local/mongodb/data/db
mkdir -p /usr/local/mongodb/logs
```
```shell script
5.编辑mongodb启动配置文件
cd /usr/local/mongodb/bin
vim mongodb.conf

dbpath = /usr/local/mongodb/data/db
logpath = /usr/local/mongodb/logs/mongodb.log
fork = true
auth=true
bind_ip=0.0.0.0
```
```shell script
6.mongodb的可执行文件位于bin目录下,需要将其添加到PATH路径中
vim /etc/profile
export MONGODB_HOME=/usr/local/mongodb
export PATH=$PATH:$MONGODB_HOME/bin   //末尾处添加
source /etc/profile
```
```shell script
7.启动mongodb
cd /usr/local/mongodb/bin
mongod -f mongodb.conf
```
```shell script
8.测试连接
在bin目录下
mangodb
```
##接着是安装完成后。外部系统连接的问题
>如果用RoBo3T或者Navicat连接时,出现Error: Failed to execute "listdatabases" command  
>原因是因为没有指定数据库的登录权限，因为mongodb安装以后只在cmd中进行本地访问，所以要使用图形界面就需要使用登录账号及密码，下面是解决办法

+ 1.开启认证，给MonGo设置一个账号密码
```shell script
./mongod --auth
```
- 2.创建管理员用户,进入bin目录,连接monGo后输入,账号密码自由填写
```shell script
> use admin
switched to db admin
> db.createUser({user:"admin",pwd:"password",roles:["root"]})
Successfully added user: { "user" : "admin", "roles" : [ "root" ] }
```
- 3.认证登录
```shell script
>db.auth("admin", "password")
```
- 4.操作数据库指给owner权限即可,db就是库名,账号密码自由填写
```shell script
> use flowpp
switched to db flowpp
db.createUser({user: "flowpp", pwd: "flopww", roles: [{ role: "dbOwner", db: "flowpp" }]})
```
+ 5.查看系统用户
```shell script
use admin
switched to db admin
> db.system.users.find()
```
##剩下的存储桶存储大文件以及图片存集合东西都在代码内,后续提供图形化界面
