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

完成文件分享功能

### 2023.7.10

完成查看分享功能

完成回收站功能

## BUG Fix

- [ ] 上传文件时同名检测并警告

- [ ] index界面，百分比计算和显示

- [X] 添加文件后User数据表countSize不变： <br>
  FileService.getFileName()在判空前操作空指针对象。

- [ ] 分享界面图标未正常显示

- [ ] 分享界面无法浏览文件夹内文件

- [X] layer插件弹窗乱码： <br>
  设置js文件字符集为UTF-8即可

- [X] 分享链接不正确：<br>
  修改index.js下的joinUrl()函数即可

- [ ] 单文件夹下载后，网盘目录下的压缩包未被正确删除

- [X] 查看分享时没有内容:<br>
  修改ShareDao.java接口中的findShareByName()方法，为每个参数加上@Param注释即可

- [X] 已分享的链接无法取消或删除<br>
  修改ShareDao.java接口中cancelShare()方法，为每个参数加上@Param注释即可

- [ ] Flash已被弃用，无法预览视频，需要替代方案

- [X] 回收站单文件删除功能，只有前端没有后端<br>
  在FileController层新增delRecycleDirectory()方法用来接受delRecycle.action请求；<br>
  在FileService层新增delRecycle()方法实现回收站单文件删除。<br>
  调用FileDao层已有方法deleteFile()来操作数据库。<br>

- [ ] 音频播放无法显示长度和调节进度