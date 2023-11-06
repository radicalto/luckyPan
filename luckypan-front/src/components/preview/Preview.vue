<template>
  <PreviewImage
    ref="imageViewerRef"
    :imageList="[imageUrl]"
    v-if="fileInfo.fileCategory == 3"
  ></PreviewImage>
  <Window
    :show="windowShow"
    @close="closeWindow"
    :width="fileInfo.fileCategory == 1 ? 1200 : 700"
    :title="fileInfo.fileName"
    :align="fileInfo.fileCategory == 1 ? 'center' : 'top'"
    v-else
  >
    <PreviewVideo :url="url" v-if="fileInfo.fileCategory == 1"></PreviewVideo>
    <PreviewPdf :url="url" v-if="fileInfo.fileType == 4"></PreviewPdf>
    <PreviewDoc :url="url" v-if="fileInfo.fileType == 5"></PreviewDoc>
    <PreviewExcel :url="url" v-if="fileInfo.fileType == 6"></PreviewExcel>
    <PreviewTxt
      :url="url"
      v-if="fileInfo.fileType == 7 || fileInfo.fileType == 8"
    ></PreviewTxt>
    <!--特殊预览-->
    <PreviewMusic
      :url="url"
      :fileName="fileInfo.fileName"
      v-if="fileInfo.fileCategory == 2"
    ></PreviewMusic>
    <PreviewDownload
      :createDownloadUrl="createDownloadUrl"
      :downloadUrl="downloadUrl"
      :fileInfo="fileInfo"
      v-if="fileInfo.fileCategory == 5 && fileInfo.fileType != 8"
    ></PreviewDownload>
  </Window>
</template>

<script setup>
import PreviewImage from "@/components/preview/PreviewImage.vue";
import PreviewVideo from "@/components/preview/PreviewVideo.vue";
import PreviewDoc from "@/components/preview/PreviewDoc.vue";
import PreviewExcel from "@/components/preview/PreviewExcel.vue";
import PreviewPdf from "@/components/preview/PreviewPdf.vue";
import PreviewTxt from "@/components/preview/PreviewTxt.vue";
import PreviewMusic from "@/components/preview/PreviewMusic.vue";
import PreviewDownload from "@/components/preview/PreviewDownload.vue";
import { ref, reactive, getCurrentInstance, nextTick, computed } from "vue";

const { proxy } = getCurrentInstance();
const FILE_URL_MAP = {
  0: {
    fileUrl: "/file/getFile",
    videoUrl: "/file/ts/getVideoInfo",
    createDownloadUrl: "/file/createDownloadUrl",
    downloadUrl: "/api/file/download",
  },
  1: {
    fileUrl: "/admin/getFile",
    videoUrl: "/admin/ts/getVideoInfo",
    createDownloadUrl: "/admin/createDownloadUrl",
    downloadUrl: "/api/admin/download",
  },
  2: {
    fileUrl: "/showShare/getFile",
    videoUrl: "/showShare/ts/getVideoInfo",
    createDownloadUrl: "/showShare/createDownloadUrl",
    downloadUrl: "/api/showShare/download",
  },
};

const fileInfo = ref({});
const imageViewerRef = ref();

const windowShow = ref(false);
const closeWindow = () => {
  windowShow.value = false;
};

const imageUrl = computed(() => {
  return (proxy.globalInfo.imageUrl + fileInfo.value.fileCover).replaceAll(
    "_.",
    "."
  );
});

const url = ref();
const createDownloadUrl = ref();
const downloadUrl = ref();

const showPreview = (data, showPart) => {
  fileInfo.value = data;
  console.log("fileInfo: ", data);
  if (data.fileCategory == 3) {
    nextTick(() => {
      imageViewerRef.value.show(0);
    });
  } else {
    windowShow.value = true;
    let _url = FILE_URL_MAP[showPart].fileUrl;
    let _createDownloadUrl = FILE_URL_MAP[showPart].createDownloadUrl;
    let _downloadUrl = FILE_URL_MAP[showPart].downloadUrl;
    //视频地址单独处理
    if (data.fileCategory == 1) {
      _url = FILE_URL_MAP[showPart].videoUrl;
    }
    if (showPart == 0) {
      _url = _url + "/" + data.fileId;
      _createDownloadUrl = _createDownloadUrl + "/" + data.fileId;
    }
    url.value = _url;
    createDownloadUrl.value = _createDownloadUrl;
    downloadUrl.value = _downloadUrl;
  }
};
defineExpose({ showPreview });
</script>

<style lang="scss" scoped>
</style>