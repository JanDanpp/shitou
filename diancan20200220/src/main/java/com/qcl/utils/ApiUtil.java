package com.qcl.utils;

import com.qcl.api.EvaluationVO;
import com.qcl.api.ResultVO;

/**
 * 返回的json数据的工具类
 * 编程小石头：2501902696（微信）
 */
public class ApiUtil {

    public static ResultVO success(Object object) {
        ResultVO resultVO = new ResultVO();
        resultVO.setData(object);
        resultVO.setCode(0);
        resultVO.setMsg("成功");
        return resultVO;
    }

    public static ResultVO success() {
        return success(null);
    }

    public static ResultVO error(Integer code, String msg) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(code);
        resultVO.setMsg(msg);
        return resultVO;
    }

    public static EvaluationVO evaluationVO(Object object,Integer rootId){
        EvaluationVO evaluationVO = new EvaluationVO();
        evaluationVO.setOrdernumber(rootId);
        evaluationVO.setData(object);
        return evaluationVO;
    }
}
