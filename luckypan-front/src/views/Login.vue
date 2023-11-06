<template>
  <div class="login-body">
    <div class="bg" style="margin-left: 300px">+</div>
    <div class="login-panel">
      <el-form
        class="login-register"
        :model="formData"
        :rules="rules"
        ref="formDataRef"
        @submit.prevent
      >
        <div class="login-title">Lucky云盘</div>
        <el-form-item prop="email">
          <el-input
            size="large"
            clearable
            placeholder="请输入邮箱"
            v-model.trim="formData.email"
            maxlength="150"
          >
            <template #prefix>
              <span class="iconfont icon-account"></span>
            </template>
          </el-input>
        </el-form-item>
        <!-- 登入密码 -->
        <el-form-item prop="password" v-if="opType == 1">
          <el-input
            v-model="formData.password"
            placeholder="请输入密码"
            size="large"
            clearable
            show-password
            type="password"
          >
            <template #prefix>
              <span class="iconfont icon-password"></span>
            </template>
          </el-input>
        </el-form-item>

        <!-- 注册才会显现 -->
        <div v-if="opType == 0 || opType == 2">
          <el-form-item prop="emailCode">
            <div class="send-email-panel">
              <el-input
                v-model="formData.emailCode"
                placeholder="请输入邮箱验证码"
                size="large"
              >
                <template #prefix>
                  <span class="iconfont icon-checkcode"></span>
                </template>
              </el-input>
              <el-button
                class="send-mail-btn"
                type="primary"
                size="large"
                @click="getEmailCode"
              >
                获取验证码
              </el-button>
            </div>
            <el-popover placement="left" width="500" trigger="click">
              <div>
                <p>1、在垃圾箱中查找邮箱验证码</p>
                <p>2、在邮箱中头像->设置->反垃圾->自名单->设置邮件地址白名单</p>
                <p>3、将邮箱【luckypan@qq.com】添加到白名单不知道怎么设置?</p>
              </div>
              <!-- 具名插槽将 
                <slot name="reference">的内容放入这里面
                #reference 是 v-slot:reference的简写
              -->
              <template #reference>
                <span class="a-link" :style="{ 'font-size': '14px' }">
                  未收到邮箱验证码?
                </span>
              </template>
            </el-popover>
          </el-form-item>
          <el-form-item prop="nickName" v-if="opType == 0">
            <el-input
              v-model="formData.nickName"
              placeholder="请输入昵称"
              size="large"
            >
              <template #prefix>
                <span class="iconfont icon-user"></span>
              </template>
            </el-input>
          </el-form-item>

          <!-- 注册密码 -->
          <el-form-item prop="registerPassword">
            <el-input
              v-model="formData.registerPassword"
              placeholder="请输入密码"
              size="large"
            >
              <template #prefix>
                <span class="iconfont icon-password"></span>
              </template>
            </el-input>
          </el-form-item>
          <!-- 重复密码 -->
          <el-form-item prop="reRegisterPassword">
            <el-input
              v-model="formData.reRegisterPassword"
              placeholder="请再次输入密码"
              size="large"
            >
              <template #prefix>
                <span class="iconfont icon-password"></span>
              </template>
            </el-input>
          </el-form-item>
        </div>

        <!-- 验证码 -->
        <el-form-item prop="checkCode">
          <div class="check-code-pannel">
            <el-input
              v-model="formData.checkCode"
              placeholder="请输入验证码"
              size="large"
              style="width: 240px; height: 40px; margin-right: 20px"
            >
              <template #prefix>
                <span class="iconfont icon-checkcode"></span>
              </template>
            </el-input>
            <img
              :src="checkCodeUrl"
              class="check-code"
              @click="changeCheckCode(0)"
            />
          </div>
        </el-form-item>
        <el-form-item v-if="opType == 1">
          <div class="rememberme-pannel">
            <el-checkbox v-model="formData.rememberMe">记住我</el-checkbox>
          </div>
          <div class="no-account">
            <a href="javascript:void(0)" class="a-link" @click="showPanel(2)"
              >忘记密码?</a
            >
            <a href="javascript:void(0)" class="a-link" @click="showPanel(0)"
              >没有账号</a
            >
          </div>
        </el-form-item>
        <!--  -->
        <el-form-item v-if="opType == 2">
          <div class="no-account">
            <a href="javascript:void(0)" class="a-link" @click="showPanel(1)"
              >去登录</a
            >
          </div>
        </el-form-item>
        <!--  -->
        <el-form-item v-if="opType == 0">
          <div class="no-account">
            <a href="javascript:void(0)" class="a-link" @click="showPanel(1)"
              >已有帐号?</a
            >
          </div>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="op-btn"
            @click="doSubmit"
          >
            <span v-if="opType == 0">注册</span>
            <span v-if="opType == 1">登录</span>
            <span v-if="opType == 2">重置密码</span>
          </el-button>
          <!-- qq登入 -->
          <div class="login-btn-qq" v-if="opType == 1">
            快捷登入<img src="" @click="qqLogin" />
          </div>
        </el-form-item>
      </el-form>
    </div>

    <Dialog
      :show="dialogConfig4SendMailCode.show"
      :title="dialogConfig4SendMailCode.title"
      :buttons="dialogConfig4SendMailCode.buttons"
      width="500px"
      :showCancel="false"
      @close="dialogConfig4SendMailCode.show = false"
    >
      <el-form
        :model="formData4SendMailCode"
        ref="formData4SendMailCodeRef"
        :rules="rules"
        label-width="80px"
        @submit.prevent
      >
        <el-form-item label="邮箱">
          {{ formData.email }}
        </el-form-item>
        <!-- 验证码 -->
        <el-form-item label="验证码" prop="checkCode">
          <div class="check-code-pannel">
            <el-input
              v-model="formData4SendMailCode.checkCode"
              placeholder="请输入验证码"
              size="large"
              @keyup.enter="doSubmit"
              style="width: 200px; margin-right: 20px"
            >
              <template #prefix>
                <span class="iconfont icon-checkcode"></span>
              </template>
            </el-input>
            <img
              :src="checkCodeUrl4SendMailCode"
              class="check-code"
              @click="changeCheckCode(1)"
            />
          </div>
        </el-form-item>
        <!-- 没出现自己加的按钮 -->
        <div class="dialog-footer">
          <!-- <el-button link @click="close" v-if="showCancel">取消</el-button>
          <el-button
            v-for="btn in dialogConfig4SendMailCode.buttons"
            :type="btn.type || 'primary'"
            @click="btn.click"
          >
            {{ btn.text }}
          </el-button> -->
        </div>
      </el-form>
    </Dialog>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, nextTick, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import md5 from "js-md5";
