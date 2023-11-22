# docker下安装minio

> 9000端口：是minio api连接用
> 
> 9090端口：是minio console浏览器访问用

```shell
docker run \
-d \
-p 9000:9000 \
-p 9090:9090 \
--name minio \
-v /home/pontus.fan/minio/data:/data \
-e "MINIO_ROOT_USER=admin" \
-e "MINIO_ROOT_PASSWORD=admin" \
quay.io/minio/minio server /data --console-address ":9090"
```