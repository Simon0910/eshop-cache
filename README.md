# eshop-cache
多级缓存架构


redis cluster最最基础的一些知识

redis cluster: 自动，master+slave复制和读写分离，master+slave高可用和主备切换，支持多个master的hash slot支持数据分布式存储

停止之前所有的实例，包括redis主从和哨兵集群

1、redis cluster的重要配置

cluster-enabled <yes/no>

cluster-config-file <filename>：这是指定一个文件，供cluster模式下的redis实例将集群状态保存在那里，包括集群中其他机器的信息，比如节点的上线和下限，故障转移，不是我们去维护的，给它指定一个文件，让redis自己去维护的

cluster-node-timeout <milliseconds>：节点存活超时时长，超过一定时长，认为节点宕机，master宕机的话就会触发主备切换，slave宕机就不会提供服务

2、在三台机器上启动6个redis实例

（1）在eshop-cache03上部署目录

/etc/redis（存放redis的配置文件），/var/redis/6379（存放redis的持久化文件）

（2）编写配置文件

redis cluster集群，要求至少3个master，去组成一个高可用，健壮的分布式的集群，每个master都建议至少给一个slave，3个master，3个slave，最少的要求

正式环境下，建议都是说在6台机器上去搭建，至少3台机器

保证，每个master都跟自己的slave不在同一台机器上，如果是6台自然更好，一个master+一个slave就死了

3台机器去搭建6个redis实例的redis cluster

mkdir -p /etc/redis-cluster
mkdir -p /var/log/redis

eshop01:
mkdir -p /var/redis/7001
mkdir -p /var/redis/7002
eshop01:
mkdir -p /var/redis/7003
mkdir -p /var/redis/7004
eshop01:
mkdir -p /var/redis/7005
mkdir -p /var/redis/7006


port 7001
cluster-enabled yes
cluster-config-file /etc/redis-cluster/node-7001.conf
cluster-node-timeout 15000
daemonize	yes							
pidfile		/var/run/redis_7001.pid 						
dir 		/var/redis/7001		
logfile 	/var/log/redis/7001.log
bind 192.168.31.187		
appendonly yes

将每个配置文件中的slaveof给删除

至少要用3个master节点启动，每个master加一个slave节点，先选择6个节点，启动6个实例

将上面的配置文件，在/etc/redis下放6个，分别为: 7001.conf，7002.conf，7003.conf，7004.conf，7005.conf，7006.conf

（3）准备生产环境的启动脚本

在/etc/init.d下，放6个启动脚本，分别为: redis_7001, redis_7002, redis_7003, redis_7004, redis_7005, redis_7006

每个启动脚本内，都修改对应的端口号

（4）分别在3台机器上，启动6个redis实例

将每个配置文件中的slaveof给删除

3、创建集群

下面方框内的内容废弃掉

=======================================================================

wget https://cache.ruby-lang.org/pub/ruby/2.3/ruby-2.3.1.tar.gz
tar -zxvf ruby-2.3.1.tar.gz
./configure -prefix=/usr/local/ruby
make && make install
cd /usr/local/ruby
cp bin/ruby /usr/local/bin
cp bin/gem /usr/local/bin

wget http://rubygems.org/downloads/redis-3.3.0.gem
gem install -l ./redis-3.3.0.gem
gem list --check redis gem

=======================================================================

因为，以前比如公司里面搭建集群，公司里的机器的环境，运维会帮你做好很多事情

在讲课的话，我们手工用从零开始装的linux虚拟机去搭建，那肯定会碰到各种各样的问题

yum install -y ruby
yum install -y rubygems
gem install redis

cp /usr/local/redis-3.2.8/src/redis-trib.rb /usr/local/bin

redis-trib.rb create --replicas 1 192.168.31.187:7001 192.168.31.187:7002 192.168.31.19:7003 192.168.31.19:7004 192.168.31.227:7005 192.168.31.227:7006

--replicas: 每个master有几个slave

6台机器，3个master，3个slave，尽量自己让master和slave不在一台机器上

yes

