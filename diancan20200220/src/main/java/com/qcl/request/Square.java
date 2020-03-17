package com.qcl.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Square {

    public Integer Numbering; //订单编号
    public String Sorder; //菜名称
    public String Sname;  //姓名
    public BigDecimal Smount; //总金额
    public String Sphone; //电话
    public String Saddress; //地址
    public Integer integral; //积分
    public Integer orderStatus; //订单状态
}
