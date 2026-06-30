<template>
  <div class="quartz-page">
    <el-card>
      <template #header>
        <div class="page-header"><span>定时任务管理</span><el-button type="primary" @click="handleAdd">新增任务</el-button></div>
      </template>
      <el-table v-loading="loading" :data="list" stripe style="width:100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="jobName" label="任务名称" min-width="140" />
        <el-table-column prop="jobGroup" label="任务组" width="120" />
        <el-table-column prop="cronExpression" label="Cron 表达式" width="150" />
        <el-table-column prop="jobClass" label="执行类" min-width="260" show-overflow-tooltip />
        <el-table-column prop="description" label="描述" min-width="160" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status===1?'success':'warning'">{{ row.status===1?'运行中':'已暂停' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleTrigger(row)">执行一次</el-button>
            <el-button v-if="row.status===1" size="small" type="warning" @click="handlePause(row)">暂停</el-button>
            <el-button v-else size="small" type="success" @click="handleResume(row)">恢复</el-button>
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <AppPagination v-model:page="page" v-model:size="size" :total="total" @change="fetchData" />
    </el-card>
    <QuartzJobForm v-model:visible="dialogVisible" :job="current" :is-edit="isEdit" @success="onFormSuccess" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getQuartzJobs, deleteQuartzJob, pauseJob, resumeJob, triggerJob } from '@/api/quartz'
import { usePagination } from '@/composables/usePagination'
import AppPagination from '@/components/common/AppPagination.vue'
import QuartzJobForm from './QuartzJobForm.vue'

const { list, total, loading, page, size, fetchData } = usePagination(getQuartzJobs)
const dialogVisible = ref(false), current = ref(null), isEdit = ref(false)

const handleAdd = () => { current.value = null; isEdit.value = false; dialogVisible.value = true }
const handleEdit = (r) => { current.value = { ...r }; isEdit.value = true; dialogVisible.value = true }
const handleDelete = (r) => {
  ElMessageBox.confirm(`确定要删除 "${r.jobName}"？`, '确认', { type:'warning' })
    .then(async () => { await deleteQuartzJob(r.id); ElMessage.success('已删除'); fetchData() }).catch(()=>{})
}
const handlePause = async (r) => { await pauseJob(r.id); ElMessage.success('已暂停'); fetchData() }
const handleResume = async (r) => { await resumeJob(r.id); ElMessage.success('已恢复'); fetchData() }
const handleTrigger = async (r) => { await triggerJob(r.id); ElMessage.success('已触发执行'); }
const onFormSuccess = () => { dialogVisible.value = false; fetchData() }
onMounted(() => fetchData())
</script>

<style scoped>
.quartz-page { padding:0; }
.page-header { display:flex; align-items:center; justify-content:space-between; }
</style>
