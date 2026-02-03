<template>
  <div class="certificate-edit">
    <div class="page-header">
      <div class="header-left">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/content/certificates' }">证件管理</el-breadcrumb-item>
          <el-breadcrumb-item>{{ isEdit ? '编辑证件' : '新增证件' }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ isEdit ? '编辑证件' : '新增证件' }}</h2>
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
            <el-form-item label="证件名称" prop="name">
              <el-input
                v-model="formData.name"
                placeholder="请输入证件名称"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="证件类型" prop="type">
              <el-select
                v-model="formData.type"
                placeholder="请选择证件类型"
                style="width: 100%"
              >
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
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="证件编号" prop="number">
              <el-input
                v-model="formData.number"
                placeholder="请输入证件编号"
                maxlength="50"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="持有人" prop="holder">
              <el-input
                v-model="formData.holder"
                placeholder="请输入持有人姓名"
                maxlength="50"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select
                v-model="formData.gender"
                placeholder="请选择性别"
                style="width: 100%"
                clearable
              >
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="身份证号">
              <el-input
                v-model="formData.idCard"
                placeholder="请输入身份证号"
                maxlength="18"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位名称">
              <el-input
                v-model="formData.position"
                placeholder="请输入岗位名称"
                maxlength="100"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="技能等级">
              <el-input
                v-model="formData.skillLevel"
                placeholder="请输入技能等级"
                maxlength="50"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="持有人联系方式">
              <el-input
                v-model="formData.holderContact"
                placeholder="请输入持有人联系方式"
                maxlength="100"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="发证机关" prop="issuingAuthority">
              <el-input
                v-model="formData.issuingAuthority"
                placeholder="请输入发证机关"
                maxlength="100"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="发证日期" prop="issueDate">
              <el-date-picker
                v-model="formData.issueDate"
                type="date"
                placeholder="请选择发证日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="有效期起始" prop="validFrom">
              <el-date-picker
                v-model="formData.validFrom"
                type="date"
                placeholder="请选择有效期起始日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="有效期截止" prop="validUntil">
              <el-date-picker
                v-model="formData.validUntil"
                type="date"
                placeholder="请选择有效期截止日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="证件状态" prop="status">
              <el-select
                v-model="formData.status"
                placeholder="请选择证件状态"
                style="width: 100%"
              >
                <el-option label="有效" :value="1" />
                <el-option label="即将过期" :value="2" />
                <el-option label="已过期" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否公开" prop="isPublic">
              <el-radio-group v-model="formData.isPublic">
                <el-radio :label="true">公开</el-radio>
                <el-radio :label="false">不公开</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="一寸照">
          <el-upload
            ref="photoUploadRef"
            :http-request="handlePhotoUpload"
            :on-remove="handlePhotoRemove"
            :file-list="photoFileList"
            :limit="1"
            :before-upload="beforePhotoUpload"
            list-type="picture-card"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                支持JPG、PNG格式，建议尺寸25mm×35mm（一寸照），文件大小不超过2MB
              </div>
            </template>
          </el-upload>
          <div v-if="formData.photoPath" class="photo-preview" style="margin-top: 10px">
            <el-image
              :src="formData.photoPath"
              style="width: 100px; height: 140px; border: 1px solid #ddd; border-radius: 4px;"
              fit="cover"
              preview
            />
          </div>
        </el-form-item>

        <el-form-item label="附件">
          <el-upload
            ref="uploadRef"
            :http-request="handleUpload"
            :on-remove="handleRemove"
            :file-list="fileList"
            :limit="1"
            :before-upload="beforeUpload"
          >
            <el-button type="primary">
              <el-icon><Upload /></el-icon>
              选择文件
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持PDF、DOC、DOCX、XLS、XLSX、PPT、PPTX、JPG、PNG等格式，文件大小不超过10MB
              </div>
            </template>
          </el-upload>
          <div v-if="formData.attachmentPath" class="file-preview" style="margin-top: 10px">
            <el-link :href="formData.attachmentPath" target="_blank" type="primary">
              <el-icon><Document /></el-icon>
              查看附件
            </el-link>
          </div>
        </el-form-item>

        <el-form-item label="描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入证件描述"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, UploadFile, UploadFiles } from 'element-plus'
