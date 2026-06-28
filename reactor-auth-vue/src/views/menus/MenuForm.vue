<template>
  <el-dialog :model-value="visible" :title="isEdit ? '编辑菜单' : '新增菜单'" width="550px" :close-on-click-modal="false" @update:model-value="$emit('update:visible', $event)">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="上级菜单">
        <el-tree-select v-model="form.parentId" :data="menuStore.menuTree" :props="{ label: 'title', value: 'id', children: 'children' }" placeholder="顶级菜单（留空）" clearable check-strictly style="width: 100%" />
      </el-form-item>
      <el-form-item label="菜单名称" prop="title"><el-input v-model="form.title" placeholder="侧边栏显示的名称" /></el-form-item>
      <el-form-item label="路由名称" prop="name"><el-input v-model="form.name" placeholder="如 Dashboard" :disabled="isEdit" /></el-form-item>
      <el-form-item label="路由路径" prop="path"><el-input v-model="form.path" placeholder="如 /users" /></el-form-item>
      <el-form-item label="重定向"><el-input v-model="form.redirect" placeholder="如 /dashboard，选填" /></el-form-item>
      <el-form-item label="组件路径">
        <el-input v-model="form.component" placeholder="如 @/views/users/Index.vue（目录留空）" />
        <span class="form-tip">格式 @/views/xxx/Index.vue，构建时自动扫描</span>
      </el-form-item>
      <el-form-item label="图标">
        <el-select v-model="form.icon" placeholder="选择图标" filterable clearable style="width: 100%">
          <el-option v-for="icon in iconList" :key="icon" :label="icon" :value="icon">
            <div style="display:flex;align-items:center"><el-icon style="margin-right:8px"><component :is="icon" /></el-icon><span>{{ icon }}</span></div>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="form.type" style="width: 100%"><el-option label="菜单 (MENU)" value="MENU" /><el-option label="按钮 (BUTTON)" value="BUTTON" /><el-option label="接口 (API)" value="API" /></el-select>
      </el-form-item>
      <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="-999" :max="999" /></el-form-item>
      <el-form-item label="需要认证"><el-switch v-model="form.requiresAuth" :active-value="1" :inactive-value="0" active-text="是" inactive-text="否" /></el-form-item>
      <el-form-item label="隐藏菜单"><el-switch v-model="form.hidden" :active-value="1" :inactive-value="0" active-text="隐藏" inactive-text="显示" /><span class="form-tip">隐藏后路由仍可访问，但不在侧边栏显示</span></el-form-item>
      <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useMenuStore } from '@/store/modules/menu'
import { createMenu, updateMenu } from '@/api/menu'
import router from '@/router'
import { loadDynamicRoutes } from '@/router/dynamicRoutes'

const props = defineProps({
  visible: { type: Boolean, default: false },
  menu: { type: Object, default: null },
  parentId: { type: [Number, String], default: null },
  isEdit: { type: Boolean, default: false }
})
const emit = defineEmits(['update:visible', 'success'])
const menuStore = useMenuStore()

const formRef = ref(), submitting = ref(false)
const form = reactive({ parentId: null, path: '', redirect: '', name: '', component: '', title: '', icon: '', type: 'MENU', requiresAuth: 1, hidden: 0, sortOrder: 0, status: 1 })
const rules = {
  title: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  name: [{ required: true, message: '请输入路由名称', trigger: 'blur' }],
  path: [{ required: true, message: '请输入路由路径', trigger: 'blur' }]
}

const iconList = ['DataAnalysis','User','DataLine','Avatar','Lock','Menu','Setting','Document','Folder','List','Search','Edit','Plus','Delete','Upload','Download','Link','Share','House','Message','Star','Picture','VideoCamera','Monitor','Tools','Key','Unlock','Bell','Warning','InfoFilled']

watch(() => props.menu, (m) => {
  Object.assign(form, {
    parentId: m?.parentId || props.parentId || null, path: m?.path || '', redirect: m?.redirect || '', name: m?.name || '', component: m?.component || '',
    title: m?.title || '', icon: m?.icon || '', type: m?.type || 'MENU',
    requiresAuth: m?.requiresAuth != null ? (m.requiresAuth ? 1 : 0) : 1,
    hidden: m?.hidden ? 1 : 0, sortOrder: m?.sortOrder ?? 0, status: m?.status ?? 1
  })
}, { immediate: true })

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (props.isEdit) { await updateMenu(props.menu.id, { ...form }); ElMessage.success('更新成功') }
      else { await createMenu({ ...form }); ElMessage.success('创建成功') }
      menuStore.setDynamicRoutesLoaded(false)
      await loadDynamicRoutes(router)
      emit('success')
    } catch (e) { ElMessage.error(e.message || '操作失败') }
    finally { submitting.value = false }
  })
}
</script>

<style scoped>
.dialog-footer { display: flex; justify-content: flex-end; gap: 12px; }
.form-tip { font-size: 12px; color: #909399; line-height: 1.5; display: block; margin-top: 4px; }
</style>
