create table gq_lecture_info(
                                id varchar(255) primary key not null comment '主键id,后端代码生成',
                                create_time datetime not null comment '创建时间',
                                update_time datetime not null comment  '更新时间',
                                delete_time datetime default null comment '删除时间',
                                is_deleted tinyint(1) default 0 comment '删除为1,否则为0',
                                version  int default 0 comment  '乐观锁',
                                official_account_url varchar(100) not null comment '微信公众号地址',
                                term int not null comment '第几期',
                                lecture_name varchar(40)not null comment '讲座主题或者名称',
                                image varchar(100) default null comment '讲座图片路径',
                                guest_name varchar(20) not null comment '嘉宾名称',
                                place varchar(100) not null comment '讲座地点',
                                start_time datetime not null  comment '讲座开始时间',
                                end_time datetime not null  comment '讲座结束时间',
                                grab_tickets_start datetime not null comment '抢票开始时间',
                                grab_tickets_end datetime not null comment  '抢票截止时间',
                                ticket_number int not null comment '票数量',
                                introduction varchar(100) default null comment '讲座导语',
                                review_name varchar(40) default null comment '讲座回顾主题',
                                review_url varchar(100) default null comment '讲座回顾图片路径',
                                review_official_account_url varchar(100) comment '微信公众号链接'
)comment '讲座信息表';

create table  gq_user_info(
                              uuid varchar(255)  primary key  not null comment  '主键id',
                              openid varchar(100) default null unique comment '微信的openid，识别用户',
                              create_time datetime not null comment '创建时间',
                              update_time datetime not null   comment  '更新时间',
                              delete_time datetime  default  null comment '删除时间',
                              is_deleted tinyint(1) default 0 comment '删除为1,否则为0',
                              version  int default 0 comment '乐观锁',
                              user_name varchar(20) unique default  null comment '管理员账号',
                              password varchar(100) default  null comment '管理员密码',
                              name varchar(10) default  null comment '姓名',
                              sex tinyint(1) default 0 comment '性别 0-男，1-女',
                              college varchar(50) default  null comment '学院',
                              student_id varchar(50) unique default  null comment '学号',
                              natural_class varchar(50) default  null comment '班级',
                              user_identity tinyint(1) default null comment '用户身份，0学生 1老师',
                              vip tinyint(1) default 0 comment '普通用户或者vip用户,0-普通，1-vip',
                              race_number  int default 0 comment '参加讲座次数',
                              permission varchar(10) default 'user' comment '用户权限  user 微信端用户  admin 管理员后台的用户'
)comment '用户表';

create  table gq_user_lecture_info(
                                      id varchar(255) primary key not null  comment '主键id',
                                      lecture_id varchar(255) not null  comment '对应的讲座的主键',
                                      openid varchar(100) not null   comment '对应用户的openid',
                                      status tinyint(1) default  0 comment '是否观看讲座 0-抢到票 1-只签 2-只签退  3-签到和签退' ,
                                      create_time datetime not null comment '创建时间',
                                      update_time datetime not null  null comment  '更新用户登录时间',
                                      is_deleted tinyint(1) default 0 comment '删除为1,否则为0',
                                      index (openid),index (lecture_id),
                                      index (lecture_id,openid)
)comment '讲座用户信息关联表';

create table gq_poster_tweet_info(
                                     id varchar(256) primary key not null  comment '主键',
                                     title varchar(20) default null comment '标题',
                                     image varchar(100) not null  comment '图片地址',
                                     official_account_url varchar(100) not null comment '微信公众号地址',
                                     type int not null comment '0-资讯类型 1-招新',
                                     create_time datetime not null comment '创建时间',
                                     update_time datetime not null comment  '更新时间',
                                     delete_time datetime default null comment '删除时间',
                                     is_deleted tinyint(1) default 0 comment '删除为1,否则为0',
                                     version  int default  0 comment '乐观锁'
)comment '海报推文';

create table  gq_curriculum_vitae_info(
                                          id varchar(256) primary key not null comment '主键',
                                          openid varchar(256) not null unique comment '用户的openid',
                                          create_time datetime not null comment '创建时间',
                                          update_time datetime not null comment  '更新时间',
                                          delete_time datetime default null comment '删除时间',
                                          is_deleted tinyint(1) default 0 comment '删除为1,否则为0',
                                          department_id varchar(256) default 0 comment '组id',
                                          position_id varchar(256) not null comment '职位id',
                                          is_adjust tinyint(1) default 0 comment '0-不接受调剂 1-接受',
                                          campus tinyint(1) default 0 comment '校区 0-松山湖  1-莞城',
                                          phone varchar(30) default null comment '手机号',
                                          wechat varchar(30) default null comment '微信号',
                                          file_url varchar(256) default null comment '简历地址',
                                          term int not null comment '第几期新人',
                                          index (department_id,position_id)
)comment '招新信息表';

create  table  gq_department_info(
                                     id varchar(256) primary key not null comment '主键',
                                     create_time datetime not null comment '创建时间',
                                     update_time datetime not null comment  '更新时间',
                                     delete_time datetime default null comment '删除时间',
                                     is_deleted tinyint(1) default 0 comment '删除为1,否则为0',
                                     version int  default  0 comment '版本号',
                                     department_name varchar(30) not null comment '组名'
)comment '各组表';

create  table gq_position_info(
                                  id varchar(256) primary key not null comment '主键',
                                  create_time datetime not null comment '创建时间',
                                  update_time datetime not null comment  '更新时间',
                                  delete_time datetime default null comment '删除时间',
                                  is_deleted tinyint(1) default 0 comment '删除为1,否则为0',
                                  version int  default  0 comment '版本号',
                                  position_name varchar(30) not null comment '职位名',
                                  department_id varchar(256) not null comment '组id'
)comment '职位表';

create  table  gq_ticket_user_info(
                                      id varchar(256) primary key  not null  comment '主键',
                                      ticket_id varchar(256) not null comment '票号',
                                      student_id varchar(256) not null  comment '学号',
                                      create_time datetime not null comment '创建时间',
                                      update_time datetime not null comment  '更新时间',
                                      is_deleted tinyint(1) default 0 comment '删除为1,否则为0',
                                      index (ticket_id,student_id)
)comment '线下用户讲座绑定表';