redis-trib.rb check 192.168.31.187:7001

4、读写分离+高可用+多master

读写分离：每个master都有一个slave
高可用：master宕机，slave自动被切换过去
多master：横向扩容支持更大数据量


=======================================================================

1、zookeeper集群搭建

将课程提供的zookeeper-3.4.5.tar.gz使用WinSCP拷贝到/usr/local目录下。
对zookeeper-3.4.5.tar.gz进行解压缩：tar -zxvf zookeeper-3.4.5.tar.gz。
对zookeeper目录进行重命名：mv zookeeper-3.4.5 zk

配置zookeeper相关的环境变量
vi ~/.bashrc
export ZOOKEEPER_HOME=/usr/local/zk
export PATH=$ZOOKEEPER_HOME/bin
source ~/.bashrc

cd zk/conf
cp zoo_sample.cfg zoo.cfg

vi zoo.cfg
修改：dataDir=/usr/local/zk/data
新增：
server.0=eshop-cache01:2888:3888	
server.1=eshop-cache02:2888:3888
server.2=eshop-cache03:2888:3888

cd zk
mkdir data
cd data

vi myid
0

在另外两个节点上按照上述步骤配置ZooKeeper，使用scp将zk和.bashrc拷贝到eshop-cache02和eshop-cache03上即可。唯一的区别是标识号分别设置为1和2。

分别在三台机器上执行：zkServer.sh start。
检查ZooKeeper状态：zkServer.sh status，应该是一个leader，两个follower
jps：检查三个节点是否都有QuromPeerMain进程

2、kafka集群搭建

scala，我就不想多说了，就是一门编程语言，现在比较火，很多比如大数据领域里面的spark（计算引擎）就是用scala编写的

将课程提供的scala-2.11.4.tgz使用WinSCP拷贝到/usr/local目录下。
对scala-2.11.4.tgz进行解压缩：tar -zxvf scala-2.11.4.tgz。
对scala目录进行重命名：mv scala-2.11.4 scala

配置scala相关的环境变量
vi ~/.bashrc
export SCALA_HOME=/usr/local/scala
export PATH=$SCALA_HOME/bin
source ~/.bashrc

查看scala是否安装成功：scala -version

按照上述步骤在其他机器上都安装好scala。使用scp将scala和.bashrc拷贝到另外两台机器上即可。

将课程提供的kafka_2.9.2-0.8.1.tgz使用WinSCP拷贝到/usr/local目录下。
对kafka_2.9.2-0.8.1.tgz进行解压缩：tar -zxvf kafka_2.9.2-0.8.1.tgz。
对kafka目录进行改名：mv kafka_2.9.2-0.8.1 kafka

配置kafka
vi /usr/local/kafka/config/server.properties
broker.id：依次增长的整数，0、1、2，集群中Broker的唯一id
zookeeper.connect=192.168.31.187:2181,192.168.31.19:2181,192.168.31.227:2181

安装slf4j
将课程提供的slf4j-1.7.6.zip上传到/usr/local目录下
unzip slf4j-1.7.6.zip
把slf4j中的slf4j-nop-1.7.6.jar复制到kafka的libs目录下面

解决kafka Unrecognized VM option 'UseCompressedOops'问题

vi /usr/local/kafka/bin/kafka-run-class.sh 

if [ -z "$KAFKA_JVM_PERFORMANCE_OPTS" ]; then
  KAFKA_JVM_PERFORMANCE_OPTS="-server  -XX:+UseCompressedOops -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:+CMSScavengeBeforeRemark -XX:+DisableExplicitGC -Djava.awt.headless=true"
fi

去掉-XX:+UseCompressedOops即可

按照上述步骤在另外两台机器分别安装kafka。用scp把kafka拷贝到其他机器即可。
唯一区别的，就是server.properties中的broker.id，要设置为1和2

在三台机器上的kafka目录下，分别执行以下命令：nohup bin/kafka-server-start.sh config/server.properties &

使用jps检查启动是否成功

使用基本命令检查kafka是否搭建成功

