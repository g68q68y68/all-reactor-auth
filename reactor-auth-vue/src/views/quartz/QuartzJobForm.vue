<template>
  <el-dialog :model-value="visible" :title="isEdit?'编辑任务':'新增任务'" width="550px" :close-on-click-modal="false" @update:model-value="$emit('update:visible',$event)">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="任务名称" prop="jobName">
        <el-input v-model="form.jobName" placeholder="如 myJob" :disabled="isEdit" />
      </el-form-item>
      <el-form-item label="任务组" prop="jobGroup">
        <el-input v-model="form.jobGroup" placeholder="如 DEFAULT" :disabled="isEdit" />
      </el-form-item>
      <el-form-item label="Cron 表达式" prop="cronExpression">
        <el-input v-model="form.cronExpression" placeholder="0/10 * * * * ?" />
        <span class="form-tip">秒 分 时 日 月 周</span>
      </el-form-item>
      <el-form-item label="执行类" prop="jobClass">
        <el-input v-model="form.jobClass" placeholder="com.reactorAuth.quartz.job.XXXJob" />
      </el-form-item>
      <el-form-item label="参数(JSON)">
        <el-input v-model="form.params" type="textarea" :rows="3" placeholder='{"key":"value"}' />
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="form.description" type="textarea" :rows="2" placeholder="任务说明" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="$emit('update:visible',false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { createQuartzJob, updateQuartzJob } from '@/api/quartz'

const props = defineProps({ visible:Boolean, job:Object, isEdit:Boolean })
const emit = defineEmits(['update:visible','success'])

const formRef=ref(), submitting=ref(false)
const form = reactive({ jobName:'', jobGroup:'DEFAULT', cronExpression:'', jobClass:'', params:'', description:'' })
const rules = {
  jobName:[{required:true,message:'请输入',trigger:'blur'}],
  jobGroup:[{required:true,message:'请输入',trigger:'blur'}],
  cronExpression:[{required:true,message:'请输入',trigger:'blur'}],
  jobClass:[{required:true,message:'请输入',trigger:'blur'}]
}

watch(()=>props.job,(j)=>{
  Object.assign(form,{jobName:j?.jobName||'',jobGroup:j?.jobGroup||'DEFAULT',cronExpression:j?.cronExpression||'',jobClass:j?.jobClass||'',params:j?.params||'',description:j?.description||''})
},{immediate:true})

const handleSubmit=async()=>{
  if(!formRef.value)return
  await formRef.value.validate(async(valid)=>{
    if(!valid)return; submitting.value=true
    try{
      if(props.isEdit){await updateQuartzJob(props.job.id,{...form});ElMessage.success('更新成功')}
      else{await createQuartzJob({...form});ElMessage.success('创建成功')}
      emit('success')
    }catch(e){ElMessage.error(e.message||'操作失败')}
    finally{submitting.value=false}
  })
}
</script>

<style scoped>
.form-tip{font-size:12px;color:#909399;}
</style>
