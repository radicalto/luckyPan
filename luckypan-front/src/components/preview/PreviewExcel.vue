<template>
  <div v-html="excelContent" class="table-info"></div>
</template>

<script setup>
import * as XLSX from "xlsx";
import { ref, reactive, getCurrentInstance, onMounted } from "vue";
const { proxy } = getCurrentInstance();

const props = defineProps({
  url: {
    type: String,
  },
});

const excelContent = ref();
const initExcel = async () => {
  let result = await proxy.Request({
    url: props.url,
    responseType: "arraybuffer",
  });
  if (!result) {
    return;
  }
  let workbook = XLSX.read(new Uint8Array(result), { type: "array" }); // 解析数据
  var worksheet = workbook.Sheets[workbook.SheetNames[0]]; // workbook.SheetNames 下存的是该文件每个工作表名字,这里取出第一个工作表
  excelContent.value = XLSX.utils.sheet_to_html(worksheet);
};
onMounted(() => {
  initExcel();
});
</script>
  
  <style lang="scss" scoped>
// .table-info {
//   table {
//     border: 1px solid #000;
//     margin-top: 10px;
//   }
//   th {
//     border: 1px solid #000;
//   }
//   td {
//     border: 1px solid #000;
//     text-align: center;
//     min-width: 20px;
//     height: 20px;
//     line-height: 20px;
//     padding: 4px;
//     color: #3e3e3e;
//   }
// }

.table-info {
  width: 100%;
  padding: 10px;
  :deep table {
    width: 100%;
    border-collapse: collapse;
    td {
      border: 1px solid #ddd;
      border-collapse: collapse;
      padding: 5px;
      height: 30px;
      min-width: 50px;
    }
  }
}
</style>
  