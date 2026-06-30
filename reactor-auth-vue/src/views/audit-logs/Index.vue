<template>
  <div class="audit-page">
    <el-card>
      <template #header><span>操作日志</span></template>
      <el-table v-loading="loading" :data="list" stripe style="width:100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="操作人" width="100" />
        <el-table-column prop="module" label="模块" width="100" />
        <el-table-column prop="action" label="操作" width="100" />
        <el-table-column prop="description" label="详情" min-width="160" show-overflow-tooltip />
        <el-table-column prop="url" label="接口" min-width="180" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="130" />
        <el-table-column prop="status" label="结果" width="70">
          <template #default="{ row }">
            <el-tag :type="row.status===1?'success':'danger'" size="small">{{ row.status===1?'成功':'失败' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="时间" width="170">
          <template #default="{ row }">{{ row.createdAt?.replace('T',' ') }}</template>
        </el-table-column>
      </el-table>
      <AppPagination v-model:page="page" v-model:size="size" :total="total" @change="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { getAuditLogs } from '@/api/audit-log'
import { usePagination } from '@/composables/usePagination'
import AppPagination from '@/components/common/AppPagination.vue'

const { list, total, loading, page, size, fetchData } = usePagination(getAuditLogs)
onMounted(() => fetchData())
</script>

<style scoped>
.audit-page { padding:0; }
</style>