const { proxy } = getCurrentInstance();

const api = {
  checkCode: "/api/checkCode",
  sendEmailCode: "/sendEmailCode",
  register: "/register",
  login: "/login",
  resetPwd: "/resetPwd", // 重设密码页面不支持简单加密，请使用加密模
  qqlogin: "/qqlogin", // QQ登录页面不支持简单加密，请使用加密模式或使
};

const router = useRouter();
const route = useRoute();

// 定义操作类型 0 注册  1 登录   2重置密码
const opType = ref(1);
const showPanel = (type) => {
  opType.value = type;
  resetForm();
};

const checkRePassword = (rule, value, callback) => {
  if (value !== formData.value.registerPassword) {
    callback(new Error(rule.message));
  } else {
    callback();
  }
};

const formData = ref({});
const formDataRef = ref();
const rules = reactive({
  email: [
    { required: true, message: "请输入正确的邮箱", trigger: "blur" },
    { validator: proxy.Verfiy.email, message: "请输入正确的邮箱" },
  ],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  emailCode: [{ required: true, message: "请输入邮箱验证码", trigger: "blur" }],
  nickName: [{ required: true, message: "请输入昵称", trigger: "blur" }],
  registerPassword: [
    { required: true, message: "请输入密码", trigger: "blur" },
    {
      validator: proxy.Verfiy.password,
      message: "密码只能是数字,字母,特殊字符8-18位",
    },
  ],
  reRegisterPassword: [
    { required: true, message: "请再次输入密码", trigger: "blur" },
    { validator: checkRePassword, message: "两次输入密码不同" },
  ],
  checkCode: [{ required: true, message: "请输入图片验证码", trigger: "blur" }],
});

