<template>
  <div>
    <div class="top">
      <div class="top-op">
        <div class="btn">
          <el-upload
            :show-file-list="false"
            :with-credentials="true"
            :multiple="true"
            :http-request="addFile"
            :accept="fileAccept"
          >
            <el-button type="primary">
              <span class="iconfont icon-upload"></span>
              上传
            </el-button>
          </el-upload>
        </div>
        <el-button type="success" @click="newFolder">
          <span class="iconfont-icon folder-add"></span>
          新建文件夹
        </el-button>
        <el-button
          type="danger"
          :disabled="selectFileIdList.length == 0"
          @click="delFileBatch"
        >
          <span class="iconfont icon-del"></span>
          批量删除
        </el-button>
        <el-button
          type="warning"
          :disabled="selectFileIdList.length == 0"
          @click="moveFolderBatch"
        >
          <span class="iconfont icon-move"></span>
          批量移动
        </el-button>
        <div class="search-panel">
          <!-- input输入 -->
          <el-input
            placeholder="输入文件名搜索"
            clearable
            v-model="fileNameFuzzy"
            @keyup.enter="search"
          >
            <template #suffix>
              <span class="iconfont icon-search" @click="search"></span>
            </template>
          </el-input>
        </div>
        <div class="iconfont icon-refresh" @click="loadDataList"></div>
      </div>
      <!-- <div>全部文件</div> -->
    </div>
    <!--导航-->
    <Navigation ref="navigationRef" @navChange="navChange"></Navigation>
    <div class="file-list" v-if="tableData.list && tableData.list.length > 0">
      <Table
        ref="dataTabelRef"
        :columns="columns"
        :dataSource="tableData"
        :fetch="loadDataList"
        :initFetch="false"
        :options="tableOptions"
        @rowSelected="rowSelected"
      >
        <template #fileName="{ index, row }">
          <div
            class="file-item"
            @mouseenter="showOp(row)"
            @mouseleave="cancelShowOp(row)"
          >
            <template
              v-if="(row.fileType == 3 || row.fileType == 1) && row.status == 2"
            >
              <icon :cover="row.fileCover" :width="32"></icon>
            </template>
            <template v-else>
              <icon v-if="row.folderType == 0" :fileType="row.fileType"></icon>
              <icon v-if="row.folderType == 1" :fileType="0"></icon>
            </template>
            <span class="file-name" :title="row.fileName" v-if="!row.showEdit">
              <span @click="preview(row)">{{ row.fileName }}</span>
              <span v-if="row.status == 0" class="transfer-status">转码中</span>
              <span v-if="row.status == 1" class="transfer-status transfer-fail"
                >转码失败</span
              >
            </span>
            <div class="edit-panel" v-if="row.showEdit">
              <el-input
                v-model.trim="row.fileNameReal"
                ref="editNameRef"
                :maxLength="190"
                @keyup.enter="saveNameEdit(index)"
              >
                <template #suffix>{{ row.fileSuffix }}</template>
              </el-input>
              <span
                :class="[
                  'iconfont icon-right1',
                  row.fileNameReal ? '' : 'not-allow',
                ]"
                @click="saveNameEdit(index)"
              ></span>
              <span
                class="iconfont icon-error"
                @click="cancelNameEdit(index)"
              ></span>
            </div>
            <span class="op">
              <template v-if="row.showOp && row.fileId && row.status == 2">
                <span class="iconfont icon-share1">分享</span>
                <span class="iconfont icon-download" v-if="row.folderType == 0"
                  >下载</span
                >
                <span class="iconfont icon-del" @click="delFile(row)"
                  >删除</span
                >
                <span
                  class="iconfont icon-edit"
                  @click.stop="editFileName(index)"
                  >重命名</span
                >
                <span class="iconfont icon-move" @click="moveFolder(row)"
                  >移动</span
                >
              </template>
            </span>
          </div>
        </template>
        <template #fileSize="{ index, row }">
          <span v-if="row.fileSize">
            {{ proxy.Utils.size2Str(row.fileSize) }}</span
          >
        </template>
      </Table>
    </div>
    <div class="no-data" v-else>
      <div class="no-data-inner">
        <Icon iconName="no_data" :width="120" fit="fill"></Icon>
        <div class="tips">当前目录为空，上传你的第一个文件吧</div>
        <div class="op-list">
          <el-upload
            :show-file-list="false"
            :with-credentials="true"
            :multiple="true"
            :http-request="addFile"
            :accept="fileAccept"
          >
            <div class="op-item">
              <Icon iconName="file" :width="60"></Icon>
              <div>上传文件</div>
            </div>
          </el-upload>
          <div class="op-item" v-if="category == 'all'" @click="newFolder">
            <Icon iconName="folder" :width="60"></Icon>
            <div>新建目录</div>
          </div>
        </div>
      </div>
    </div>
    <!--预览-->
    <Preview ref="previewRef"> </Preview>
    <!--移动-->
    <FolderSelect
      ref="folderSelectRef"
      @folderSelect="moveFolderDone"
    ></FolderSelect>
  </div>