bin/kafka-topics.sh --zookeeper 192.168.198.130:2181,192.168.198.131:2181,192.168.198.132:2181 --topic cache-message --replication-factor 1 --partitions 1 --create

bin/kafka-console-producer.sh --broker-list 192.168.198.130:9092,192.168.198.131:9092,192.168.198.132:9092 --topic cache-message

bin/kafka-console-consumer.sh --zookeeper 192.168.198.130:2181,192.168.198.131:2181,192.168.198.132:2181 --topic cache-message --from-beginning

{"serviceId":"shopInfoService","productId":1,"shopId":1}

{"serviceId":"productInfoService","productId":1,"shopId":1}

=============================================================

1、部署第一个nginx，作为应用层nginx（192.168.31.187那个机器上）

（1）部署openresty

mkdir -p /usr/servers  
cd /usr/servers/

yum install -y readline-devel pcre-devel openssl-devel gcc

wget http://openresty.org/download/openresty-1.15.8.2.tar.gz
tar -xzvf openresty-1.15.8.2.tar.gz
cd /usr/servers/openresty-1.15.8.2/

cd bundle/LuaJIT-2.1-20150120/  
make clean && make && make install  
ln -sf luajit-2.1.0-alpha /usr/local/bin/luajit

cd bundle  
wget https://github.com/FRiCKLE/ngx_cache_purge/archive/2.3.tar.gz  
tar -xvf 2.3.tar.gz  

cd bundle  
wget https://github.com/yaoweibin/nginx_upstream_check_module/archive/v0.3.0.tar.gz  
tar -xvf v0.3.0.tar.gz  

cd /usr/servers/openresty-1.15.8.2
./configure --prefix=/usr/servers --with-http_realip_module  --with-pcre  --with-luajit --add-module=./bundle/ngx_cache_purge-2.3/ --add-module=./bundle/nginx_upstream_check_module-0.3.0/ -j2  
make && make install 

cd /usr/servers/  
ll

/usr/servers/luajit
/usr/servers/lualib
/usr/servers/nginx
/usr/servers/nginx/sbin/nginx -V 

启动nginx: /usr/servers/nginx/sbin/nginx

（2）nginx+lua开发的hello world

vi /usr/servers/nginx/conf/nginx.conf

在http部分添加：

lua_package_path "/usr/servers/lualib/?.lua;;";  
lua_package_cpath "/usr/servers/lualib/?.so;;";  

/usr/servers/nginx/conf下，创建一个lua.conf

server {  
    listen       80;  
    server_name  _;  
}  

在nginx.conf的http部分添加：

include lua.conf;

验证配置是否正确：

/usr/servers/nginx/sbin/nginx -t

在lua.conf的server部分添加：

location /lua {  
    default_type 'text/html';  
    content_by_lua 'ngx.say("hello world")';  
} 

/usr/servers/nginx/sbin/nginx -t  

重新nginx加载配置

/usr/servers/nginx/sbin/nginx -s reload  

访问http: http://192.168.31.187/lua

vi /usr/servers/nginx/conf/lua/test.lua

ngx.say("hello world"); 

修改lua.conf

location /lua {  
    default_type 'text/html';  
    content_by_lua_file conf/lua/test.lua; 
}

查看异常日志

tail -f /usr/servers/nginx/logs/error.log

（3）工程化的nginx+lua项目结构

项目工程结构

hello
    hello.conf     
    lua              
      hello.lua
    lualib            
      *.lua
      *.so

放在/usr/hello目录下

/usr/servers/nginx/conf/nginx.conf

worker_processes  2;  

error_log  logs/error.log;  

events {  
    worker_connections  1024;  
}  

http {  
    include       mime.types;  
    default_type  text/html;  
  
    lua_package_path "/usr/hello/lualib/?.lua;;";  
    lua_package_cpath "/usr/hello/lualib/?.so;;"; 
    include /usr/hello/hello.conf;  
}  

/usr/hello/hello.conf

