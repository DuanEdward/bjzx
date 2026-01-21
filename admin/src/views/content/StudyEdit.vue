<template>
  <div class="study-edit">
    <div class="page-header">
      <div class="header-left">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/content/study' }">学习考试管理</el-breadcrumb-item>
          <el-breadcrumb-item>{{ isEdit ? '编辑学习考试' : '新增学习考试' }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ isEdit ? '编辑学习考试' : '新增学习考试' }}</h2>
      </div>
      <div class="header-right">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">
          保存
        </el-button>
      </div>
    </div>

    <el-card class="form-card" shadow="never">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        size="default"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="标题" prop="title">
              <el-input
                v-model="formData.title"
                placeholder="请输入标题"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作者" prop="author">
              <el-input
                v-model="formData.author"
                placeholder="请输入作者"
                maxlength="50"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="封面图片">
              <el-upload
                ref="uploadRef"
                :http-request="handleUpload"
                :on-remove="handleRemove"
                :file-list="fileList"
                :limit="1"
                :before-upload="beforeUpload"
                list-type="picture-card"
              >
                <el-icon><Plus /></el-icon>
                <template #tip>
                  <div class="el-upload__tip">
                    支持JPG、PNG、GIF格式，文件大小不超过10MB
                  </div>
                </template>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="摘要" prop="summary">
          <el-input
            v-model="formData.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入摘要"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="15"
            placeholder="请输入内容"
          />
        </el-form-item>

        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="formData.status">
                <el-radio :label="0">草稿</el-radio>
                <el-radio :label="1">已发布</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="是否置顶">
              <el-switch
                v-model="formData.isTop"
                :active-value="1"
                :inactive-value="0"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="formData.status === 1">
            <el-form-item label="发布时间">
              <el-date-picker
                v-model="formData.publishedAt"
                type="datetime"
                placeholder="选择发布时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, UploadFile, UploadFiles } from 'element-plus'
import request from '@/api'

const router = useRouter()
const route = useRoute()

const formRef = ref<FormInstance>()
const saving = ref(false)
const uploadRef = ref()
const fileList = ref<UploadFiles>([])
const uploading = ref(false)

// 判断是编辑还是新增
const isEdit = computed(() => {
  return !!route.params.id && route.params.id !== 'create'
})

const studyId = computed(() => {
  return isEdit.value ? Number(route.params.id) : null
})

// 表单数据
const formData = reactive({
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  author: '',
  categoryId: 5, // 学习考试分类ID，需要根据实际情况调整
  status: 0,
  isTop: 0,
  publishedAt: ''
})

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 200, message: '标题长度在 2 到 200 个字符', trigger: 'blur' }
  ],
  summary: [
    { required: true, message: '请输入摘要', trigger: 'blur' },
    { max: 500, message: '摘要长度不能超过 500 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' }
  ],
  author: [
    { required: true, message: '请输入作者', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 文件上传前验证
const beforeUpload = (file: File) => {
  const isValidType = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type) ||
    file.name.endsWith('.jpg') ||
    file.name.endsWith('.jpeg') ||
    file.name.endsWith('.png') ||
    file.name.endsWith('.gif')

  if (!isValidType) {
    ElMessage.error('只支持JPG、PNG、GIF格式的图片')
    return false
  }

  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB')
    return false
  }

  return true
}

// 自定义文件上传
const handleUpload = async (options: any) => {
  const { file } = options
  const uploadFormData = new FormData()
  uploadFormData.append('file', file)

  try {
    uploading.value = true
    const response = await request.post('/file/upload', uploadFormData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (response && response.code === 200 && response.data) {
      formData.coverImage = response.data.url
      ElMessage.success('图片上传成功')
      // 更新文件列表显示
      fileList.value = [{
        name: file.name,
        url: response.data.url,
        status: 'success'
      } as UploadFile]
    } else {
      ElMessage.error(response?.message || '图片上传失败')
    }
  } catch (error: any) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败，请稍后重试')
  } finally {
    uploading.value = false
  }
}

// 移除文件
const handleRemove = () => {
  formData.coverImage = ''
  fileList.value = []
}

// 获取学习考试详情
const fetchStudyDetail = async () => {
  if (!isEdit.value || !studyId.value) return

  try {
    const result = await request.get(`/news/${studyId.value}`)
    const study = result.data
    Object.assign(formData, {
      title: study.title || '',
      summary: study.summary || '',
      content: study.content || '',
      coverImage: study.coverImage || '',
      author: study.author || '',
      categoryId: study.categoryId || 5,
      status: study.status ?? 0,
      isTop: study.isTop ?? 0,
      publishedAt: study.publishedAt || ''
    })
    
    // 如果有封面图片，设置文件列表用于显示
    if (study.coverImage) {
      fileList.value = [{
        name: study.coverImage.split('/').pop() || '封面图片',
        url: study.coverImage
      } as UploadFile]
    } else {
      fileList.value = []
    }
  } catch (error: any) {
    console.error('获取学习考试详情失败:', error)
    ElMessage.error('获取学习考试详情失败')
  }
}

// 保存
const handleSave = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    saving.value = true

    // 如果状态为已发布且未设置发布时间，则设置为当前时间
    if (formData.status === 1 && !formData.publishedAt) {
      const now = new Date()
      formData.publishedAt = now.toISOString().slice(0, 19).replace('T', ' ')
    }

    if (isEdit.value) {
      await request.put(`/news/${studyId.value}`, formData)
    } else {
      await request.post('/news', formData)
    }
    
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    router.push('/content/study')
  } catch (error: any) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

// 取消
const handleCancel = () => {
  router.push('/content/study')
}

// 页面加载时获取数据
onMounted(() => {
  if (isEdit.value) {
    fetchStudyDetail()
  }
})
</script>

<style lang="scss" scoped>
.study-edit {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20px;

    .header-left {
      .el-breadcrumb {
        margin-bottom: 8px;
      }

      h2 {
        margin: 0;
        font-size: 24px;
        font-weight: 500;
        color: #303133;
      }
    }
  }

  .form-card {
    max-width: 1200px;
  }
}
</style>

