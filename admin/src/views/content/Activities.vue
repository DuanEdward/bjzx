<template>
  <div class="activities-page">
    <div class="page-header">
      <div class="header-left">
        <h2>活动管理</h2>
        <p>管理系统中的活动信息</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增活动
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
            placeholder="请输入活动标题"
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
            <el-option label="未开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已结束" :value="2" />
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
        :data="activityList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="location" label="地点" width="150" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column label="参与人数" width="120" align="center">
          <template #default="{ row }">
            {{ row.currentParticipants || 0 }} / {{ row.maxParticipants || '不限' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="报名" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isRegistrationOpen === 1 ? 'success' : 'info'" size="small">
              {{ row.isRegistrationOpen === 1 ? '开放' : '关闭' }}
            </el-tag>
          </template>
        </el-table-column>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import request from '@/api'

const router = useRouter()

// 类型定义
interface Activity {
  id: number
  title: string
  summary: string
  content: string
  coverImage?: string
  location: string
  startTime: string
  endTime: string
  registrationDeadline?: string
  maxParticipants?: number
  currentParticipants: number
  status: number
  isRegistrationOpen: number
  createdAt: string
  updatedAt: string
}

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: null as number | null
})

// 分页配置
const pagination = reactive({
  current: 1,
  size: 10
})

// 数据
const loading = ref(false)
const total = ref(0)
const activityList = ref<Activity[]>([])

// 获取活动列表
const fetchActivityList = async () => {
  try {
    loading.value = true
    const params: any = {
      page: pagination.current,
      pageSize: pagination.size
    }

    if (searchForm.status !== null) {
      params.status = searchForm.status.toString()
    }

    const result = await request.get('/activities/list', { params })
    activityList.value = result.data.list || []
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
  fetchActivityList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = null
  pagination.current = 1
  fetchActivityList()
}

// 新增
const handleCreate = () => {
  router.push('/content/activities/create')
}

// 编辑
const handleEdit = (row: Activity) => {
  router.push(`/content/activities/${row.id}/edit`)
}

// 删除
const handleDelete = (row: Activity) => {
  ElMessageBox.confirm(
    `确定要删除活动"${row.title}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await request.delete(`/activities/${row.id}`)
      ElMessage.success('删除成功')
      fetchActivityList()
    } catch (error: any) {
      console.error('删除失败:', error)
    }
  })
}

// 获取状态类型
const getStatusType = (status: number) => {
  switch (status) {
    case 0: return 'info'
    case 1: return 'success'
    case 2: return 'warning'
    default: return 'info'
  }
}

// 获取状态文本
const getStatusText = (status: number) => {
  switch (status) {
    case 0: return '未开始'
    case 1: return '进行中'
    case 2: return '已结束'
    default: return '未知'
  }
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  fetchActivityList()
}

// 当前页改变
const handleCurrentChange = (current: number) => {
  pagination.current = current
  fetchActivityList()
}

// 页面加载时获取数据
onMounted(() => {
  fetchActivityList()
})
</script>

<style lang="scss" scoped>
.activities-page {
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