</template>

<script setup>
import CategoryInfo from "@/js/CategoryInfo.js";
import { ref, reactive, getCurrentInstance, nextTick, computed } from "vue";

const { proxy } = getCurrentInstance();

const emit = defineEmits(["addFile"]);
const addFile = (fileData) => {
  emit("addFile", { file: fileData.file, filePid: currentFolder.value.fileId });
};
const currentFolder = ref({ fileId: 0 });

const api = {
  loadDataList: "/file/loadDataList",
  rename: "/file/rename",
  newFolder: "/file/newFolder",
  getFolderInfo: "/file/getFolderInfo",
  delFile: "/file/delFile",
  changeFileFolder: "/file/changeFileFolder",
  createDownloadUrl: "/file/createDownloadUrl",
  download: "/api/file/download",
};

//添加文件回调
const reload = () => {
  showLoading.value = false;
  loadDataList();
};
defineExpose({
  reload,
});

//列表
const columns = [
  {
    label: "文件名",
    prop: "fileName",
    scopedSlots: "fileName",
  },
  {
    label: "修改时间",
    prop: "lastUpdateTime",
    width: 200,
  },
  {
    label: "大小",
    prop: "fileSize",
    scopedSlots: "fileSize",
    width: 200,
  },
];

const tableData = ref({});
const tableOptions = ref({
  extHeight: 50,
  selectType: "checkbox",
});

const fileNameFuzzy = ref();
const category = ref();
const showLoading = ref(true);

//搜索
const search = () => {
  showLoading.value = true;
  loadDataList();
};

// 过滤上传类型文件
const fileAccept = computed(() => {
  const categoryItem = CategoryInfo[category.value];
  return categoryItem ? categoryItem.accept : "*";
});

const loadDataList = async () => {
  let params = {
    pageNo: tableData.value.pageNo,
    pageSize: tableData.value.pageSize,
    fileNameFuzzy: fileNameFuzzy.value,
    filePid: currentFolder.value.fileId,
    category: category.value,
  };
  if (params.category !== "all") {
    delete params.filePid;
  }
  let result = await proxy.Request({
    url: api.loadDataList,
    showLoading: showLoading,
    params: params,
  });
  if (!result) {
    return;
  }
  tableData.value = result.data;
};
//多选 批量选择
const selectFileIdList = ref([]);
const rowSelected = (rows) => {
  selectFileIdList.value = [];
  rows.forEach((item) => {
    selectFileIdList.value.push(item.fileId);
  });
};

// 批量删除
const delFileBatch = () => {
  if (selectFileIdList.value.length == 0) {
    return;
  }
  proxy.Confirm(
    `你确定要删除这些文件吗？删除的文件可在10天内通过回收站还原`,
    async () => {
      let result = await proxy.Request({
        url: api.delFile,
        params: {
          fileIds: selectFileIdList.value.join(","),
        },
      });
      if (!result) {
        return;
      }
      loadDataList();
    }
  );
};

// 删除单个文件
const delFile = (row) => {
  console.log(row);
  proxy.Confirm(
    `你确定要删除【${row.fileName}】吗？删除的文件可在10天内通过回收站还原`,
    async () => {
      let result = await proxy.Request({
        url: api.delFile,
        params: {
          fileIds: row.fileId,
        },
      });
      if (!result) {
        return;
      }
      loadDataList();
    }
  );
};

// 展示操作按钮
const showOp = (row) => {
  tableData.value.list.forEach((element) => {
    element.showOp = false;
  });
  row.showOp = true;
};

