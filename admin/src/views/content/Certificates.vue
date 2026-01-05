<template>
  <div class="certificates-page">
    <div class="page-header">
      <div class="header-left">
        <h2>证件管理</h2>
        <p>管理系统中的证件信息</p>
      </div>
      <div class="header-right">
        <el-button @click="handleDownloadTemplate">
          <el-icon><Download /></el-icon>
          下载模板
        </el-button>
        <el-button type="success" @click="handleBatchImport">
          <el-icon><Upload /></el-icon>
          批量导入
        </el-button>
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增证件
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
        <el-form-item label="证件名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入证件名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="证件类型">
          <el-select
            v-model="searchForm.type"
            placeholder="请选择证件类型"
            clearable
            style="width: 200px"
          >
            <el-option label="全部" value="" />
            <el-option label="营业执照" value="营业执照" />
            <el-option label="组织机构代码证" value="组织机构代码证" />
            <el-option label="税务登记证" value="税务登记证" />
            <el-option label="社会保险登记证" value="社会保险登记证" />
            <el-option label="统计登记证" value="统计登记证" />
            <el-option label="开户许可证" value="开户许可证" />
            <el-option label="卫生许可证" value="卫生许可证" />
            <el-option label="安全生产许可证" value="安全生产许可证" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="证件编号">
          <el-input
            v-model="searchForm.number"
            placeholder="请输入证件编号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="持有人">
          <el-input
            v-model="searchForm.holder"
            placeholder="请输入持有人姓名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="证件状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择证件状态"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" value="" />
            <el-option label="有效" :value="1" />
            <el-option label="即将过期" :value="2" />
            <el-option label="已过期" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否公开">
          <el-select
            v-model="searchForm.isPublic"
            placeholder="请选择"
            clearable
            style="width: 120px"
          >
            <el-option label="全部" value="" />
            <el-option label="公开" :value="true" />
            <el-option label="不公开" :value="false" />
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
        v-loading="loading"
        :data="certificateList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="证件名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="type" label="证件类型" width="120" />
        <el-table-column prop="number" label="证件编号" width="150" show-overflow-tooltip />
        <el-table-column prop="holder" label="持有人" width="120" />
        <el-table-column prop="issuingAuthority" label="发证机关" width="150" show-overflow-tooltip />
        <el-table-column label="证件状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="是否公开" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isPublic ? 'success' : 'info'" size="small">
              {{ row.isPublic ? '公开' : '不公开' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="有效期" width="180">
          <template #default="{ row }">
            {{ formatDateRange(row.validFrom, row.validUntil) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
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

      <!-- 批量操作 -->
      <div class="batch-actions" v-if="selectedCertificates.length > 0">
        <span class="selected-count">已选择 {{ selectedCertificates.length }} 项</span>
        <el-button type="danger" size="small" @click="handleBatchDelete">
          批量删除
        </el-button>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current="pagination.current"
          v-model:page-size="pagination.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 批量导入对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="批量导入证件"
      width="600px"
      :before-close="handleCloseImport"
    >
      <div class="import-content">
        <el-alert
          title="导入说明"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 20px"
        >
          <template #default>
            <ul style="margin: 10px 0 0 20px; padding: 0; line-height: 1.8">
              <li>请先下载Excel模板，按照模板格式填写数据</li>
              <li>支持.xlsx和.xls格式的Excel文件</li>
              <li>必填字段：证件名称、证件类型、证件编号、持有人、发证机关、发证日期、有效期起始、有效期截止</li>
              <li>证件状态：有效/1、即将过期/2、已过期/0（默认为有效）</li>
              <li>是否公开：公开/1/true、不公开/0/false（默认为公开）</li>
            </ul>
          </template>
        </el-alert>

        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :on-change="handleFileChange"
          :file-list="fileList"
          accept=".xlsx,.xls"
          :limit="1"
          drag
        >
          <el-icon class="el-icon--upload" style="font-size: 67px; color: #c0c4cc; margin: 40px 0 16px; line-height: 50px;"><UploadFilled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              只能上传Excel文件，且不超过10MB
            </div>
          </template>
        </el-upload>

        <div v-if="importResult" class="import-result" style="margin-top: 20px">
          <el-alert
            :title="`导入完成：成功 ${importResult.successCount} 条，失败 ${importResult.failCount} 条`"
            :type="importResult.failCount > 0 ? 'warning' : 'success'"
            :closable="false"
            show-icon
          >
            <template #default>
              <div v-if="importResult.errorMessages && importResult.errorMessages.length > 0" style="margin-top: 10px">
                <p style="font-weight: bold; margin-bottom: 5px">错误信息：</p>
                <ul style="margin: 0; padding-left: 20px; max-height: 200px; overflow-y: auto">
                  <li v-for="(msg, index) in importResult.errorMessages" :key="index" style="margin-bottom: 5px">
                    {{ msg }}
                  </li>
                </ul>
              </div>
            </template>
          </el-alert>
        </div>
      </div>

      <template #footer>
        <el-button @click="handleCloseImport">取消</el-button>
        <el-button type="primary" @click="handleConfirmImport" :loading="importing">
          确认导入
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="证件详情"
      width="800px"
      :before-close="handleCloseDetail"
    >
      <div v-if="currentCertificate" class="certificate-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="证件名称" :span="2">
            {{ currentCertificate.name }}
          </el-descriptions-item>
          <el-descriptions-item label="证件类型">
            {{ currentCertificate.type }}
          </el-descriptions-item>
          <el-descriptions-item label="证件编号">
            {{ currentCertificate.number }}
          </el-descriptions-item>
          <el-descriptions-item label="持有人">
            {{ currentCertificate.holder }}
          </el-descriptions-item>
          <el-descriptions-item label="持有人联系方式">
            {{ currentCertificate.holderContact || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="发证机关" :span="2">
            {{ currentCertificate.issuingAuthority }}
          </el-descriptions-item>
          <el-descriptions-item label="发证日期">
            {{ formatDate(currentCertificate.issueDate) }}
          </el-descriptions-item>
          <el-descriptions-item label="证件状态">
            <el-tag :type="getStatusType(currentCertificate.status)" size="small">
              {{ getStatusText(currentCertificate.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="有效期起始">
            {{ formatDate(currentCertificate.validFrom) }}
          </el-descriptions-item>
          <el-descriptions-item label="有效期截止">
            {{ formatDate(currentCertificate.validUntil) }}
          </el-descriptions-item>
          <el-descriptions-item label="是否公开">
            <el-tag :type="currentCertificate.isPublic ? 'success' : 'info'" size="small">
              {{ currentCertificate.isPublic ? '公开' : '不公开' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="附件路径" :span="2">
            {{ currentCertificate.attachmentPath || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">
            {{ currentCertificate.description || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDateTime(currentCertificate.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">
            {{ formatDateTime(currentCertificate.updateTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Upload, Download, UploadFilled } from '@element-plus/icons-vue'
import type { UploadFile, UploadFiles } from 'element-plus'
import request from '@/api'

// 类型定义
interface Certificate {
  id: number
  name: string
  type: string
  number: string
  holder: string
  holderContact?: string
  issuingAuthority: string
  issueDate: string
  validFrom: string
  validUntil: string
  status: number
  isPublic: boolean
  attachmentPath?: string
  description?: string
  createTime: string
  updateTime: string
}

interface ImportResult {
  successCount: number
  failCount: number
  errorMessages?: string[]
}

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  name: '',
  type: '',
  number: '',
  holder: '',
  status: '',
  isPublic: ''
})

// 分页配置
const pagination = reactive({
  current: 1,
  size: 10
})

// 数据
const loading = ref(false)
const total = ref(0)
const certificateList = ref<Certificate[]>([])
const selectedCertificates = ref<Certificate[]>([])

// 详情对话框
const detailDialogVisible = ref(false)
const currentCertificate = ref<Certificate | null>(null)

// 批量导入对话框
const importDialogVisible = ref(false)
const uploadRef = ref()
const fileList = ref<UploadFiles>([])
const importing = ref(false)
const importResult = ref<ImportResult | null>(null)

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchCertificateList()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.type = ''
  searchForm.number = ''
  searchForm.holder = ''
  searchForm.status = ''
  searchForm.isPublic = ''
  pagination.current = 1
  fetchCertificateList()
}

// 获取证件列表
const fetchCertificateList = async () => {
  try {
    loading.value = true
    const params: any = {
      current: pagination.current,
      size: pagination.size
    }

    // 添加搜索参数
    if (searchForm.name) params.name = searchForm.name
    if (searchForm.type) params.type = searchForm.type
    if (searchForm.number) params.number = searchForm.number
    if (searchForm.holder) params.holder = searchForm.holder
    if (searchForm.status !== '') params.status = searchForm.status
    if (searchForm.isPublic !== '') params.isPublic = searchForm.isPublic

    const result = await request.get('/certificate/page', { params })
    total.value = result.data.total
    certificateList.value = result.data.records || []
  } catch (error: any) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}

// 新增
const handleCreate = () => {
  router.push('/content/certificates/create')
}

// 编辑
const handleEdit = (row: Certificate) => {
  router.push(`/content/certificates/${row.id}/edit`)
}

// 查看
const handleView = async (row: Certificate) => {
  try {
    const result = await request.get(`/certificate/${row.id}`)
    currentCertificate.value = result.data as Certificate
    detailDialogVisible.value = true
  } catch (error: any) {
    console.error('查询详情失败:', error)
  }
}

// 删除
const handleDelete = (row: Certificate) => {
  ElMessageBox.confirm(
    `确定要删除证件"${row.name}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await request.delete(`/certificate/${row.id}`)
      ElMessage.success('删除成功')
      fetchCertificateList()
    } catch (error: any) {
      console.error('删除失败:', error)
    }
  })
}

// 批量选择
const handleSelectionChange = (selection: Certificate[]) => {
  selectedCertificates.value = selection
}

// 批量删除
const handleBatchDelete = () => {
  const ids = selectedCertificates.value.map(item => item.id)
  ElMessageBox.confirm(
    `确定要删除选中的 ${ids.length} 个证件吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await request.delete('/certificate/batch', { data: ids })
      ElMessage.success('批量删除成功')
      selectedCertificates.value = []
      fetchCertificateList()
    } catch (error: any) {
      console.error('批量删除失败:', error)
    }
  })
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  fetchCertificateList()
}

// 当前页改变
const handleCurrentChange = (current: number) => {
  pagination.current = current
  fetchCertificateList()
}

// 关闭详情
const handleCloseDetail = () => {
  detailDialogVisible.value = false
  currentCertificate.value = null
}

// 格式化日期
const formatDate = (date: string | null | undefined) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

// 格式化时间
const formatDateTime = (date: string | null | undefined) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

// 格式化日期范围
const formatDateRange = (startDate: string | null | undefined, endDate: string | null | undefined) => {
  if (!startDate || !endDate) return ''
  return `${formatDate(startDate)} 至 ${formatDate(endDate)}`
}

// 获取状态类型
const getStatusType = (status: number) => {
  switch (status) {
    case 0: return 'danger'
    case 1: return 'success'
    case 2: return 'warning'
    default: return 'info'
  }
}

// 获取状态文本
const getStatusText = (status: number) => {
  switch (status) {
    case 0: return '已过期'
    case 1: return '有效'
    case 2: return '即将过期'
    default: return '未知'
  }
}

// 下载模板
const handleDownloadTemplate = () => {
  const link = document.createElement('a')
  link.href = '/api/certificate/template'
  link.download = 'certificate_template.xlsx'
  link.style.display = 'none'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  ElMessage.success('模板下载成功')
}

// 打开批量导入对话框
const handleBatchImport = () => {
  importDialogVisible.value = true
  fileList.value = []
  importResult.value = null
}

// 文件选择变化
const handleFileChange = (file: UploadFile) => {
  fileList.value = [file]
}

// 确认导入
const handleConfirmImport = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请先选择要导入的文件')
    return
  }

  const file = fileList.value[0].raw
  if (!file) {
    ElMessage.warning('文件无效')
    return
  }

  // 验证文件类型
  const fileName = file.name
  if (!fileName.endsWith('.xlsx') && !fileName.endsWith('.xls')) {
    ElMessage.error('只支持Excel文件（.xlsx或.xls格式）')
    return
  }

  // 验证文件大小（10MB）
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过10MB')
    return
  }

  try {
    importing.value = true
    importResult.value = null

    const formData = new FormData()
    formData.append('file', file)

    const result = await request.post('/certificate/batch-import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    if (result.code === 200 && result.data) {
      importResult.value = result.data
      ElMessage.success(`导入完成：成功 ${result.data.successCount} 条，失败 ${result.data.failCount} 条`)
      
      // 如果全部成功，关闭对话框并刷新列表
      if (result.data.failCount === 0) {
        setTimeout(() => {
          handleCloseImport()
          fetchCertificateList()
        }, 2000)
      } else {
        // 有失败的，刷新列表但不关闭对话框
        fetchCertificateList()
      }
    } else {
      ElMessage.error(result.message || '导入失败')
    }
  } catch (error: any) {
    console.error('导入失败:', error)
    ElMessage.error('导入失败: ' + (error?.message || '未知错误'))
  } finally {
    importing.value = false
  }
}

// 关闭导入对话框
const handleCloseImport = () => {
  importDialogVisible.value = false
  fileList.value = []
  importResult.value = null
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchCertificateList()
})
</script>

<style lang="scss" scoped>
.certificates-page {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
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

    .el-form {
      .el-form-item {
        margin-bottom: 0;
      }
    }
  }

  .table-card {
    .batch-actions {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 16px;
      padding-top: 16px;
      border-top: 1px solid #ebeef5;

      .selected-count {
        color: #409eff;
        font-size: 14px;
      }
    }

    .pagination {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }
  }

  .certificate-detail {
    .el-descriptions {
      :deep(.el-descriptions__label) {
        font-weight: 500;
      }
    }
  }
}
</style>