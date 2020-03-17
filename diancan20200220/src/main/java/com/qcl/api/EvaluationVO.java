package com.qcl.api;


import lombok.Data;

/**
 * 剑胆
 * 2020-03-07
 */
@Data
public class EvaluationVO<T> {

    /**   订单号 */
    private  Integer ordernumber;

    /** 具体内容. */
    private T data;

}
