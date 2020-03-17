//app.js
App({
  //创建towxml对象，供小程序页面使用
  globalData: {
    userInfo: null,
    openid: null,
    baseUrl: 'http://localhost:8080/diancan' //本地调试
    // baseUrl: 'http://192.168.31.159:8080/diancan' //真机调试,这里的ip地址一定是你电脑的ip
  },

  // onLaunch: function() {
  //   //云开发初始化
  //   wx.cloud.init({
  //     // env: 'prod-924a3b', //这里一定要换成你自己的云开发环境id
  //     // env: 'pp-5ehzi', //这里一定要换成你自己的云开发环境id
  //     traceUser: true,
  //   })
  //   this.getOpenid();
  // },

  onLaunch: function () {

    if (!wx.cloud) {
      console.error('请使用 2.2.3 或以上的基础库以使用云能力')
    } else {
      wx.cloud.init({
        // env 参数说明：
        //   env 参数决定接下来小程序发起的云开发调用（wx.cloud.xxx）会默认请求到哪个云环境的资源
        //   此处请填入环境 ID, 环境 ID 可打开云控制台查看
        //   如不填则使用默认环境（第一个创建的环境）
        // env: 'pp-5ehzi',
        traceUser: true,
      })
    }
    this.globalData = {}
    this.onGetOpenid()
  },

  // openid新的获取方式
  onGetOpenid: function () {
    var app = this;
    var openidStor = wx.getStorageSync('openid');
    // 判断openidStor是否为空
    if (openidStor) {
      console.log('本地获取openid:' + openidStor);
      app.globalData.openid = openidStor;
      app._getUserInfo();
    }else{
      // 调用云函数
      wx.cloud.callFunction({
        name: 'login',
        data: {},
        success: res => {
          console.log('[云函数] [login] user openid: ', res.result.openid)
          var openid = res.result.openid;
          wx.setStorageSync('openid', openid)
          app.globalData.openid = openid;
          app._getUserInfo();
          // app.globalData.openid = res.result.openid
        },
        fail: err => {
          console.error('[云函数] [login] 调用失败', err)
        }
      })
    }
    
  },

  // 获取用户openid 石头老师
  getOpenid: function () {
    var app = this;
    var openidStor = wx.getStorageSync('openid');
    if (openidStor) {
      console.log('本地获取openid:' + openidStor);
      app.globalData.openid = openidStor;
      app._getUserInfo();
    } else {
      wx.cloud.callFunction({
        name: 'getOpenid',
        success(res) {
          console.log('云函数获取openid成功', res)
          var openid = res.result.openid;
          wx.setStorageSync('openid', openid)
          app.globalData.openid = openid;
          app._getUserInfo();
        },
        fail(res) {
          console.log('云函数获取失败', res)
        }
      })
    }
  },
  
  // 获取用户信息，如果用户没有授权，就获取不到
  _getUserInfo: function () {
    var app = this;
    wx.getUserInfo({ //从网络获取最新用户信息
      success: function (res) {
        var user = res.userInfo;
        user.openid = app.globalData.openid;
        app.globalData.userInfo = user;
        console.log('请求获取user成功', user)
        app._getMyUserInfo();
        app._saveUserInfo(user);
        // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
        // 所以此处加入 callback 以防止这种情况
        if (app.userInfoReadyCallback) {
          app.userInfoReadyCallback(res)
        }
      },
      fail: function (res) { //请求网络失败时，再读本地数据
        console.log('请求获取user失败')
        var userStor = wx.getStorageSync('user');
        if (userStor) {
          console.log('本地获取user')
          app.globalData.userInfo = userStor;
        }
      }
    })
  },

  _checkOpenid() {
    let app = this
    let openid = this.globalData.openid;
    if (!openid) {
      app.getOpenid();
      wx.showLoading({
        title: 'openid不能为空，请重新登录',
      })
      return null;
    } else {
      return openid;
    }
  },
  // 保存userinfo
  _saveUserInfo: function (user) {
    this.globalData.userInfo = user;
  },
  //获取自己后台的user信息
  _getMyUserInfo(user) {
    let app = this
    let userInfo = app.globalData.userInfo;
    wx.request({
      url: app.globalData.baseUrl + '/user/getUserInfo',
      data: {
        openid: app.globalData.openid
      },
      success: function (res) {
        if (res && res.data && res.data.data) {
          console.log("Java后台返回的用户信息", res.data)
          userInfo.nickName = res.data.data.username;
          userInfo.realphone = res.data.data.phone;
          userInfo.realzhuohao = res.data.data.zhuohao;
          userInfo.realrenshu = res.data.data.renshu;
          app.globalData.userInfo = userInfo;
          console.log("===userInfo", userInfo)
        }
      }
    })
    console.log("我的后台", userInfo)
    //缓存到sd卡里
    wx.setStorageSync('user', userInfo);
  },
  //获取今天是本月第几周
  _getWeek: function () {
    // 将字符串转为标准时间格式
    let date = new Date();
    let month = date.getMonth() + 1;
    let week = this.getWeekFromDate(date);
    if (week === 0) { //第0周归于上月的最后一周
      month = date.getMonth();
      let dateLast = new Date();
      let dayLast = new Date(dateLast.getFullYear(), dateLast.getMonth(), 0).getDate();
      let timestamp = new Date(new Date().getFullYear(), new Date().getMonth() - 1, dayLast);
      week = this.getWeekFromDate(new Date(timestamp));
    }
    let time = month + "月第" + week + "周";
    return time;
  },

  getWeekFromDate: function (date) {
    // 将字符串转为标准时间格式
    let w = date.getDay(); //周几
    if (w === 0) {
      w = 7;
    }
    let week = Math.ceil((date.getDate() + 6 - w) / 7) - 1;
    return week;
  },
  // 获取当前时间
  _getCurrentTime() {
    var d = new Date();
    var month = d.getMonth() + 1;
    var date = d.getDate();
    var day = d.getDay();
    var hours = d.getHours();
    var minutes = d.getMinutes();
    var seconds = d.getSeconds();
    var ms = d.getMilliseconds();

    var curDateTime = d.getFullYear() + '年';
    if (month > 9)
      curDateTime += month + '月';
    else
      curDateTime += month + '月';

    if (date > 9)
      curDateTime = curDateTime + date + "日";
    else
      curDateTime = curDateTime + date + "日";
    if (hours > 9)
      curDateTime = curDateTime + hours + "时";
    else
      curDateTime = curDateTime + hours + "时";
    if (minutes > 9)
      curDateTime = curDateTime + minutes + "分";
    else
      curDateTime = curDateTime + minutes + "分";
    return curDateTime;
  }
})