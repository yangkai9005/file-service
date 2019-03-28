# 简单的文件服务器管理系统

## 编写原因
搭建文件服务器时, 文件几乎冗杂, 上传时间太久无用的文件也无人清理

## 项目配置
> * [「yml」](src/main/resources/application-dev.yml)文件中upload域字段含义 <br>

    file-dir     ->  文件存储目录
    tmp-dir      ->  临时文件存储目录
    ups-buffer   ->  上传文件时 内存缓冲 
                     超过改大小的文件将存放在tmp-dir
    get-buffer   ->  视频请求头Range 断点续传时 内存缓冲
    read-bytes   ->  流式下载时 每次写入通道的bytes
    timeout      ->  MD5校验后 凭证的使用有效时间
    file-max     ->  上传文件的最大size
    size-max     ->  请求body最大size

## api展示
> * 文件秒传 MD5校验 [「/md5」](.image/md5.jpg)
> * 上传文件 支持批量 [「/ups」](.image/ups.jpg)
> * 下载文件 单个下载 [「/get」](.image/get.jpg)

## 项目扩展
> * 自定义扩展配置文件与配置信息
> * resize去除图片中的js代码 <br>
   [「resize参考」](https://www.jianshu.com/p/27536926aa2f)
   [「js隐藏于图片」](https://blog.csdn.net/shixing_11/article/details/7072804)

## 适用范围
> 仅适用于中小型文件服务器

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
