package com.qcl.controller;

import com.qcl.api.EvaluationVO;
import com.qcl.api.ResultVO;
import com.qcl.bean.Comment;
import com.qcl.bean.UserInfo;
import com.qcl.bean.WxOrderDetail;
import com.qcl.bean.WxOrderRoot;
import com.qcl.repository.OrderDetailRepository;
import com.qcl.repository.UserRepository;
import com.qcl.request.Square;
import com.qcl.response.WxOrderResponse;
import com.qcl.meiju.OrderStatusEnum;
import com.qcl.meiju.ResultEnum;
import com.qcl.yichang.DianCanException;
import com.qcl.repository.CommentRepository;
import com.qcl.repository.OrderRootRepository;
import com.qcl.utils.ApiUtil;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * desc:评论相关
 */
@RestController
public class CommentController {

    @Autowired
    private CommentRepository repository;

    @Autowired
    UserRepository userRepository;


    @Autowired
    private OrderRootRepository orderRootRepository;

    @Autowired
    private WxOrderUtils wxOrder;

    @Autowired
    private OrderRootRepository masterRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    //订单详情
    @PostMapping("/comment")
    public ResultVO<Comment> detail(@RequestParam("openid") String openid,
                                    @RequestParam("orderId") int orderId,
                                    @RequestParam("name") String name,
                                    @RequestParam("avatarUrl") String avatarUrl,
                                    @RequestParam("content") String content,
                                    @RequestParam("evaluation") String evaluation,
                                    @RequestParam("commentas") String commentas,
                                    @RequestParam("openidas") String openidsa){
        if (StringUtils.isEmpty(openid) || StringUtils.isEmpty(orderId)) {
            throw new DianCanException(ResultEnum.PARAM_ERROR);
        }
        //提交评论
        Comment comment = new Comment();
        comment.setName(name);
        comment.setAvatarUrl(avatarUrl);
        comment.setOpenid(openid);
        comment.setContent(content);
//      判断用户信用积分若大于100则不加分。若小于80则不显示订单广场 传过来的openid为当前A用户的给下单是B用户这里注意
        UserInfo userOld = userRepository.findByOpenid(openid);

        if (evaluation.equals("好评加3分")){
            if (userOld.getIntegral()>100){
                //不用处理
            }else {
////                积分加3
                userOld.setIntegral(userOld.getIntegral() + 3);
                }
        }
        else {
//            差评减5分
            userOld.setIntegral(userOld.getIntegral() - 5);
        }
        //将积分数据更新到数据库
        userRepository.save(userOld);
        comment.setEvaluation(commentas);
//        comment.setOpenidas(openidsa);
        comment.setRootId(orderId);
        Comment save = repository.save(comment);

        //修改订单状态
        WxOrderResponse orderDTO = wxOrder.findOne(orderId);
        orderDTO.setOrderStatus(OrderStatusEnum.COMMENT.getCode());
        WxOrderRoot orderMaster = new WxOrderRoot();
        BeanUtils.copyProperties(orderDTO, orderMaster);
        WxOrderRoot updateResult = masterRepository.save(orderMaster);

        return ApiUtil.success(save);
    }

    //所有评论
    @GetMapping("/commentList")
    public ResultVO<List<Comment>> commentList() {
        List<Comment> all = repository.findAll();
        return ApiUtil.success(all);
    }

    //单个用户的所有评论
    @GetMapping("/userCommentList")
    public ResultVO<List<Comment>> userCommentList(@RequestParam("openid") String openid) {
        List<Comment> all = repository.findAllByOpenid(openid);
        return ApiUtil.success(all);
    }
//

    //我的积分
    @GetMapping("/userintegral")
    public UserInfo userIntegral(@RequestParam("openid") String openid) {
        UserInfo userInfo = new UserInfo();
        userInfo = userRepository.findByOpenid(openid);
        return userInfo;
    }