const checkCodeUrl = ref(api.checkCode);
const checkCodeUrl4SendMailCode = ref(api.checkCode);
const changeCheckCode = (type) => {
  if (type == 0) {
    checkCodeUrl.value =
      api.checkCode + "?type=" + type + "&time=" + new Date().getTime();
  } else {
    checkCodeUrl4SendMailCode.value =
      api.checkCode + "?type=" + type + "&time=" + new Date().getTime();
  }
};
// 发送邮箱验证码
const formData4SendMailCode = ref({});
const formData4SendMailCodeRef = ref({});

const dialogConfig4SendMailCode = reactive({
  show: false,
  title: "发送验证码",
  buttons: [
    {
      type: "primary",
      text: "发送验证码",
      click: (e) => {
        sendEmailCode();
      },
    },
  ],
});

// 重置密码
const resetForm = () => {
  changeCheckCode(0);
  formDataRef.value.resetFields();
  formData.value = {};
  // 登录
  if (opType == 1) {
    const cookieLoginInfo = proxy.VueCookies.get("loginInfo");
    if (cookieLoginInfo) {
      formData.value = cookieLoginInfo;
    }
  }
};
// 页面渲染完成 获取更新后的dom 给rememberMe用户的表单赋值
onMounted(() => {
  showPanel(1);
});

const doSubmit = () => {
  formDataRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }
    let params = {};
    Object.assign(params, formData.value); // 将form中的值复制到params中。这是一种
    // 注册 找回密码
    if (opType.value == 0 || opType.value == 2) {
      // 找回密码或重新登录匹配的验证码匹配或者匹配到一个)
      params.password = params.registerPassword;
      // 将多余的请求参数删除 让后端接受更清楚
      delete params.reRegisterPassword;
      delete params.reRegisterPassword;
    }
    // 登入
    if (opType.value == 1) {
      let cookieLoginInfo = proxy.VueCookies.get("loginInfo");
      let cookiePassword =
        cookieLoginInfo == null ? null : cookieLoginInfo.password;
      if (params.password != cookiePassword) {
        // 如果不相等，说明密码为加密
        params.password = md5(params.password);
      }
    }
    let url = null;
    if (opType.value == 0) {
      // 注册请求 发送验证码到服务器 匹配验证码
      url = api.register;
    } else if (opType.value == 1) {
      url = api.login;
    } else if (opType.value == 2) {
      url = api.resetPwd;
    }
    let result = await proxy.Request({
      url: url, // 请求的地址
      params: params, // 请求的参数
      errorCallback: () => {
        changeCheckCode(0);
      },
    });
    // 注册返回
    if (opType.value == 0) {
      // 注册成功或者找回密码成功的回调
      ElMessage.success("注册成功，请登录");
      showPanel(1);
    } else if (opType.value == 1) {
      if (params.rememberMe) {
        const loginInfo = {
          email: params.email, // 用户名或者邮件地址
          password: params.password, // 用户名或者邮件地址的密码
          rememberMe: params.rememberMe, // 是否记住密码或不记录密码
        };
        proxy.VueCookies.set("loginInfo", loginInfo, "7d"); // 记住用户名或者邮件地址的密码或不记录密码的Cookie的7天
      } else {
        proxy.VueCookies.remove("loginInfo"); // 移除记住用户名或者邮件地址的密码或不记录密
      }
      ElMessage.success("登入成功");
      // 存储cookie
      proxy.VueCookies.set("userInfo", result.data, 0); // 存储用户信息到Cookie中 （用户名、密码、
      console.log("result-userinfo: ", result);
      // 重定向到原始页面
      const redirectUrl = route.query.redirectUrl || "/";
      router.push(redirectUrl);
    } else if (opType.value == 2) {
      // 找回密码成功
      ElMessage.success("重置密码成功，请重新登录");
      showPanel(1);
    }
  });
};
// 发送邮箱验证码
const sendEmailCode = () => {
  formData4SendMailCodeRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }
    const params = Object.assign({}, formData4SendMailCode.value);
    params.type = opType.value == 0 ? 0 : 1;
    let result = await proxy.Request({
      url: api.sendEmailCode,
      params: params,
      errorCallback: () => {
        ElMessage.error("请求参数错误");
        changeCheckCode(1);
        breack();
      },
    });
    if (!result) {
      return;
    }
    // proxy.Message.success("验证码发送成功，请查看邮箱");
    ElMessage.success("验证码发送成功，请查看邮件");
    dialogConfig4SendMailCode.show = false; // 隐藏加载对话框，防止误操作造成影响其
  });
};

