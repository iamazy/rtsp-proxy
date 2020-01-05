### 简易rtsp代理

### 请求方式
>[POST] http://localhost:8686/stream/proxy
```json
{
	"url":"rtsp://x.x.x.x:554/xxx",
	"username":"admin",
	"password":"12345"
}
```
>返回值
```json
{
    "streamId": "5deef6126423ad3cc8ce112e",
    "url": "rtsp://localhost:53882/5deef6126423ad3cc8ce112e",
}
```