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
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 编程小石头：2501902696（微信）
 * 用户信息表
 */
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserInfo {

    @Id
    @GeneratedValue
    private int id;
    private String username;
    private String phone;
    private String openid;
    private String zhuohao;//桌号
    private String renshu;//用餐人数
    private Integer integral;//积分
    private String address;//地址

    @CreatedDate//自动添加创建时间的注解
    private Date createTime;
    @LastModifiedDate//自动添加更新时间的注解
    private Date updateTime;
}
