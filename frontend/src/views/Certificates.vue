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
              <div class="certificate-info">
                <div class="certificate-header">
                  <h3 class="certificate-name">{{ certificate.name }}</h3>
                  <el-tag
                    :type="getStatusType(certificate.status)"
                    size="large"
                    effect="dark"
                  >
                    {{ getStatusText(certificate.status) }}
                  </el-tag>
                </div>

                <el-descriptions :column="2" border class="certificate-details">
                  <el-descriptions-item label="证书编号">
                    {{ certificate.number }}
                  </el-descriptions-item>
                  <el-descriptions-item label="证书类型">
                    {{ certificate.type }}
                  </el-descriptions-item>
                  <el-descriptions-item label="持有人">
                    {{ certificate.holder }}
                  </el-descriptions-item>
                  <el-descriptions-item label="联系方式">
                    {{ certificate.holderContact || '暂无' }}
                  </el-descriptions-item>
                  <el-descriptions-item label="发证机关">
                    {{ certificate.issuingAuthority }}
                  </el-descriptions-item>
                  <el-descriptions-item label="发证日期">
                    {{ formatDate(certificate.issueDate) }}
                  </el-descriptions-item>
                  <el-descriptions-item label="有效期起始">
                    {{ formatDate(certificate.validFrom) }}
                  </el-descriptions-item>
                  <el-descriptions-item label="有效期截止">
                    {{ formatDate(certificate.validUntil) }}
                  </el-descriptions-item>
                </el-descriptions>

                <div class="certificate-description" v-if="certificate.description">
                  <el-divider content-position="left">证书描述</el-divider>
                  <p>{{ certificate.description }}</p>
                </div>

                <div class="certificate-attachment" v-if="certificate.attachmentPath">
                  <el-divider content-position="left">附件</el-divider>
                  <el-link
                    :href="certificate.attachmentPath"
                    target="_blank"
                    type="primary"
                    :underline="false"
                  >
                    <el-icon><Document /></el-icon>
                    查看附件
                  </el-link>
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
const validateIdCard = (rule: any, value: string, callback: Function) => {
  if (value && value.length !== 18) {
    callback(new Error('身份证号必须为18位'))
  } else if (value && !/^\d{17}[\dXx]$/.test(value)) {
    callback(new Error('身份证号格式不正确'))
  } else {
    callback()
  }
}

const validateAtLeastOne = (rule: any, value: string, callback: Function) => {
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

    const response = await request.get('/certificate/public', { params })
    
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
        .certificate-item {
          margin-bottom: 30px;
          padding: 20px;
          background: var(--background-base);
          border-radius: 8px;
          transition: all 0.3s;

          &:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            transform: translateY(-2px);
          }

          &:last-child {
            margin-bottom: 0;
          }

          .certificate-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;

            .certificate-name {
              font-size: 22px;
              font-weight: 600;
              color: var(--text-primary);
              margin: 0;
            }
          }

          .certificate-details {
            margin-bottom: 20px;

            :deep(.el-descriptions__label) {
              font-weight: 500;
              color: var(--text-regular);
            }

            :deep(.el-descriptions__content) {
              color: var(--text-primary);
            }
          }

          .certificate-description {
            margin-top: 20px;

            p {
              color: var(--text-regular);
              line-height: 1.8;
              margin: 10px 0 0 0;
            }
          }

          .certificate-attachment {
            margin-top: 20px;
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