    @RequestMapping("/userEvaluation")
    public List<EvaluationVO> receiveReviews(@RequestParam("openid") String openid){
        List<WxOrderRoot> byBuyerOpenid = orderRootRepository.findByBuyerOpenid(openid);
        Comment byRootId =new Comment();
        List<EvaluationVO> evaluationVOList = new ArrayList<>();
        System.out.println(byBuyerOpenid);
        for (int i=0; i<byBuyerOpenid.size();i++){
            if (i==0) {
                for (int y = 0; y < byBuyerOpenid.size(); y++) {
                    EvaluationVO<Object> objectEvaluationVO = new EvaluationVO<>();
                    evaluationVOList.add(objectEvaluationVO);
                }
            }
            byRootId = repository.findByRootId(byBuyerOpenid.get(i).getOrderId());
            evaluationVOList.get(i).setOrdernumber(byBuyerOpenid.get(i).getOrderId());
            evaluationVOList.get(i).setData(byRootId);
        }
        return evaluationVOList;
    }
    //我收到的评价
    @RequestMapping("wcommentList")
    public List<Comment> wcommentList(@RequestParam("openid") String openid){
        List<Comment> commentList = new ArrayList<>();
        Comment byRootId =new Comment();
        //根据订查询
        List<WxOrderRoot> byBuyerOpenid = orderRootRepository.findByCommentaa(openid);
        for (int i =0;i<byBuyerOpenid.size();i++){
            if (i==0) {
                for (int y = 0; y < byBuyerOpenid.size(); y++) {
                    Comment comment = new Comment();
                    commentList.add(comment);
                }
            }
            byRootId = repository.findByRootId(byBuyerOpenid.get(i).getOrderId());
            //写入订单编号
            commentList.get(i).setRootId(byRootId.getRootId());
            //写入评论语
            commentList.get(i).setEvaluation(byRootId.getEvaluation());

        }

        System.out.println(byBuyerOpenid);


        return commentList;
    }

    @ResponseBody
    @GetMapping("/Wlist")
    public List<WxOrderRoot> wList(){
        List<WxOrderRoot> all = masterRepository.findAll();
        return all;
    }

    @ResponseBody
    @RequestMapping("/LsquareList")
    public List<Square> squareList(){
        List<WxOrderRoot> all = orderRootRepository.findBySquare();
        System.out.println("squareList------"+all+"------square");
        return squares(all);
    }

    //订单广场
    //0"新订单，未支付;1"新订单，已支付",待接单;2, "已取消"；3"待评价"；4“已完成”;6"已经接单"
    //buyerfoodList
    public List<Square> squares(List<WxOrderRoot> wxOrderRootList){
        List<Square> squareList = new ArrayList<>();
        UserInfo userInfo = new UserInfo();
        for (int i = 0;i<wxOrderRootList.size();i++){
            System.out.println("第"+i+"次执行");
            if (i==0){
                //第一次初始化集合squareList
                for (int y = 0; y<wxOrderRootList.size();y++) {
                    Square square = new Square();
                    squareList.add(square);
                }
            }
//        赋值
            squareList.get(i).setNumbering(wxOrderRootList.get(i).getOrderId());
            squareList.get(i).setSorder(this.roderRootToDetail(wxOrderRootList.get(i).getOrderId()));
            squareList.get(i).setSname(wxOrderRootList.get(i).getBuyerName());
            squareList.get(i).setSmount(this.getAmount(wxOrderRootList.get(i).getOrderId()));
            squareList.get(i).setSphone(wxOrderRootList.get(i).getBuyerPhone());
            squareList.get(i).setSaddress(this.getAddtess(wxOrderRootList.get(i).getBuyerOpenid()));
            squareList.get(i).setIntegral(this.getIntegral(wxOrderRootList.get(i).getBuyerOpenid()));
            squareList.get(i).setOrderStatus(wxOrderRootList.get(i).getOrderStatus());
        }

        System.out.println(squareList);
        return squareList;
    }

    //获取菜名
    public String roderRootToDetail(Integer rootId){
        //正则表达式去除前后多余的逗号
        String regex = "^,*|,*$";
        String Sorder = "";
        String foodname = null;
        List<WxOrderDetail> wxOrderDetailList = new ArrayList<>();
        wxOrderDetailList=orderDetailRepository.findByOrderId(rootId);
        for (int i=0;i<wxOrderDetailList.size();i++){
            foodname = wxOrderDetailList.get(i).getFoodName();
         Sorder = Sorder+","+foodname;
        }
        String str1 = Sorder.replaceAll(regex, "");
       return str1;
    }
    //获取总金额
    public BigDecimal getAmount(Integer rootId){
        BigDecimal totalMoney = new BigDecimal(0);
        List<WxOrderDetail> wxOrderDetailList = new ArrayList<>();
        wxOrderDetailList=orderDetailRepository.findByOrderId(rootId);
        for (int i=0;i<wxOrderDetailList.size();i++){
            //金额相加
            totalMoney = totalMoney.add(wxOrderDetailList.get(i).getFoodPrice());
        }
        return totalMoney;
    }
    //获取地址
    public String getAddtess(String openid){
        String address= "";
        UserInfo byOpenid = userRepository.findByOpenid(openid);
        address=byOpenid.getAddress();
        return address;
    }
    //获取积分
    public Integer getIntegral(String openid){
        Integer address= 0;
        UserInfo byOpenid = userRepository.findByOpenid(openid);
        address=byOpenid.getIntegral();
        return address;
    }



}
