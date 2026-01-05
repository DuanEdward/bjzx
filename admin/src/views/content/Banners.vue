<template>
  <div class="banners-page">
    <div class="page-header">
      <div class="header-left">
        <h2>Banner管理</h2>
        <p>管理系统首页Banner图片</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增Banner
        </el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        :data="bannerList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column label="图片" width="200">
          <template #default="{ row }">
            <el-image
              v-if="row.image"
              :src="row.image"
              :preview-src-list="[row.image]"
              style="width: 100px; height: 60px"
              fit="cover"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="link" label="跳转链接" min-width="200" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="100" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑Banner' : '新增Banner'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="formData.title"
            placeholder="请输入Banner标题"
            maxlength="100"
          />
        </el-form-item>
        <el-form-item label="图片URL" prop="image">
          <el-input
            v-model="formData.image"
            placeholder="请输入图片URL"
            maxlength="500"
          />
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input
            v-model="formData.link"
            placeholder="请输入跳转链接（可选）"
            maxlength="500"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number
            v-model="formData.sort"
            :min="0"
            :max="999"
            placeholder="排序值，数字越小越靠前"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="0">禁用</el-radio>
            <el-radio :label="1">启用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import request from '@/api'

// 类型定义
interface Banner {
  id: number
  title: string
  image: string
  link?: string
  sort: number
  status: number
  createdAt: string
  updatedAt: string
}

// 数据
const loading = ref(false)
const bannerList = ref<Banner[]>([])
const dialogVisible = ref(false)
const saving = ref(false)
const isEdit = ref(false)
const currentId = ref<number | null>(null)

const formRef = ref<FormInstance>()

// 表单数据
const formData = reactive({
  title: '',
  image: '',
  link: '',
  sort: 0,
  status: 1
})

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入Banner标题', trigger: 'blur' }
  ],
  image: [
    { required: true, message: '请输入图片URL', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '请输入排序值', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取Banner列表
const fetchBannerList = async () => {
  try {
    loading.value = true
    const result = await request.get('/banner/list')
    bannerList.value = result.data || []
  } catch (error: any) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}

// 新增
const handleCreate = () => {
  isEdit.value = false
  currentId.value = null
  Object.assign(formData, {
    title: '',
    image: '',
    link: '',
    sort: 0,
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: Banner) => {
  isEdit.value = true
  currentId.value = row.id
  Object.assign(formData, {
    title: row.title,
    image: row.image,
    link: row.link || '',
    sort: row.sort,
    status: row.status
  })
  dialogVisible.value = true
}

// 保存
const handleSave = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    saving.value = true

    if (isEdit.value && currentId.value) {
      await request.put(`/banner/${currentId.value}`, formData)
    } else {
      await request.post('/banner', formData)
    }
    
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    fetchBannerList()
  } catch (error: any) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// 删除
const handleDelete = (row: Banner) => {
  ElMessageBox.confirm(
    `确定要删除Banner"${row.title}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await request.delete(`/banner/${row.id}`)
      ElMessage.success('删除成功')
      fetchBannerList()
    } catch (error: any) {
      console.error('删除失败:', error)
    }
  })
}

// 页面加载时获取数据
onMounted(() => {
  fetchBannerList()
})
</script>

<style lang="scss" scoped>
.banners-page {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20px;

    .header-left {
      h2 {
        margin: 0 0 8px 0;
        font-size: 24px;
        font-weight: 500;
        color: #303133;
      }

      p {
        margin: 0;
        color: #909399;
        font-size: 14px;
      }
    }
  }
}
</style>
