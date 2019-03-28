create schema fileService collate utf8mb4_unicode_ci;

create table filesystem
(
  id int auto_increment
    primary key,
  fileMd5 varchar(32) not null comment '文件md5',
  savedFileName varchar(64) not null comment '保存的文件名',
  originalFileName varchar(64) not null comment '原始文件名',
  entry bigint not null comment '文件上传时间 时间戳',
  entryDisplay varchar(32) not null comment '文件上传时间',
  size bigint default 0 not null,
  number int default 0 not null comment '下载次数',
  service bigint not null comment '最后一次使用文件的时间 时间戳',
  serviceDisplay varchar(32) not null comment '最后一次使用文件的时间',
  constraint filesystem_fileMd5_uindex
    unique (fileMd5),
  constraint filesystem_savedFileName_uindex
    unique (savedFileName)
)
  comment '文件管理';

create table md5checked
(
  id int auto_increment
    primary key,
  fileMd5 varchar(32) not null comment '文件md5',
  savedFilename varchar(64) not null comment 'oss文件名',
  ossUrl varchar(256) not null comment 'oss链接',
  originalFilename varchar(64) not null comment '源文件名',
  entry bigint not null comment '文件上传时间 时间戳',
  entryDisplay varchar(32) not null comment '文件上传时间',
  constraint md5checked_fileMd5_uindex
    unique (fileMd5)
);

create table uploadcert
(
  id int auto_increment
    primary key,
  certificate varchar(32) not null comment '上传文件需要的凭证',
  used bit not null comment '凭证是否使用 true 使用 | false 未使用',
  time bigint not null comment '凭证颁发的时间戳',
  timeDisplay varchar(32) not null comment '凭证颁发的时间',
  constraint uploadcert_certificate_uindex
    unique (certificate)
);

create table whitelist
(
  ext varchar(8) not null comment '文件后缀名',
  contentType varchar(16) not null comment '文件content-type',
  `range` bit default b'0' not null comment '是否需要断点续传',
  constraint whitelist_contentType_uindex
    unique (contentType)
);

