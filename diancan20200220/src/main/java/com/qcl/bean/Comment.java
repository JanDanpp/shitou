package com.qcl.bean;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Created by qcl on 2019-03-31
 * 微信：2501902696
 * desc:
 */
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
//@DynamicUpdate
//@DynamicInsert
public class Comment {

    @Id
    @GeneratedValue
    private int commentId;
    private String openid;
    private String name;
    private String avatarUrl;//头像
    private String content;
    private String evaluation; //对送餐者的评价
//    private String openidas; //评论者的openid
    private Integer rootId;  //订单的id

    @CreatedDate//自动添加创建时间的注解
    private Date createTime;


}
