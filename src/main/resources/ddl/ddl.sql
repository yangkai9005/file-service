create schema filemanager collate utf8mb4_unicode_ci;

create table filesystem
(
  id bigint auto_increment
    primary key,
  savedFileName varchar(64) not null comment '保存的文件名',
  originalFileName varchar(64) not null comment '原始文件名',
  callIp varchar(16) not null comment '调用时 客户端ip',
  number bigint default 0 not null comment '下载次数',
  service bigint not null comment '最后一次使用文件的时间 时间戳',
  serviceDisplay varchar(32) not null comment '最后一次使用文件的时间',
  entry bigint not null comment '文件上传时间 时间戳',
  entryDisplay varchar(32) not null comment '文件上传时间',
  constraint filesystem_savedFileName_uindex
    unique (savedFileName)
)
  comment '文件管理';

create index fileSystem_service_time
  on filesystem (service);

create table whitelist
(
  ext varchar(8) not null comment '文件后缀名'
    primary key,
  fileHeader varchar(8) not null comment '文件头',
  contentType varchar(16) not null comment '文件content-type',
  constraint whitelist_fileHeader_uindex
    unique (fileHeader)
);