import { Upload, Document, Plus } from '@element-plus/icons-vue'
import request from '@/api'

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()

// 是否为编辑模式
const isEdit = computed(() => route.name === 'CertificateEdit')
const certificateId = computed(() => route.params.id as string)

// 保存状态
const saving = ref(false)

// 文件上传相关
const uploadRef = ref()
const fileList = ref<UploadFiles>([])
const uploading = ref(false)

// 一寸照上传相关
const photoUploadRef = ref()
const photoFileList = ref<UploadFiles>([])
const uploadingPhoto = ref(false)

// 表单数据
const formData = reactive({
  name: '',
  type: '',
  number: '',
  holder: '',
  gender: '',
  idCard: '',
  position: '',
  skillLevel: '',
  holderContact: '',
  issuingAuthority: '',
  issueDate: '',
  validFrom: '',
  validUntil: '',
  status: 1,
  isPublic: true,
  photoPath: '',
  attachmentPath: '',
  description: ''
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入证件名称', trigger: 'blur' },
    { min: 2, max: 100, message: '证件名称长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择证件类型', trigger: 'change' }
  ],
  number: [
    { required: true, message: '请输入证件编号', trigger: 'blur' },
    { min: 2, max: 50, message: '证件编号长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  holder: [
    { required: true, message: '请输入持有人姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '持有人姓名长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  issuingAuthority: [
    { required: true, message: '请输入发证机关', trigger: 'blur' },
    { min: 2, max: 100, message: '发证机关长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  issueDate: [
    { required: true, message: '请选择发证日期', trigger: 'change' }
  ],
  validFrom: [
    { required: true, message: '请选择有效期起始日期', trigger: 'change' }
  ],
  validUntil: [
    { required: true, message: '请选择有效期截止日期', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择证件状态', trigger: 'change' }
  ],
  isPublic: [
    { required: true, message: '请选择是否公开', trigger: 'change' }
  ]
}

// 一寸照上传前验证
const beforePhotoUpload = (file: File) => {
  const isValidType = [
    'image/jpeg',
    'image/jpg',
    'image/png'
  ].includes(file.type) || 
  file.name.endsWith('.jpg') ||
  file.name.endsWith('.jpeg') ||
  file.name.endsWith('.png')

  if (!isValidType) {
    ElMessage.error('一寸照只支持JPG、PNG格式')
    return false
  }

  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('一寸照大小不能超过2MB')
    return false
  }

  return true
}

// 自定义一寸照上传
const handlePhotoUpload = async (options: any) => {
  const { file } = options
  const uploadFormData = new FormData()
  uploadFormData.append('file', file)

  try {
    uploadingPhoto.value = true
    const response = await request.post('/file/upload', uploadFormData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (response && response.code === 200 && response.data) {
      formData.photoPath = response.data.url
      ElMessage.success('一寸照上传成功')
      // 更新文件列表显示
      photoFileList.value = [{
        name: file.name,
        url: response.data.url,
        status: 'success'
      } as UploadFile]
    } else {
      ElMessage.error(response?.message || '一寸照上传失败')
    }
  } catch (error: any) {
    console.error('一寸照上传失败:', error)
    ElMessage.error('一寸照上传失败，请稍后重试')
  } finally {
    uploadingPhoto.value = false
  }
}

// 移除一寸照
const handlePhotoRemove = () => {
  formData.photoPath = ''
  photoFileList.value = []
}

// 文件上传前验证
const beforeUpload = (file: File) => {
  const isValidType = [
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'application/vnd.ms-excel',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'application/vnd.ms-powerpoint',
    'application/vnd.openxmlformats-officedocument.presentationml.presentation',
    'image/jpeg',
    'image/png',
    'image/gif'
  ].includes(file.type) || 
  file.name.endsWith('.pdf') ||
  file.name.endsWith('.doc') ||
  file.name.endsWith('.docx') ||
  file.name.endsWith('.xls') ||
  file.name.endsWith('.xlsx') ||
  file.name.endsWith('.ppt') ||
  file.name.endsWith('.pptx') ||
  file.name.endsWith('.jpg') ||
  file.name.endsWith('.jpeg') ||
  file.name.endsWith('.png') ||
  file.name.endsWith('.gif')

  if (!isValidType) {
    ElMessage.error('不支持的文件格式，请上传PDF、DOC、DOCX、XLS、XLSX、PPT、PPTX、JPG、PNG等格式')
    return false
  }

  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过10MB')
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
      formData.attachmentPath = response.data.url
      ElMessage.success('文件上传成功')
      // 更新文件列表显示
      fileList.value = [{
        name: file.name,
        url: response.data.url,
        status: 'success'
      } as UploadFile]
    } else {
      ElMessage.error(response?.message || '文件上传失败')
    }
  } catch (error: any) {
    console.error('文件上传失败:', error)
    ElMessage.error('文件上传失败，请稍后重试')
  } finally {
    uploading.value = false
  }
}

// 移除文件
const handleRemove = () => {
  formData.attachmentPath = ''
  fileList.value = []
}

// 获取证件详情
const fetchCertificateDetail = async () => {
  if (!isEdit.value || !certificateId.value) return

  try {
    const result = await request.get(`/certificate/${certificateId.value}`)
    const certificate = result.data
    Object.assign(formData, {
      gender: certificate.gender || '',
      idCard: certificate.idCard || '',
      position: certificate.position || '',
      skillLevel: certificate.skillLevel || '',
      name: certificate.name || '',
      type: certificate.type || '',
      number: certificate.number || '',
      holder: certificate.holder || '',
      holderContact: certificate.holderContact || '',
      issuingAuthority: certificate.issuingAuthority || '',
      issueDate: certificate.issueDate || '',
      validFrom: certificate.validFrom || '',
      validUntil: certificate.validUntil || '',
      status: certificate.status ?? 1,
      isPublic: certificate.isPublic ?? true,
      photoPath: certificate.photoPath || '',
      attachmentPath: certificate.attachmentPath || '',
      description: certificate.description || ''
    })
    
    // 如果有一寸照路径，设置文件列表用于显示
    if (certificate.photoPath) {
      photoFileList.value = [{
        name: certificate.photoPath.split('/').pop() || '一寸照',
        url: certificate.photoPath
      } as UploadFile]
    }
    
    // 如果有附件路径，设置文件列表用于显示
    if (certificate.attachmentPath) {
      fileList.value = [{
        name: certificate.attachmentPath.split('/').pop() || '附件',
        url: certificate.attachmentPath
      } as UploadFile]
    }
  } catch (error: any) {
    console.error('获取证件详情失败:', error)
  }
}

// 保存
const handleSave = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    // 验证日期逻辑
    if (formData.validFrom && formData.validUntil) {
      if (new Date(formData.validFrom) > new Date(formData.validUntil)) {
        ElMessage.error('有效期起始日期不能大于截止日期')
        return
      }
    }

    saving.value = true

    if (isEdit.value) {
      await request.put(`/certificate/${certificateId.value}`, formData)
    } else {
      await request.post('/certificate', formData)
    }
    
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    router.push('/content/certificates')
  } catch (error: any) {
    console.error('保存失败:', error)
    const errorMessage = error?.response?.data?.message || error?.message || '保存失败，请稍后重试'
    ElMessage.error(errorMessage)
  } finally {
    saving.value = false
  }
}

// 取消
const handleCancel = () => {
  router.push('/content/certificates')
}

// 页面加载时获取数据
onMounted(() => {
  if (isEdit.value) {
    fetchCertificateDetail()
  }
})
</script>

<style lang="scss" scoped>
.certificate-edit {
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