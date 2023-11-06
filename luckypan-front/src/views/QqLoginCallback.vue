<template>
 <div>
    登入中，请勿刷新
 </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, nextTick, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
const { proxy } = getCurrentInstance();

const router = useRouter();
const route = useRoute();

const api = {
   logincallback:"/qqlogin/callback",
}

const login = async () => {
   let result = await proxy.Request({
      url:api.logincallback,//请求的url或地址，不要带“/”。 这个地址自然地应该是一个接口地址，例如 https://codegeex.cn 。 你也可以选择从一个已发送的请求的中缀名称中提取。 这个页面可以是任何简单的或零长度的字符串。请检查请求是否发送成功，并检查返回的消息是否是“000000”
      params: router.currentRoute.value.query,//请求的query参数，如果有的话。 参数可以是任何简单的字符
      errorCallback: () => {
         router.push("/")
      }
   });
   if(!result) {
      return;
   }
   let redirectUrl = result.data.errorCallback || "/";
   if (redirectUrl == "login") {
      redirectUrl = "/";
   }
   proxy.VueCookies.set("userInfo", result.data.userInfo, 0);
   router.push(redirectUrl);//push是直接跳转到指定的路径，不要使用页面跳转。
}

</script>

<style lang="scss" scoped>

</style>