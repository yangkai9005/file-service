# 简单的文件服务器管理系统

## 编写原因
搭建文件服务器时, 文件几乎冗杂, 上传时间太久无用的文件也无人清理

## 项目配置
> * 以文件头判断文件类型 与后缀名无关
> * 文件头可从项目日志中读取 或使用[「Files.getFileHeader()」](src/main/java/org/elsa/filemanager/common/utils/Files.java)
> * [「yml」](src/main/resources/application-dev.yml)文件中config域配置项目基本内容 <br>

    file-dir     ->  文件存储目录
    expired-day  ->  文件过期时间 超过天数文件将被删除

## api展示
> * 上传文件 支持批量 [「/ups」](.image/ups.jpg)
> * 下载文件 单个下载 [「/get」](.image/get.jpg)

## 项目扩展
> * 自定义扩展配置文件与配置信息
> * resize去除图片中的js代码 <br>
   [「resize参考」](https://www.jianshu.com/p/27536926aa2f)
   [「js隐藏于图片」](https://blog.csdn.net/shixing_11/article/details/7072804)

## 适用范围
> 仅适用于中小型文件服务器 <br>
> 大型或分布式文件服务器... (一个人也做不到 hahah..)

## 开源协议
Copyright 2018 valord577

Licensed under the Apache License, Version 2.0 (the "License"); you may
not use this file except in compliance with the License. You may obtain
a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations
under the License.
