<template>
  <div class="resources-page">
    <div class="page-header">
      <div class="header-left">
        <h2>资源管理</h2>
        <p>管理系统中的可下载资源文件</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增资源
        </el-button>
      </div>
    </div>

    <!-- 搜索表单 -->
    <el-card class="search-card" shadow="never">
      <el-form
        :model="searchForm"
        inline
        @submit.prevent="handleSearch"
        label-width="80px"
      >
        <el-form-item label="标题">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入资源标题"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select
            v-model="searchForm.categoryId"
            placeholder="请选择分类"
            clearable
            style="width: 200px"
          >
            <el-option label="全部" :value="null" />
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" :value="null" />
            <el-option label="禁用" :value="0" />
            <el-option label="启用" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        :data="resourceList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="fileName" label="文件名" min-width="150" show-overflow-tooltip />
        <el-table-column label="文件大小" width="120" align="right">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="downloadCount" label="下载次数" width="100" align="center" />
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

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑资源' : '新增资源'"
      width="700px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="资源标题" prop="title">
          <el-input
            v-model="formData.title"
            placeholder="请输入资源标题"
            maxlength="200"
          />
        </el-form-item>
        <el-form-item label="资源分类" prop="categoryId">
          <el-select
            v-model="formData.categoryId"
            placeholder="请选择分类"
            style="width: 100%"
          >
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="文件URL" prop="fileUrl">
          <el-input
            v-model="formData.fileUrl"
            placeholder="请输入文件URL"
            maxlength="500"
          />
        </el-form-item>
        <el-form-item label="文件名" prop="fileName">
          <el-input
            v-model="formData.fileName"
            placeholder="请输入文件名"
            maxlength="200"
          />
        </el-form-item>
        <el-form-item label="文件大小">
          <el-input-number
            v-model="formData.fileSize"
            :min="0"
            placeholder="文件大小（字节）"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="文件类型">
          <el-input
            v-model="formData.fileType"
            placeholder="请输入文件类型（如：pdf, doc, xls）"
            maxlength="50"
          />
        </el-form-item>
        <el-form-item label="资源描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入资源描述"
            maxlength="500"
            show-word-limit
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
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import request from '@/api'

// 类型定义
interface Resource {
  id: number
  title: string
  description?: string
  fileUrl: string
  fileName: string
  fileSize: number
  fileType?: string
  downloadCount: number
  categoryId: number
  categoryName?: string
  status: number
  createdAt: string
  updatedAt: string
}

interface ResourceCategory {
  id: number
  name: string
  code: string
}

// 数据
const loading = ref(false)
const total = ref(0)
const resourceList = ref<Resource[]>([])
const categories = ref<ResourceCategory[]>([])
const dialogVisible = ref(false)
const saving = ref(false)
const isEdit = ref(false)
const currentId = ref<number | null>(null)

const formRef = ref<FormInstance>()

// 搜索表单
const searchForm = reactive({
  keyword: '',
  categoryId: null as number | null,
  status: null as number | null
})

// 分页配置
const pagination = reactive({
  current: 1,
  size: 10
})

// 表单数据
const formData = reactive({
  title: '',
  description: '',
  fileUrl: '',
  fileName: '',
  fileSize: 0,
  fileType: '',
  categoryId: null as number | null,
  status: 1
})

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入资源标题', trigger: 'blur' }
  ],
  fileUrl: [
    { required: true, message: '请输入文件URL', trigger: 'blur' }
  ],
  fileName: [
    { required: true, message: '请输入文件名', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取资源分类列表
const fetchCategories = async () => {
  try {
    // 暂时使用硬编码的分类
    categories.value = [
      { id: 1, name: '技术文档', code: 'document' },
      { id: 2, name: '研究报告', code: 'report' },
      { id: 3, name: '会议资料', code: 'conference' },
      { id: 4, name: '标准规范', code: 'standard' },
      { id: 5, name: '学习资料', code: 'material' }
    ]
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取资源列表
const fetchResourceList = async () => {
  try {
    loading.value = true
    const params: any = {
      page: pagination.current,
      pageSize: pagination.size
    }

    if (searchForm.keyword) {
      // 注意：后端可能不支持keyword参数，需要检查
    }
    if (searchForm.categoryId) {
      params.categoryId = searchForm.categoryId
    }

    const result = await request.get('/resources/list', { params })
    resourceList.value = result.data.list || []
    total.value = result.data.total || 0
  } catch (error: any) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchResourceList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.categoryId = null
  searchForm.status = null
  pagination.current = 1
  fetchResourceList()
}

// 新增
const handleCreate = () => {
  isEdit.value = false
  currentId.value = null
  Object.assign(formData, {
    title: '',
    description: '',
    fileUrl: '',
    fileName: '',
    fileSize: 0,
    fileType: '',
    categoryId: null,
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: Resource) => {
  isEdit.value = true
  currentId.value = row.id
  Object.assign(formData, {
    title: row.title,
    description: row.description || '',
    fileUrl: row.fileUrl,
    fileName: row.fileName,
    fileSize: row.fileSize || 0,
    fileType: row.fileType || '',
    categoryId: row.categoryId,
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
      await request.put(`/resources/${currentId.value}`, formData)
    } else {
      await request.post('/resources', formData)
    }
    
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    fetchResourceList()
  } catch (error: any) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// 删除
const handleDelete = (row: Resource) => {
  ElMessageBox.confirm(
    `确定要删除资源"${row.title}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await request.delete(`/resources/${row.id}`)
      ElMessage.success('删除成功')
      fetchResourceList()
    } catch (error: any) {
      console.error('删除失败:', error)
    }
  })
}

// 格式化文件大小
const formatFileSize = (bytes: number) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  fetchResourceList()
}

// 当前页改变
const handleCurrentChange = (current: number) => {
  pagination.current = current
  fetchResourceList()
}

// 页面加载时获取数据
onMounted(() => {
  fetchCategories()
  fetchResourceList()
})
</script>

<style lang="scss" scoped>
.resources-page {
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

  .search-card {
    margin-bottom: 20px;
  }

  .table-card {
    .pagination-wrapper {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>
