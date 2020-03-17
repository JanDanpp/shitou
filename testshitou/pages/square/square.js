// pages/square/square.js
var app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    squareList: [], //订单广场
    integral: 0,
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.square();
  },

  onGotUserInfo: function (e) {
    console.log(e.target.id)//第几个按钮
    console.log(this.data.squareList[e.target.id].Numbering) //id
    let Numbering = this.data.squareList[e.target.id].Numbering
    console.log(this.data.squareList[e.target.id].orderStatus) //状态
    let orderStatus = this.data.squareList[e.target.id].orderStatus
    console.log(app.globalData.openid) //openid
    let openid = app.globalData.openid
    wx.request({
      url: app.globalData.baseUrl + '/user/updateSquare',
      method: "POST",
      header: {
        "Content-Type": "application/x-www-form-urlencoded"
      },
      data: {
        openid: openid,
        numbering: Numbering,
        orderStatus:orderStatus,  
      },
      success: function(res){
        console.log(res+"-----")
        if(res.data==0){
          wx.showToast({
            title: '违规操作',
            icon: 'none',
            duration: 2000,
          })
        }else if(res.data==1){
          wx.showToast({
            title: '接单成功',
            icon: 'success',
            duration: 2000,
          })
        }else if(res.data==2){
          wx.showToast({
            title: '收到外卖',
            icon: 'success',
            duration: 2000,
          })
        }else if(res.data==3){
          wx.showToast({
            title: '不能取消订单',
            icon: 'none',
            duration: 2000,
          })
        }
        setTimeout(function() {
          wx.reLaunch({
            url: '/pages/square/square'
           })
        }, 1000)
      },
      fail: function(){
        wx.showToast({
          title: '接单失败请联系管理员',
        })
      }
    })
  },

  square() {
    var that = this
    console.log("11111"+that);
    let  openid = app._checkOpenid();
    console.log(openid)
    wx.request({
      url: app.globalData.baseUrl + '/LsquareList',
      data: {
        openid: openid
      },
      success: function (res) {
        if (res != null) {
          console.log(res.data)
          console.log(res.data[0].integral)
          that.setData({
            squareList: res.data,
            integral:res.data[0].integral
          })

        } else {
          
        }
      }
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})