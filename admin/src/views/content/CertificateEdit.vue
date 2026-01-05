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
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="持有人联系方式">
              <el-input
                v-model="formData.holderContact"
                placeholder="请输入持有人联系方式"
                maxlength="100"
              />
            </el-form-item>
          </el-col>
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

        <el-form-item label="附件路径">
          <el-input
            v-model="formData.attachmentPath"
            placeholder="请输入附件路径"
            maxlength="255"
          />
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
import type { FormInstance } from 'element-plus'
import request from '@/api'

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()

// 是否为编辑模式
const isEdit = computed(() => route.name === 'CertificateEdit')
const certificateId = computed(() => route.params.id as string)

// 保存状态
const saving = ref(false)

// 表单数据
const formData = reactive({
  name: '',
  type: '',
  number: '',
  holder: '',
  holderContact: '',
  issuingAuthority: '',
  issueDate: '',
  validFrom: '',
  validUntil: '',
  status: 1,
  isPublic: true,
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

// 获取证件详情
const fetchCertificateDetail = async () => {
  if (!isEdit.value || !certificateId.value) return

  try {
    const result = await request.get(`/certificate/${certificateId.value}`)
    const certificate = result.data
    Object.assign(formData, {
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
      attachmentPath: certificate.attachmentPath || '',
      description: certificate.description || ''
    })
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