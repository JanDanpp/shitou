package com.qcl.controller;

import com.qcl.api.ResultVO;
import com.qcl.bean.UserInfo;
import com.qcl.bean.WxOrderRoot;
import com.qcl.meiju.OrderStatusEnum;
import com.qcl.meiju.ResultEnum;
import com.qcl.repository.OrderRootRepository;
import com.qcl.response.WxOrderResponse;
import com.qcl.yichang.DianCanException;
import com.qcl.request.UserForm;
import com.qcl.repository.UserRepository;
import com.qcl.utils.ApiUtil;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * 用户相关
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class WxUserController {

    @Autowired
    UserRepository repository;

    @Autowired
    OrderRootRepository orderRootRepository;

    @Autowired
    private WxOrderUtils wxOrder;

    //创建订单
    @PostMapping("/save")
    public ResultVO create(@Valid UserForm userForm,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("参数不正确, userForm={}", userForm);
            throw new DianCanException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        UserInfo userOld = repository.findByOpenid(userForm.getOpenid());
        UserInfo user = new UserInfo();
        if (userOld != null) {
            user.setId(userOld.getId());
        }
        user.setUsername(userForm.getUsername());
        user.setOpenid(userForm.getOpenid());
        user.setPhone(userForm.getPhone());
        user.setZhuohao(userForm.getZhuohao());
        user.setRenshu(userForm.getRenshu());
        user.setAddress(userForm.getAddress());
//        首次用户注册 积分为100 integral
        String integral   = "100";
        if (userOld==null){
            user.setIntegral(100);
        }

        return ApiUtil.success(repository.save(user));
    }

    @GetMapping("/getUserInfo")
    public ResultVO getUserInfo(@RequestParam("openid") String openid) {
        UserInfo user = repository.findByOpenid(openid);
        return ApiUtil.success(user);
    }

    @PostMapping("/updateSquare")
    public Integer squareStatus(@RequestParam("openid") String openid,
                               @RequestParam("numbering") Integer numbering,
                               @RequestParam("orderStatus") Integer orderStatus){
        System.out.println(openid+"----"+numbering+"----"+orderStatus);
            Integer SqStarus=0;
//0"新订单，未支付;1"新订单，已支付",待接单;2, "已取消"；3"待评价"；4“已完成”;6"已经接单"
        WxOrderRoot byId = orderRootRepository.findById(numbering).get();

            if (byId.getOrderStatus() == OrderStatusEnum.NEW_PAYED.getCode()) {
//            如果是1则改为6保存
                byId.setOrderStatus(OrderStatusEnum.SIX.getCode());
                byId.setCommentaa(openid);
                SqStarus =1;
            } else if (byId.getOrderStatus() == OrderStatusEnum.SIX.getCode()) {
//            如果是6则改为4保存
                if (openid.equals(byId.getBuyerOpenid())){
                byId.setOrderStatus(OrderStatusEnum.FINISHED.getCode());
                    SqStarus =2;
                }else{
                SqStarus =3;
                }
            }
        //保存到订单表
        WxOrderRoot updateResult = orderRootRepository.save(byId);
//      1接单成功    2收到外卖  3 不能取消订单 0 违规操作
        return SqStarus;
    }

}