server {  
    listen       80;  
    server_name  _;  
  
    location /lua {  
        default_type 'text/html';  
        # lua_code_cache off;  
        content_by_lua_file /usr/example/lua/test.lua;  
    }  
}  

=============================================

在nginx这一层，接收到访问请求的时候，就把请求的流量上报发送给kafka

这样的话，storm才能去消费kafka中的实时的访问日志，然后去进行缓存热数据的统计

用得技术方案非常简单，从lua脚本直接创建一个kafka producer，发送数据到kafka

wget https://github.com/doujiang24/lua-resty-kafka/archive/master.zip

yum install -y unzip

unzip lua-resty-kafka-master.zip

mv /usr/local/lua-resty-kafka-master/lib/resty/kafka /usr/eshop/hello/lualib/resty

nginx -s reload

local cjson = require("cjson")  
local producer = require("resty.kafka.producer")  

local broker_list = {  
    { host = "192.168.198.130", port = 9092 },  
    { host = "192.168.198.131", port = 9092 },  
    { host = "192.168.198.132", port = 9092 }
}

local log_json = {}  
log_json["headers"] = ngx.req.get_headers()  
log_json["uri_args"] = ngx.req.get_uri_args()  
log_json["body"] = ngx.req.read_body()  
log_json["http_version"] = ngx.req.http_version()  
log_json["method"] =ngx.req.get_method() 
log_json["raw_reader"] = ngx.req.raw_header()  
log_json["body_data"] = ngx.req.get_body_data()  

local message = cjson.encode(log_json);  

local productId = ngx.req.get_uri_args()["productId"]

local async_producer = producer:new(broker_list, { producer_type = "async" })   
local ok, err = async_producer:send("access-log", productId, message)  

if not ok then  
    ngx.log(ngx.ERR, "kafka send err:", err)  
    return  
end

两台机器上都这样做，才能统一上报流量到kafka


bin/kafka-topics.sh --zookeeper 192.168.198.130:2181,192.168.198.131:2181,192.168.198.132:2181 --topic access-log --replication-factor 1 --partitions 1 --create

bin/kafka-console-producer.sh --broker-list 192.168.198.130:9092,192.168.198.131:9092,192.168.198.132:9092 --topic access-log

bin/kafka-console-consumer.sh --zookeeper 192.168.198.130:2181,192.168.198.131:2181,192.168.198.132:2181 --topic access-log --from-beginning


（1）kafka在187上的节点死掉了，可能是虚拟机的问题，杀掉进程，重新启动一下

nohup bin/kafka-server-start.sh config/server.properties &

（2）需要在nginx.conf中，http部分，加入resolver 8.8.8.8;

（3）需要在kafka中加入advertised.host.name = 192.168.31.187，重启三个kafka进程

（4）需要启动eshop-cache缓存服务，因为nginx中的本地缓存可能不在了


六、部署一个storm集群

（1）安装Java 7和Python 2.6.6

（2）下载storm安装包，解压缩，重命名，配置环境变量

~/.bashrc
source ~/.bashrc

（3）修改storm配置文件

mkdir /var/storm

conf/storm.yaml

storm.zookeeper.servers:
  - "192.168.198.130"
  - "192.168.198.131"
  - "192.168.198.132"

nimbus.seeds: ["192.168.198.130"]

storm.local.dir: "/var/storm"


slots.ports，指定每个机器上可以启动多少个worker，一个端口号代表一个worker

supervisor.slots.ports:
    - 6700
    - 6701
    - 6702
    - 6703

(4)启动storm集群和ui界面

一个节点，storm nimbus >/dev/null 2>&1 &
三个节点，storm supervisor >/dev/null 2>&1 &
一个节点，storm ui >/dev/null 2>&1 &

需要在两个supervisor节点上, 启动logviewer, 然后才能看到日志, storm logviewer >/dev/null 2>&1 &

（5）访问一下ui界面，8080端口

storm jar /usr/local/eshop-storm-0.0.1-SNAPSHOT.jar com.roncoo.eshop.storm.HotProductTopology HotProductTopology

storm kill HotProductTopology
````