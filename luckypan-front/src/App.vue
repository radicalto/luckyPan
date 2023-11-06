<template>
  <el-config-provider :message="config"
    ><!-- :locale="locale" -->
    <router-view />
  </el-config-provider>
</template>

<script setup>
import { reactive } from "vue";
// import zhCn from "element-plus/lib/locale/ang/zh-cn";
// const locale = zhCn;
const config = reactive({
  max: 1,
});
</script>

<style lang="scss" scoped>
</style>