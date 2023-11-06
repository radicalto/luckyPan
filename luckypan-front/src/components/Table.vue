<template>
  <div>
    <el-table
      ref="dataTable"
      :data="dataSource.list || []"
      :height="tableHight"
      :stripe="options.stripe"
      :border="options.border"
      header-row-class-name="table-header-row"
      highlight-current-row
      @row-click="handleRowClick"
      @selection-change="handleSelectionChange"
    >
      <!-- 
ref="dataTable": 设置组件的引用名称为 "dataTable"，可以在其他地方通过该引用访问该组件。
:data="dataSource.list || []": 绑定数据源为 dataSource.list，如果不存在则绑定一个空数组([])。
:height="tableHight": 设置表格高度为变量 tableHight 的值。
:stripe="options.stripe": 根据 options.stripe 变量的值来决定是否给表格添加斑马纹样式。
:border="options.border": 根据 options.border 变量的值来决定是否给表格添加边框样式。
header-row-class-name="table-header-row": 为表头行指定自定义样式类名为 "table-header-row"。
highlight-current-row: 当前行高亮显示，表示选中状态或鼠标悬停在行上时会有特殊效果。
@row-click="handleRowClick": 监听行点击事件，并调用方法 handleRowClick 处理点击事件。
@selection-change="handleSelectionChange": 监听选择变化事件，并调用方法 handleSelectionChange 处理选择变化
       -->
      <!-- selection选择框 -->
      <el-table-column
        v-if="options.selectType && options.selectType === 'checkbox'"
        type="selection"
        width="50"
        align="center"
      ></el-table-column>
      <!-- 序号 -->
      <el-table-column
        v-if="options.showIndex"
        label="序号"
        type="index"
        width="60"
        align="center"
      ></el-table-column>
      <!-- 数据列 -->
      <template v-for="(column, index) in columns">
        <template v-if="column.scopedSlots">
          <el-table-column
            :key="index"
            :prop="column.prop"
            :label="column.label"
            :align="column.align || 'left'"
            :width="column.width"
          >
            <template #default="scope">
              <slot
                :name="column.scopedSlots"
                :index="scope.$index"
                :row="scope.row"
              >
              </slot>
            </template>
          </el-table-column>
        </template>
        <template v-else>
          <el-table-column
            :key="index"
            :prop="column.prop"
            :label="column.label"
            :align="column.align || 'left'"
            :width="column.width"
            :fixed="column.fixed"
          ></el-table-column>
        </template>
      </template>
    </el-table>

    <!-- 分页 -->
    <div class="pagination" v-if="showPagination">
      <el-pagination
        v-if="dataSource.totalCount"
        background
        :total="dataSource.totalCount"
        :page-sizes="[15, 30, 50, 100]"
        :page-size="dataSource.pageSize"
        :current-page.sync="dataSource.pageNo"
        :layout="layout"
        @size-change="handlePageSizeChange"
        @current-change="handlePageNoChange"
        style="text-align: right"
      >
      </el-pagination>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
const emit = defineEmits(["rowSelected", "rowClick"]);
const props = defineProps({
  dataSource: Object,
  showPagination: {
    type: Boolean,
    default: true,
  },
  showPageSize: {
    type: Boolean,
    default: true,
  },
  options: {
    type: Object,
    default: {
      extHeight: 0,
      showIndex: false,
    },
  },
  columns: Array,
  fetch: Function, // 获取数据函数
  initFetch: {
    type: Boolean,
    default: true,
  },
});

const layout = computed(() => {
  return `total, ${props.showPageSize ? "size" : ""}, prev,pager,next,jumper`;
});
// 顶部60 内容区域距离顶部20， 内容上下间距15*2 分页区域高度46
const topHeight = 60 + 20 + 30 + 46;

const tableHight = ref(
  props.options.tableHight
    ? props.options.tableHight
    : window.innerHeight - topHeight - props.options.extHeight
);

// 初始化
const init = () => {
  if (props.initFetch && props.fetch) {
    props.fetch();
  }
};
init();

const dataTable = ref();
// 清除选中
const clearSelection = () => {
  dataTable.value.clearSelection();
};
// 设置行选中
const setCurrentRow = (rowKey, rowValue) => {
  let row = props.dataSource.list.find((item) => {
    return item[rowKey] == rowValue;
  });
  dataTable.value.setCurrentRow(row);
};
// 将子组件暴露出去 使组件能调用
defineExpose({
  clearSelection,
  setCurrentRow,
});

// 行点击
const handleRowClick = (row) => {
  emit("rowClick", row);
};

// 多选
const handleSelectionChange = (row) => {
  emit("rowSelected", row);
};

// 切换每页大小
const handlePageSizeChange = (size) => {
  props.dataSource.pageSize = size;
  props.dataSource.pageNo = 1;
  props.fetch();
};

// 切换页码
const handlePageNoChange = (pageNo) => {
  props.dataSource.pageNo = pageNo;
  props.fetch();
};
</script>

<style lang="scss" scoped>
.pagination {
  padding-top: 10px;
  padding-right: 10px;
}
.el-pagination {
  justify-content: right;
}

:deep .el-table_cell {
  padding: 4px 0;
}
</style>