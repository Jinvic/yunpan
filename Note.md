**return "redirect:/index.action";**

页面跳转有两种方式

* 服务器转发：forward:index.action
* 客户端重定向：redirect:index.action

区别：

转发，只有一次请求，是服务端行为，可以共用request对象的数据

转发，重新发起请求，是客户端行为，无法共用request对象的数据