const getEmailCode = async () => {
  await formDataRef.value.validateField("email", (valid) => {
    if (!valid) {
      return;
    }
    dialogConfig4SendMailCode.show = true;
    nextTick(() => {
      changeCheckCode(1);
      formData4SendMailCodeRef.value.resetFields(); // 清空表单
      formData4SendMailCode.value = {
        email: formData.value.email, // 赋上邮箱值
      };
    });
  });
};

// qq登入
const qqLogin = async () => {
  let result = await proxy.Request({
    url: api.qqlogin,
    params: {
      callbackUrl: route.query.redirectUrl || "", // 回调地址，可以是任何格式的URL，也可以自定义。回调地址可以
    },
  });
  if (!result) {
    return;
  }
  proxy.VueCookies.remove("userInfo"); // 清除Cookie中的userInfo值，防止误操作造成影响其。如果你的应用程序使
  document.location.href = result.data;
};
</script>

<style lang="scss" scoped>
.login-body {
  height: calc(100vh);
  background: url("../assets/loginBg.jpg");
  background-position: center;
  background-repeat: no-repeat;
  background-size: 100%;
  display: flex;
  // .bg {
  //   flex: 1;
  //   background-size: cover;
  //   background-position: center;
  //   background-size: 800px;
  //   background-repeat: no-repeat;
  //   background-image: url("../assets/laptop.png");
  // }
  .login-panel {
    width: 430px;
    margin-right: 15%;
    margin-top: calc((100vh - 500px) / 2);
    .login-register {
      padding: 25px;
      background: #fff;
      border-radius: 5px;
      .login-title {
        text-align: center;
        font-size: 18px;
        font-weight: bold;
        margin-bottom: 20px;
      }
      .send-email-panel {
        display: flex;
        width: 100%;
        justify-content: space-between;
        .send-mail-btn {
          margin-left: 5px;
        }
      }
      .remeberme-panel {
        width: 100%;
      }
      .no-account {
        width: 100%;
        display: flex;
        justify-content: space-between;
      }
      .op-btn {
        width: 100%;
      }
    }
  }

  .check-code-panel {
    width: 100%;
    display: flex;
    overflow: hidden;
    .check-code {
      margin-left: 5px;
      cursor: pointer;
      float: right;
    }
  }
  .login-btn-qq {
    margin-top: 20px;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
    img {
      cursor: pointer;
      margin-left: 10px;
      width: 20px;
    }
  }
}
.dialog-footer {
  text-align: right;
  padding: 5px 20px;
}
</style>
