<template>
  <div class="feedback-page">
    <div class="page-header">
      <div class="header-left">
        <h2>投诉反馈</h2>
        <p>管理用户投诉反馈信息</p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 150px">
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已处理" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        :data="feedbackList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="content" label="反馈内容" min-width="300" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="处理备注" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="提交时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="success" link size="small" @click="handleEdit(row)">
              处理
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 查看/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '处理投诉反馈' : '查看投诉反馈'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="姓名">
          <el-input v-model="formData.name" disabled />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="formData.phone" disabled />
        </el-form-item>
        <el-form-item label="反馈内容">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="5"
            disabled
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="0">待处理</el-radio>
            <el-radio :label="1">处理中</el-radio>
            <el-radio :label="2">已处理</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入处理备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="isEdit" type="primary" @click="handleSave" :loading="saving">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import request from '@/api'

// 类型定义
interface Feedback {
  id: number
  name: string
  phone: string
  content: string
  status: number
  remark?: string
  createdAt: string
}

// 数据
const loading = ref(false)
const feedbackList = ref<Feedback[]>([])
const dialogVisible = ref(false)
const saving = ref(false)
const isEdit = ref(false)
const currentId = ref<number | null>(null)

const formRef = ref<FormInstance>()

// 搜索表单
const searchForm = reactive({
  status: null as number | null
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表单数据
const formData = reactive({
  name: '',
  phone: '',
  content: '',
  status: 0,
  remark: ''
})

// 表单验证规则
const formRules = {
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取状态类型
const getStatusType = (status: number) => {
  const types: Record<number, string> = {
    0: 'warning',
    1: 'info',
    2: 'success'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: number) => {
  const texts: Record<number, string> = {
    0: '待处理',
    1: '处理中',
    2: '已处理'
  }
  return texts[status] || '未知'
}

// 获取列表
const fetchFeedbackList = async () => {
  try {
    loading.value = true
    const params: any = {
      current: pagination.current,
      size: pagination.size
    }
    if (searchForm.status !== null) {
      params.status = searchForm.status
    }
    const result = await request.get('/feedback/list', { params })
    if (result.data) {
      feedbackList.value = result.data.records || []
      pagination.total = result.data.total || 0
    }
  } catch (error: any) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchFeedbackList()
}

// 重置
const handleReset = () => {
  searchForm.status = null
  handleSearch()
}

// 查看
const handleView = async (row: Feedback) => {
  isEdit.value = false
  currentId.value = row.id
  try {
    const result = await request.get(`/feedback/${row.id}`)
    if (result.data) {
      Object.assign(formData, result.data)
    }
  } catch (error: any) {
    console.error('获取详情失败:', error)
  }
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row: Feedback) => {
  isEdit.value = true
  currentId.value = row.id
  try {
    const result = await request.get(`/feedback/${row.id}`)
    if (result.data) {
      Object.assign(formData, result.data)
    }
  } catch (error: any) {
    console.error('获取详情失败:', error)
  }
  dialogVisible.value = true
}

// 保存
const handleSave = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    saving.value = true

    if (currentId.value) {
      await request.put(`/feedback/${currentId.value}`, formData)
    }
    
    ElMessage.success('更新成功')
    dialogVisible.value = false
    fetchFeedbackList()
  } catch (error: any) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// 删除
const handleDelete = (row: Feedback) => {
  ElMessageBox.confirm(
    `确定要删除这条投诉反馈吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await request.delete(`/feedback/${row.id}`)
      ElMessage.success('删除成功')
      fetchFeedbackList()
    } catch (error: any) {
      console.error('删除失败:', error)
    }
  })
}

// 分页
const handleSizeChange = () => {
  fetchFeedbackList()
}

const handleCurrentChange = () => {
  fetchFeedbackList()
}

// 页面加载时获取数据
onMounted(() => {
  fetchFeedbackList()
})
</script>

<style lang="scss" scoped>
.feedback-page {
  .page-header {
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

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>