const cancelShowOp = (row) => {
  row.showOp = false;
};

//编辑行
const editing = ref(false);
const editNameRef = ref();
//新建文件夹
const newFolder = () => {
  if (editing.value) {
    // 编辑中不能做其他操作
    return;
  }
  tableData.value.list.forEach((element) => {
    element.showOp = false; // 编辑中把其他行改为不可编辑
  });
  editing.value = true;
  // 给tableData增加一个关于新建文件夹数据
  tableData.value.list.unshift({
    showEdit: true,
    fileType: 0,
    fileId: "",
    filePid: 0,
  });
  nextTick(() => {
    editNameRef.value.focus();
  });
};
// 取消编辑或新建文价夹
const cancelNameEdit = (index) => {
  const fileData = tableData.value.list[index];
  if (fileData.fileId) {
    fileData.showEdit = false;
  } else {
    tableData.value.list.splice(index, 1);
  }
  editing.value = false;
};
// 保存编辑或新建文价夹
const saveNameEdit = async (index) => {
  const { fileId, filePid, fileNameReal } = tableData.value.list[index];
  if (fileNameReal == "" || fileNameReal.indexOf("/") != -1) {
    proxy.Message.warning("文件名不能为空且不能含有斜杠");
    return;
  }
  let url = api.rename;
  if (fileId == "") {
    url = api.newFolder;
  }
  let result = await proxy.Request({
    url: url,
    params: {
      fileId,
      filePid: filePid,
      fileName: fileNameReal,
    },
  });
  if (!result) {
    return;
  }
  tableData.value.list[index] = result.data;
  editing.value = false;
};

const editFileName = (index) => {
  if (tableData.value.list[0].fileId == "") {
    tableData.value.list.splice(0, 1);
    index = index - 1;
  }
  tableData.value.list.forEach((element) => {
    element.showEdit = false;
  });
  let cureentData = tableData.value.list[index];
  cureentData.showEdit = true;
  //编辑文件
  if (cureentData.folderType == 0) {
    cureentData.fileNameReal = cureentData.fileName.substring(
      0,
      cureentData.fileName.indexOf(".")
    );
    cureentData.fileSuffix = cureentData.fileName.substring(
      cureentData.fileName.indexOf(".")
    );
  } else {
    cureentData.fileNameReal = cureentData.fileName;
    cureentData.fileSuffix = "";
  }
  editing.value = true;
  nextTick(() => {
    editNameRef.value.focus();
  });
};

// 批量移动
const folderSelectRef = ref();
const currentMoveFile = ref({});

const moveFolder = (data) => {
  currentMoveFile.value = data;
  folderSelectRef.value.showFolderDialog(currentFolder.value.fileId);
};
const moveFolderBatch = () => {
  currentMoveFile.value = {};
  folderSelectRef.value.showFolderDialog(currentFolder.value.fileId);
};

const moveFolderDone = async (folderId) => {
  if (
    currentMoveFile.value.filePid === folderId ||
    currentFolder.value.fileId == folderId
  ) {
    proxy.Message.warning("文件正在当前目录，无需移动");
    return;
  }
  let filedIdsArray = [];
  if (currentMoveFile.value.fileId) {
    filedIdsArray.push(currentMoveFile.value.fileId);
  } else {
    filedIdsArray = filedIdsArray.concat(selectFileIdList.value);
  }
  let result = await proxy.Request({
    url: api.changeFileFolder,
    params: {
      fileIds: filedIdsArray.join(","),
      filePid: folderId,
    },
  });
  if (!result) {
    return;
  }
  folderSelectRef.value.close();

  loadDataList();
};

const navChange = (data) => {
  const { categoryId, curFolder } = data;
  category.value = categoryId;
  currentFolder.value = curFolder;
  loadDataList();
};

// 预览
const navigationRef = ref();
const previewRef = ref();
const preview = (data) => {
  if (data.folderType == 1) {
    navigationRef.value.openFolder(data);
    return;
  }
  if (data.status != 2) {
    proxy.Message.warning("文件正在转码中，无法预览");
    return;
  }
  previewRef.value.showPreview(data, 0);
};
</script>

<style lang="scss" scoped>
@import "@/assets/file.list.scss";
</style>