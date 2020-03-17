//JS
var app = getApp()

Page({
  data: {
    list: ''
  },

  onLoad(options) {
      this.getMydeliveryCommentList();
  },

  //获取我的所有评论列表
  getMydeliveryCommentList() {
    let that = this;
    let openid = app._checkOpenid();
    if (!openid) {
      return;
    }
    //请求自己后台获取用户openid
    wx.request({
      url: app.globalData.baseUrl + '/userintegral',
      data: {
        openid: openid
      },
      success: function (res) {
        if (res && res.data) {
          
          that.setData({
            list: res.data.integral
          })
        } else {
          
        }
      }
    })
  },
})