# CloudHub

QST青软实训

## 测试&调试须知

测试用账号：admin 1234

云端拉到本地调试需修改配置文件：resources\db.properties

## 开发进度

### 2023.7.6

完成了注册功能

### 2023.7.7

完成了登录功能

添加登录拦截器

完成文件/文件夹列表展示

完成文件上传

完成计算文件大小

### 2023.7.8

完成单文件&多文件下载

完成文件分类&查询

## TODO List

- [ ] 上传文件时同名检测并警告

## BUG Fix

- [ ] index界面，百分比计算和显示

- [ ] 分享界面修正

- [X] 添加文件后User数据表countSize不变： <br>
  FileService.countFileSize()在判空前操作空指针对象。

- [ ] 分享页面文件夹只能下载不能查看内容

- [X] layer插件弹窗乱码： <br>
  设置js文件字符集为UTF-8即可

- [X] 分享链接不正确：<br>
  修改index.js下的joinUrl()函数即可