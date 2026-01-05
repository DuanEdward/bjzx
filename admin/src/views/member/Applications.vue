<template>
  <div class="applications-page">
    <div class="page-header">
      <div class="header-left">
        <h2>入会申请</h2>
        <p>处理会员入会申请</p>
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
        <el-form-item label="姓名">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入会员姓名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" :value="null" />
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
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
        :data="applicationList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="title" label="职称" width="150" />
        <el-table-column prop="company" label="工作单位" min-width="200" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column label="等级" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)">
              {{ getLevelText(row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="申请时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="success"
              link
              size="small"
              @click="handleApprove(row)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="danger"
              link
              size="small"
              @click="handleReject(row)"
            >
              拒绝
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

    <!-- 查看对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="查看申请详情"
      width="700px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="姓名">{{ currentMember.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ currentMember.gender }}</el-descriptions-item>
        <el-descriptions-item label="出生年月">{{ currentMember.birthDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ currentMember.title || '-' }}</el-descriptions-item>
        <el-descriptions-item label="工作单位" :span="2">{{ currentMember.company || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentMember.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentMember.email }}</el-descriptions-item>
        <el-descriptions-item label="研究方向" :span="2">{{ currentMember.research || '-' }}</el-descriptions-item>
        <el-descriptions-item label="会员等级">
          <el-tag :type="getLevelType(currentMember.level)">
            {{ getLevelText(currentMember.level) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentMember.status)">
            {{ getStatusText(currentMember.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="个人简介" :span="2">
          <div style="white-space: pre-wrap;">{{ currentMember.introduction || '-' }}</div>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentMember.reviewRemark" label="审核意见" :span="2">
          {{ currentMember.reviewRemark }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentMember.reviewedAt" label="审核时间" :span="2">
          {{ currentMember.reviewedAt }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
        <el-button
          v-if="currentMember.status === 0"
          type="success"
          @click="handleApprove(currentMember)"
        >
          通过
        </el-button>
        <el-button
          v-if="currentMember.status === 0"
          type="danger"
          @click="handleReject(currentMember)"
        >
          拒绝
        </el-button>
      </template>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog
      v-model="reviewDialogVisible"
      :title="reviewType === 'approve' ? '通过申请' : '拒绝申请'"
      width="500px"
    >
      <el-form
        ref="reviewFormRef"
        :model="reviewForm"
        :rules="reviewFormRules"
        label-width="100px"
      >
        <el-form-item
          v-if="reviewType === 'reject'"
          label="拒绝原因"
          prop="reviewRemark"
        >
          <el-input
            v-model="reviewForm.reviewRemark"
            type="textarea"
            :rows="4"
            placeholder="请输入拒绝原因"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item v-else label="审核意见">
          <el-input
            v-model="reviewForm.reviewRemark"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见（可选）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleReviewSubmit" :loading="reviewing">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import request from '@/api'

// 类型定义
interface Member {
  id: number
  name: string
  gender: string
  birthDate?: string
  title: string
  company: string
  email: string
  phone: string
  research?: string
  level: number
  introduction?: string
  status: number
  reviewRemark?: string
  reviewedAt?: string
  createdAt: string
  updatedAt: string
}

// 数据
const loading = ref(false)
const total = ref(0)
const applicationList = ref<Member[]>([])
const viewDialogVisible = ref(false)
const reviewDialogVisible = ref(false)
const reviewing = ref(false)
const reviewType = ref<'approve' | 'reject'>('approve')
const currentMember = ref<Member>({} as Member)

const reviewFormRef = ref<FormInstance>()

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: 0 // 默认只显示待审核的
})

// 分页配置
const pagination = reactive({
  current: 1,
  size: 10
})

// 审核表单
const reviewForm = reactive({
  reviewRemark: ''
})

// 审核表单验证规则
const reviewFormRules = {
  reviewRemark: [
    { required: true, message: '请输入拒绝原因', trigger: 'blur' }
  ]
}

// 获取申请列表
const fetchApplicationList = async () => {
  try {
    loading.value = true
    const params: any = {
      page: pagination.current,
      pageSize: pagination.size
    }

    if (searchForm.status !== null) {
      params.status = searchForm.status
    }

    const result = await request.get('/member/list', { params })
    applicationList.value = result.data.list || []
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
  fetchApplicationList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = 0
  pagination.current = 1
  fetchApplicationList()
}

// 查看
const handleView = (row: Member) => {
  currentMember.value = { ...row }
  viewDialogVisible.value = true
}

// 通过
const handleApprove = (row: Member) => {
  currentMember.value = { ...row }
  reviewType.value = 'approve'
  reviewForm.reviewRemark = ''
  reviewDialogVisible.value = true
}

// 拒绝
const handleReject = (row: Member) => {
  currentMember.value = { ...row }
  reviewType.value = 'reject'
  reviewForm.reviewRemark = ''
  reviewDialogVisible.value = true
}

// 提交审核
const handleReviewSubmit = async () => {
  if (reviewType.value === 'reject' && !reviewFormRef.value) return

  if (reviewType.value === 'reject') {
    try {
      await reviewFormRef.value.validate()
    } catch {
      return
    }
  }

  try {
    reviewing.value = true
    const status = reviewType.value === 'approve' ? 1 : 2
    await request.post(`/member/${currentMember.value.id}/review`, {
      status,
      reviewRemark: reviewForm.reviewRemark || ''
    })
    
    ElMessage.success(reviewType.value === 'approve' ? '审核通过' : '已拒绝申请')
    reviewDialogVisible.value = false
    viewDialogVisible.value = false
    fetchApplicationList()
  } catch (error: any) {
    console.error('审核失败:', error)
  } finally {
    reviewing.value = false
  }
}

// 获取等级类型
const getLevelType = (level: number) => {
  switch (level) {
    case 1: return 'info'
    case 2: return 'success'
    case 3: return 'warning'
    default: return 'info'
  }
}

// 获取等级文本
const getLevelText = (level: number) => {
  switch (level) {
    case 1: return '普通会员'
    case 2: return '高级会员'
    case 3: return '理事'
    default: return '未知'
  }
}

// 获取状态类型
const getStatusType = (status: number) => {
  switch (status) {
    case 0: return 'info'
    case 1: return 'success'
    case 2: return 'danger'
    default: return 'info'
  }
}

// 获取状态文本
const getStatusText = (status: number) => {
  switch (status) {
    case 0: return '待审核'
    case 1: return '已通过'
    case 2: return '已拒绝'
    default: return '未知'
  }
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  fetchApplicationList()
}

// 当前页改变
const handleCurrentChange = (current: number) => {
  pagination.current = current
  fetchApplicationList()
}

// 页面加载时获取数据
onMounted(() => {
  fetchApplicationList()
})
</script>

<style lang="scss" scoped>
.applications-page {
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
