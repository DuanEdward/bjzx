<template>
  <div class="certificate-query-page">
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">证书查询</h1>
        <p class="page-subtitle">请输入相关信息查询证书</p>
      </div>

      <!-- 查询表单区域 -->
      <div class="query-section">
        <el-card class="query-card" shadow="hover">
          <el-form
            :model="queryForm"
            :rules="queryRules"
            ref="queryFormRef"
            label-width="100px"
            class="query-form"
          >
            <el-form-item label="姓名" prop="name">
              <el-input
                v-model="queryForm.name"
                placeholder="请输入姓名"
                clearable
                size="large"
                :prefix-icon="User"
                style="max-width: 400px"
              />
            </el-form-item>

            <el-form-item label="身份证号" prop="idCard">
              <el-input
                v-model="queryForm.idCard"
                placeholder="请输入18位身份证号"
                clearable
                size="large"
                :prefix-icon="CreditCard"
                maxlength="18"
                show-word-limit
                style="max-width: 400px"
                @input="handleIdCardInput"
              />
            </el-form-item>

            <el-form-item label="证书编号" prop="certificateNumber">
              <el-input
                v-model="queryForm.certificateNumber"
                placeholder="请输入证书编号"
                clearable
                size="large"
                :prefix-icon="Document"
                style="max-width: 400px"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                @click="handleQuery"
                style="min-width: 120px"
              >
                <el-icon v-if="!loading"><Search /></el-icon>
                {{ loading ? '查询中...' : '查询' }}
              </el-button>
              <el-button
                size="large"
                @click="handleReset"
                style="min-width: 120px; margin-left: 20px"
              >
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>

      <!-- 查询结果区域 -->
      <div class="result-section" v-if="hasSearched">
        <el-card class="result-card" shadow="hover">
          <template #header>
            <div class="result-header">
              <span class="result-title">查询结果</span>
              <span class="result-count" v-if="!loading">
                共找到 {{ total }} 条记录
              </span>
            </div>
          </template>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="3" animated />
          </div>

          <!-- 空状态 -->
          <el-empty
            v-else-if="certificateList.length === 0"
            description="未找到相关证书信息，请检查输入信息是否正确"
            :image-size="120"
          />

          <!-- 证书列表 -->
          <div v-else class="certificate-list">
            <div
              v-for="certificate in certificateList"
              :key="certificate.id"
              class="certificate-item"
            >
              <div class="certificate-display">
                <div class="certificate-bg">
                  <img src="/pic/bgCert.png" alt="证书背景" class="certificate-bg-image" />
                  <div class="certificate-content">
                    <!-- 证书编号 - [25%-45%, 17%-20%] -->
                    <div class="cert-field field-number">{{ certificate.number }}</div>
                    
                    <!-- 持有人姓名 - [15%-35%, 25%-28%] -->
                    <div class="cert-field field-holder">{{ certificate.holder }}</div>
                    
                    <!-- 性别 - [15%-35%, 33%-36%] -->
                    <div class="cert-field field-gender">{{ certificate.gender || '' }}</div>
                    
                    <!-- 身份证号 - [15%-35%, 41%-44%] -->
                    <div class="cert-field field-id-card">{{ certificate.idCard || '' }}</div>
                    
                    <!-- 岗位名称 - [15%-35%, 49%-52%] -->
                    <div class="cert-field field-position">{{ certificate.position || '' }}</div>
                    
                    <!-- 技能等级 - [15%-35%, 57%-60%] -->
                    <div class="cert-field field-skill-level">{{ certificate.skillLevel || '' }}</div>
                    
                    <!-- 发证日期 - [15%-35%, 65%-68%] -->
                    <div class="cert-field field-issue-date">{{ formatDate(certificate.issueDate) }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 提示信息 -->
      <div class="tips-section" v-if="!hasSearched">
        <el-alert
          title="查询提示"
          type="info"
          :closable="false"
          show-icon
        >
          <template #default>
            <ul class="tips-list">
              <li>请输入姓名、身份证号或证书编号进行查询</li>
              <li>至少输入一项查询条件</li>
              <li>身份证号请输入18位有效身份证号码</li>
              <li>证书编号请准确输入，区分大小写</li>
            </ul>
          </template>
        </el-alert>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElForm } from 'element-plus'
import { User, CreditCard, Document, Search, Refresh } from '@element-plus/icons-vue'
import request from '@/api/index'

// 表单引用
const queryFormRef = ref<InstanceType<typeof ElForm>>()

