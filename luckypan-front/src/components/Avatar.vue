<template>
  <span class="avatar" :style="{ width: width + 'px', height: width + 'px' }">
    <img
      :src="
        avatar && avatar != ''
          ? avatar
          : `${proxy.globalInfo.avatarUrl}${userId}?timestamp=${timestamp}`
      "
      v-if="userId"
    />
  </span>
</template>

<script setup>
import { getCurrentInstance } from "vue";
const { proxy } = getCurrentInstance();

const props = defineProps({
  userId: {
    type: String,
  },
  avatar: {
    type: String,
  },
  width: {
    type: Number,
    default: 40,
  },
  timestamp: {
    type: Number,
    default: 0,
  },
});
</script>

<style lang="scss" scoped>
.avatar {
  display: flex;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  img {
    width: 100%;
    object-fit: cover;
  }
}
</style>