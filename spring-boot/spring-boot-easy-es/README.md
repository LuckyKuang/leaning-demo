# Easy-ES 使用详解

## id字段设置方式

```java
/**
 * 推荐方式1: es中的唯一id,不加任何注解或@IndexId(type=IdType.NONE) 此时id值将由es自动生成
 */
private String id;

/**
 * 不推荐方式2:如果你想自定义es中的id为你提供的id,比如MySQL中的id,请将注解中的type指定为customize或直接在全局配置文件中指定,此时你便可以在插入数据时赋值给id
 */
@IndexId(type = IdType.CUSTOMIZE)
private Long id;

/**
 * 推荐方式3:如果你确实有需求用到其它数据库中的id,不妨在加了推荐方式1中的id后,再加一个字段类型为keyword的列,用来存储其它数据库中的id
 */
@IndexField(fieldType = FieldType.KEYWORD)
private Long mysqlId;
```

## 安装Kibana

```shell
# 拉取镜像
docker pull kibana:7.17.23

# 运行容器
docker run --name kibana -e ELASTICSEARCH_HOSTS=http://1.12.248.106:9200 -p 5601:5601 \
-d kibana:7.17.23
```

## 安装ElasticSearch

```shell
# 拉取镜像
docker pull elasticsearch:7.17.23

# 运行容器
docker run --name elasticsearch -p 9200:9200 -p 9300:9300 \
-e "discovery.type=single-node" \
-e "ES_JAVA_OPTS=-Xms512m -Xmx512m" \
-d elasticsearch:7.17.23

# 或者使用持久化(需要自行配置)
docker run --name elasticsearch -p 9200:9200  -p 9300:9300 \
-e "discovery.type=single-node" \
-e "ES_JAVA_OPTS=-Xms512m -Xmx512m" \
-v /home/pontus.fan/es/conf/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
-v /home/pontus.fan/es/data:/usr/share/elasticsearch/data \
-v /home/pontus.fan/es/plugins:/usr/share/elasticsearch/plugins \
-d elasticsearch:7.17.23
```