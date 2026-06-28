<template>
  <div class="app-pagination">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="currentSize"
      :page-sizes="pageSizes"
      :total="total"
      :layout="layout"
      @size-change="onSizeChange"
      @current-change="onPageChange"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  page: { type: Number, required: true },
  size: { type: Number, required: true },
  total: { type: Number, required: true },
  pageSizes: { type: Array, default: () => [10, 20, 50, 100] },
  layout: { type: String, default: 'total, sizes, prev, pager, next, jumper' }
})

const emit = defineEmits(['update:page', 'update:size', 'change'])

const currentPage = computed({
  get: () => props.page,
  set: (v) => emit('update:page', v)
})
const currentSize = computed({
  get: () => props.size,
  set: (v) => emit('update:size', v)
})

const onPageChange = (val) => {
  emit('update:page', val)
  emit('change')
}
const onSizeChange = (val) => {
  emit('update:size', val)
  emit('change')
}
</script>

<style scoped>
.app-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