// 查询表单
const queryForm = reactive({
  name: '',
  idCard: '',
  certificateNumber: ''
})

// 表单验证规则
const validateIdCard = (_rule: any, value: string, callback: Function) => {
  if (value && value.length !== 18) {
    callback(new Error('身份证号必须为18位'))
  } else if (value && !/^\d{17}[\dXx]$/.test(value)) {
    callback(new Error('身份证号格式不正确'))
  } else {
    callback()
  }
}

const validateAtLeastOne = (_rule: any, _value: string, callback: Function) => {
  if (!queryForm.name && !queryForm.idCard && !queryForm.certificateNumber) {
    callback(new Error('至少输入一项查询条件'))
  } else {
    callback()
  }
}

const queryRules = {
  name: [
    { validator: validateAtLeastOne, trigger: 'blur' }
  ],
  idCard: [
    { validator: validateIdCard, trigger: 'blur' }
  ],
  certificateNumber: [
    { validator: validateAtLeastOne, trigger: 'blur' }
  ]
}

// 数据状态
const loading = ref(false)
const hasSearched = ref(false)
const total = ref(0)
const certificateList = ref<any[]>([])

// 处理身份证号输入（自动转换为大写）
const handleIdCardInput = (value: string) => {
  queryForm.idCard = value.toUpperCase()
}

// 查询证书
const handleQuery = async () => {
  if (!queryFormRef.value) return

  try {
    await queryFormRef.value.validate()
    
    loading.value = true
    hasSearched.value = true

    const params: any = {}
    if (queryForm.name) {
      params.holder = queryForm.name
    }
    if (queryForm.idCard) {
      // 如果后端支持身份证号查询，使用idCard参数
      // 如果不支持，可以通过holderContact字段查询
      params.idCard = queryForm.idCard
      // 如果后端不支持idCard参数，可以尝试通过holderContact查询
      // params.holderContact = queryForm.idCard
    }
    if (queryForm.certificateNumber) {
      params.number = queryForm.certificateNumber
    }

    const response = await request.get<any[]>('/certificate/public', { params })
    
    if (response.code === 200) {
      certificateList.value = response.data || []
      total.value = certificateList.value.length
      
      if (certificateList.value.length === 0) {
        ElMessage.warning('未找到相关证书信息')
      } else {
        ElMessage.success(`查询成功，找到 ${total.value} 条记录`)
      }
    } else {
      ElMessage.error(response.message || '查询失败')
      certificateList.value = []
      total.value = 0
    }
  } catch (error: any) {
    if (error.message && error.message.includes('至少输入')) {
      ElMessage.warning('请至少输入一项查询条件')
    } else if (error.message && error.message.includes('身份证号')) {
      ElMessage.warning(error.message)
    } else {
      console.error('查询失败:', error)
      ElMessage.error('查询失败，请稍后重试')
    }
    certificateList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 重置表单
const handleReset = () => {
  queryFormRef.value?.resetFields()
  queryForm.name = ''
  queryForm.idCard = ''
  queryForm.certificateNumber = ''
  certificateList.value = []
  total.value = 0
  hasSearched.value = false
}

// 格式化日期
const formatDate = (date: string) => {
  if (!date) return '暂无'
  try {
    const d = new Date(date)
    return d.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    })
  } catch {
    return date
  }
}

