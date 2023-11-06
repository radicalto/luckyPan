<template>
  <div>
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
        :model="formData"
        label-width="80px"
        @submit.prevent
      >
        <!-- input输入 -->
        <el-form-item label="昵称" prop="">
          <el-input v-model.trim="formData.nickName" placeholder=""></el-input>
        </el-form-item>
        <!-- textarea输入 -->
        <el-form-item label="头像" prop="">
          <AvatarUpload v-model="formData.avatar"></AvatarUpload>
        </el-form-item>
      </el-form>
    </Dialog>
  </div>
</template>

<script setup>
import AvatarUpload from "@/components/AvatarUpload.vue";
import { ref, reactive, getCurrentInstance, nextTick } from "vue";
import { ElMessage } from "element-plus";
const { proxy } = getCurrentInstance();

const formData = ref({});
const formDataRef = ref();
const emit = defineEmits();

const api = {
  updateUserAvatar: "updateUserAvatar",
};
const show = (data) => {
  formData.value = Object.assign({}, data);
  formData.value.avatar = { userId: data.userId, qqAvatar: data.avatar };
  console.log("formData.value.avatar: ", formData.value.avatar);
  dialogConfig.value.show = true;
};

defineExpose({ show });

const dialogConfig = ref({
  show: false,
  title: "修改头像",
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
  // 如果它不是一个文件，则无返回，同时避免了没上传头像就能提交修改的问题
  if (!(formData.value.avatar instanceof File)) {
    dialogConfig.value.show = false;
    return;
  }

  let result = await proxy.Request({
    url: api.updateUserAvatar,
    params: {
      avatar: formData.value.avatar,
    },
  });
  if (!result) {
    return;
  }
  dialogConfig.value.show = false;
  const cookieUserInfo = proxy.VueCookies.get("userInfo");
  delete cookieUserInfo.avatar;
  proxy.VueCookies.set("userInfo", cookieUserInfo, 0);
  emit("updateAvatar");
  ElMessage.success("头像修改成功");
};
</script>

<style lang="scss" scoped>
</style>