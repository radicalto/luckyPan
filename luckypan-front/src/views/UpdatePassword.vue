<template>
  <Dialog
    :show="dialogConfig.show"
    :title="dialogConfig.title"
    :buttons="dialogConfig.buttons"
    width="500px"
    :showCancel="false"
    @close="dialogConfig.show = false"
  >
    <el-form
      ref="formDataRef"
      :rules="rules"
      :model="formData"
      label-width="80px"
      @submit.prevent
    >
      <!-- input输入 -->
      <el-form-item label="密码" prop="password">
        <el-input
          type="password"
          v-model.trim="formData.password"
          placeholder="请输入密码"
          size="large"
          show-password
        >
          <template #prefix>
            <span class="iconfont icon-password"></span>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item label="确认密码" prop="rePassword">
        <el-input
          type="password"
          v-model.trim="formData.rePassword"
          placeholder="请再次输入密码"
          size="large"
          show-password
        >
          <template #prefix>
            <span class="iconfont icon-rePassword"></span>
          </template>
        </el-input>
      </el-form-item>
    </el-form>
  </Dialog>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, nextTick } from "vue";
import { ElMessage } from "element-plus";
const { proxy } = getCurrentInstance();

const checkRePassword = (rule, value, callback) => {
  if (value !== formData.value.rePassword) {
    callback(new Error(rule.message));
  } else {
    callback();
  }
};

const api = {
  updatePassword: "updatePassword",
};

const formData = ref({});
const formDataRef = ref();
const rules = {
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    {
      validator: proxy.Verfiy.password,
      message: "密码只能是数字,字母,特殊字符8-18位",
    },
  ],
  rePassword: [
    { required: true, message: "请再次输入密码", trigger: "blur" },
    { validator: checkRePassword, message: "两次输入密码不同" },
  ],
};

const show = (data) => {
  dialogConfig.value.show = true;
  nextTick(() => {
    formDataRef.value.resetFields();
    formData.value = {};
  });
};

defineExpose({ show });

const dialogConfig = ref({
  show: false,
  title: "修改密码",
  buttons: [
    {
      type: "primary",
      text: "确定",
      click: (e) => {
        submitForm();
      },
    },
  ],
});

const submitForm = async () => {
  formDataRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }

    let result = await proxy.Request({
      url: api.updatePassword,
      params: {
        password: formData.value.password,
      },
    });
    if (!result) {
      return;
    }
    dialogConfig.value.show = false;
    ElMessage.success("密码修改成功");
  });
};
</script>

<style lang="scss" scoped>
</style>