// 获取状态类型
const getStatusType = (status: number): 'success' | 'warning' | 'danger' => {
  switch (status) {
    case 0: return 'danger'
    case 1: return 'success'
    case 2: return 'warning'
    default: return 'success'
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
</script>

<style lang="scss" scoped>
.certificate-query-page {
  padding: 40px 0;
  min-height: calc(100vh - 160px);
  background: linear-gradient(to bottom, #f5f7fa 0%, #ffffff 100%);

  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .page-header {
    text-align: center;
    margin-bottom: 40px;

    .page-title {
      font-size: 36px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 10px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .page-subtitle {
      font-size: 16px;
      color: var(--text-regular);
    }
  }

  .query-section {
    margin-bottom: 30px;

    .query-card {
      background: #fff;
      border-radius: 12px;

      .query-form {
        padding: 20px 0;

        :deep(.el-form-item) {
          margin-bottom: 30px;
        }

        :deep(.el-form-item__label) {
          font-weight: 500;
          font-size: 16px;
          color: var(--text-primary);
        }
      }
    }
  }

  .result-section {
    margin-bottom: 30px;

    .result-card {
      background: #fff;
      border-radius: 12px;

      .result-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .result-title {
          font-size: 20px;
          font-weight: 600;
          color: var(--text-primary);
        }

        .result-count {
          font-size: 14px;
          color: var(--text-secondary);
        }
      }

      .loading-container {
        padding: 40px 0;
      }

      .certificate-list {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 40px;

        .certificate-item {
          width: 100%;
          max-width: 900px;
          display: flex;
          justify-content: center;

          .certificate-display {
            width: 100%;
            position: relative;

            .certificate-bg {
              position: relative;
              width: 100%;
              padding-bottom: 135%; // 图片尺寸 1220×1646，宽高比 1646/1220 ≈ 1.35
              overflow: hidden;
              border-radius: 8px;
              box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);

              .certificate-bg-image {
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                object-fit: fill; // 改为 fill 让图片完全填充容器
                background: #fff;
              }

              .certificate-content {
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                display: flex;
                flex-direction: column;

                .cert-field {
                  position: absolute;
                  font-size: 24px;
                  color: #333;
                  font-weight: 500;
                  white-space: nowrap;
                  line-height: 2.4; // 行间距加大
                  
                  // 字段位置根据背景图片 bgCert.png 的像素坐标转换
                  // 图片尺寸：1220×1646 像素
                  // 坐标转换：x/1220*100% = left, y/1646*100% = top, width/1220*100% = width
                  // 每行之间top相隔5.5%
                  
                  &.field-number {
                    // 证书编号 (485, 205) 宽度575
                    top: 14.5%; // 起始位置
                    left: 39.8%; // 485/1220
                    width: 47.1%; // 575/1220
                    font-size: 24px;
                    color: #333;
                    text-align: left;
                  }

                  &.field-holder {
                    // 姓名 (385, 275) 宽度640
                    top: 20.0%; // 证书编号 + 5.5%
                    left: 31.6%; // 385/1220
                    width: 52.5%; // 640/1220
                    font-size: 24px;
                    color: #333;
                    text-align: left;
                  }

                  &.field-gender {
                    // 性别 (385, 345) 宽度640
                    top: 25.5%; // 姓名 + 5.5%
                    left: 31.6%; // 385/1220
                    width: 52.5%; // 640/1220
                    font-size: 24px;
                    color: #333;
                    text-align: left;
                  }

                  &.field-id-card {
                    // 身份证号 (385, 415) 宽度640
                    top: 31.0%; // 性别 + 5.5%
                    left: 31.6%; // 385/1220
                    width: 52.5%; // 640/1220
                    font-size: 24px;
                    color: #333;
                    text-align: left;
                  }

                  &.field-position {
                    // 岗位名称 (385, 485) 宽度640
                    top: 36.5%; // 身份证号 + 5.5%
                    left: 31.6%; // 385/1220
                    width: 52.5%; // 640/1220
                    font-size: 24px;
                    color: #333;
                    text-align: left;
                  }

                  &.field-skill-level {
                    // 技能等级 (385, 555) 宽度640
                    top: 42.0%; // 岗位名称 + 5.5%
                    left: 31.6%; // 385/1220
                    width: 52.5%; // 640/1220
                    font-size: 24px;
                    color: #333;
                    text-align: left;
                  }

                  &.field-issue-date {
                    // 发证日期 (385, 625) 宽度640
                    top: 47.5%; // 技能等级 + 5.5%
                    left: 31.6%; // 385/1220
                    width: 52.5%; // 640/1220
                    font-size: 24px;
                    color: #333;
                    text-align: left;
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  .tips-section {
    .tips-list {
      margin: 10px 0 0 20px;
      padding: 0;
      color: var(--text-regular);
      line-height: 2;

      li {
        margin-bottom: 5px;
      }
    }
  }
}

@media (max-width: 768px) {
  .certificate-query-page {
    padding: 20px 0;

    .page-header {
      .page-title {
        font-size: 28px;
      }
    }

    .query-section {
      .query-card {
        .query-form {
          :deep(.el-input) {
            max-width: 100% !important;
          }
        }
      }
    }

    .result-section {
      .result-card {
        .result-header {
          flex-direction: column;
          align-items: flex-start;
          gap: 10px;
        }

        .certificate-list {
          .certificate-item {
            .certificate-header {
              flex-direction: column;
              align-items: flex-start;
              gap: 15px;
            }

            .certificate-details {
              :deep(.el-descriptions) {
                .el-descriptions__table {
                  .el-descriptions__cell {
                    display: block;
                    width: 100% !important;
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
</style>
