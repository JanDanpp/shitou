package com.qcl.request;

import lombok.Data;

/**
 * 编程小石头：2501902696（微信）
 */
@Data
public class AdminForm {

    private String username;
    private String password;
    private String phone;
    private Integer adminId;
    private Integer adminType;
}
