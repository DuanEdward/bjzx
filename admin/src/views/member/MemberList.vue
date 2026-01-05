<template>
  <div class="member-list-page">
    <div class="page-header">
      <div class="header-left">
        <h2>会员列表</h2>
        <p>管理系统中的会员信息</p>
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
        <el-form-item label="等级">
          <el-select
            v-model="searchForm.level"
            placeholder="请选择等级"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" :value="null" />
            <el-option label="普通会员" :value="1" />
            <el-option label="高级会员" :value="2" />
            <el-option label="理事" :value="3" />
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
        :data="memberList"
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
        <el-table-column prop="joinedAt" label="入会时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">
              查看
            </el-button>
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

    <!-- 查看/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑会员' : '查看会员'"
      width="700px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        :disabled="!isEdit"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="formData.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="formData.gender" style="width: 100%">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="出生年月">
              <el-date-picker
                v-model="formData.birthDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职称" prop="title">
              <el-input v-model="formData.title" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="工作单位" prop="company">
          <el-input v-model="formData.company" />
        </el-form-item>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="formData.phone" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formData.email" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="研究方向">
          <el-input v-model="formData.research" type="textarea" :rows="2" />
        </el-form-item>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="会员等级" prop="level">
              <el-select v-model="formData.level" style="width: 100%">
                <el-option label="普通会员" :value="1" />
                <el-option label="高级会员" :value="2" />
                <el-option label="理事" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="formData.status" style="width: 100%">
                <el-option label="待审核" :value="0" />
                <el-option label="已通过" :value="1" />
                <el-option label="已拒绝" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="个人简介">
          <el-input v-model="formData.introduction" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="isEdit" type="primary" @click="handleSave" :loading="saving">
          保存
        </el-button>
        <el-button v-else type="primary" @click="isEdit = true">
          编辑
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
  avatar?: string
  research?: string
  level: number
  introduction?: string
  status: number
  reviewedBy?: number
  reviewedAt?: string
  reviewRemark?: string
  joinedAt?: string
  createdAt: string
  updatedAt: string
}

// 数据
const loading = ref(false)
const total = ref(0)
const memberList = ref<Member[]>([])
const dialogVisible = ref(false)
const saving = ref(false)
const isEdit = ref(false)
const currentId = ref<number | null>(null)

const formRef = ref<FormInstance>()

// 搜索表单
const searchForm = reactive({
  keyword: '',
  level: null as number | null,
  status: null as number | null
})

// 分页配置
const pagination = reactive({
  current: 1,
  size: 10
})

// 表单数据
const formData = reactive({
  name: '',
  gender: '',
  birthDate: '',
  title: '',
  company: '',
  email: '',
  phone: '',
  research: '',
  level: 1,
  introduction: '',
  status: 0
})

// 表单验证规则
const formRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  level: [{ required: true, message: '请选择会员等级', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 获取会员列表
const fetchMemberList = async () => {
  try {
    loading.value = true
    const params: any = {
      page: pagination.current,
      pageSize: pagination.size
    }

    if (searchForm.level !== null) {
      params.level = searchForm.level
    }
    if (searchForm.status !== null) {
      params.status = searchForm.status
    }

    const result = await request.get('/member/list', { params })
    memberList.value = result.data.list || []
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
  fetchMemberList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.level = null
  searchForm.status = null
  pagination.current = 1
  fetchMemberList()
}

// 查看
const handleView = (row: Member) => {
  isEdit.value = false
  currentId.value = row.id
  Object.assign(formData, {
    name: row.name,
    gender: row.gender || '',
    birthDate: row.birthDate || '',
    title: row.title || '',
    company: row.company || '',
    email: row.email || '',
    phone: row.phone || '',
    research: row.research || '',
    level: row.level || 1,
    introduction: row.introduction || '',
    status: row.status ?? 0
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: Member) => {
  isEdit.value = true
  handleView(row)
}

// 保存
const handleSave = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    saving.value = true

    await request.put(`/member/${currentId.value}`, formData)
    
    ElMessage.success('更新成功')
    dialogVisible.value = false
    fetchMemberList()
  } catch (error: any) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// 删除
const handleDelete = (row: Member) => {
  ElMessageBox.confirm(
    `确定要删除会员"${row.name}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await request.delete(`/member/${row.id}`)
      ElMessage.success('删除成功')
      fetchMemberList()
    } catch (error: any) {
      console.error('删除失败:', error)
    }
  })
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
  fetchMemberList()
}

// 当前页改变
const handleCurrentChange = (current: number) => {
  pagination.current = current
  fetchMemberList()
}

// 页面加载时获取数据
onMounted(() => {
  fetchMemberList()
})
</script>

<style lang="scss" scoped>
.member-list-page {
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
