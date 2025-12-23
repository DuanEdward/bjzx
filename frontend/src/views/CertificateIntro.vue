<template>
  <div class="certificate-intro-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">证书说明</h1>
        <p class="page-subtitle">了解各类证书的申请流程、使用说明和注意事项</p>
      </div>

      <!-- 证书说明内容 -->
      <div class="intro-content">
        <el-card class="intro-card">
          <template #header>
            <div class="card-header">
              <h2>证书类型</h2>
            </div>
          </template>
          <div class="certificate-types">
            <div class="type-item" v-for="type in certificateTypes" :key="type.name">
              <el-icon :size="40" :color="type.color"><component :is="type.icon" /></el-icon>
              <h3>{{ type.name }}</h3>
              <p>{{ type.description }}</p>
            </div>
          </div>
        </el-card>

        <el-card class="intro-card">
          <template #header>
            <div class="card-header">
              <h2>申请流程</h2>
            </div>
          </template>
          <el-steps :active="0" direction="vertical" class="steps">
            <el-step
              v-for="(step, index) in applicationSteps"
              :key="index"
              :title="step.title"
              :description="step.description"
            />
          </el-steps>
        </el-card>

        <el-card class="intro-card">
          <template #header>
            <div class="card-header">
              <h2>使用说明</h2>
            </div>
          </template>
          <div class="usage-instructions">
            <div class="instruction-item" v-for="(instruction, index) in usageInstructions" :key="index">
              <el-icon class="instruction-icon"><CircleCheck /></el-icon>
              <div class="instruction-content">
                <h4>{{ instruction.title }}</h4>
                <p>{{ instruction.content }}</p>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="intro-card">
          <template #header>
            <div class="card-header">
              <h2>注意事项</h2>
            </div>
          </template>
          <div class="notices">
            <el-alert
              v-for="(notice, index) in importantNotices"
              :key="index"
              :title="notice.title"
              :type="notice.type"
              :closable="false"
              class="notice-alert"
            >
              <template #default>
                <p>{{ notice.content }}</p>
              </template>
            </el-alert>
          </div>
        </el-card>

        <div class="action-buttons">
          <el-button type="primary" size="large" @click="$router.push('/certificate-query')">
            <el-icon><Search /></el-icon>
            查询证书
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Document, CircleCheck, Search, InfoFilled, WarningFilled, SuccessFilled } from '@element-plus/icons-vue'

// 证书类型
const certificateTypes = [
  {
    name: '营业执照',
    description: '企业法人营业执照，证明企业合法经营资格',
    icon: 'Document',
    color: '#409EFF'
  },
  {
    name: '组织机构代码证',
    description: '组织机构代码证，用于标识组织机构',
    icon: 'Document',
    color: '#67C23A'
  },
  {
    name: '税务登记证',
    description: '税务登记证，用于税务管理',
    icon: 'Document',
    color: '#E6A23C'
  },
  {
    name: '其他证件',
    description: '其他各类有效证件',
    icon: 'Document',
    color: '#909399'
  }
]

// 申请流程
const applicationSteps = [
  {
    title: '准备材料',
    description: '准备相关申请材料，包括身份证明、申请表等'
  },
  {
    title: '提交申请',
    description: '在线或线下提交证书申请'
  },
  {
    title: '审核处理',
    description: '相关部门审核申请材料'
  },
  {
    title: '领取证书',
    description: '审核通过后领取证书'
  }
]

// 使用说明
const usageInstructions = [
  {
    title: '证书查询',
    content: '可通过证书编号、持有人姓名等信息查询证书状态和详细信息'
  },
  {
    title: '证书验证',
    content: '证书信息已通过相关部门审核，确保真实有效'
  },
  {
    title: '有效期管理',
    content: '请关注证书有效期，及时办理续期手续'
  },
  {
    title: '信息更新',
    content: '如证书信息有变更，请及时联系相关部门更新'
  }
]

// 注意事项
const importantNotices = [
  {
    title: '证书有效期',
    content: '请注意证书的有效期，过期证书将无法使用。建议在有效期到期前30天办理续期手续。',
    type: 'warning' as const
  },
  {
    title: '信息真实性',
    content: '所有证书信息均经过严格审核，确保真实有效。如有疑问，请联系相关部门核实。',
    type: 'info' as const
  },
  {
    title: '证书保管',
    content: '请妥善保管证书，避免丢失或损坏。如证书遗失，请及时联系相关部门补办。',
    type: 'success' as const
  }
]
</script>

<style lang="scss" scoped>
.certificate-intro-page {
  padding: 40px 0;
  min-height: calc(100vh - 160px);

  .page-header {
    text-align: center;
    margin-bottom: 40px;

    .page-title {
      font-size: 32px;
      font-weight: 500;
      color: var(--text-primary);
      margin-bottom: 10px;
    }

    .page-subtitle {
      font-size: 16px;
      color: var(--text-regular);
    }
  }

  .intro-content {
    max-width: 1000px;
    margin: 0 auto;

    .intro-card {
      margin-bottom: 30px;

      .card-header {
        h2 {
          font-size: 20px;
          font-weight: 500;
          color: var(--text-primary);
          margin: 0;
        }
      }
    }

    .certificate-types {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 30px;
      padding: 20px 0;

      .type-item {
        text-align: center;
        padding: 20px;
        background: var(--background-base);
        border-radius: 8px;
        transition: all 0.3s;

        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        .el-icon {
          margin-bottom: 15px;
        }

        h3 {
          font-size: 18px;
          font-weight: 500;
          color: var(--text-primary);
          margin-bottom: 10px;
        }

        p {
          font-size: 14px;
          color: var(--text-regular);
          line-height: 1.6;
        }
      }
    }

    .steps {
      padding: 20px 0;
    }

    .usage-instructions {
      .instruction-item {
        display: flex;
        gap: 15px;
        padding: 20px;
        margin-bottom: 15px;
        background: var(--background-base);
        border-radius: 8px;

        .instruction-icon {
          font-size: 24px;
          color: var(--primary-color);
          flex-shrink: 0;
          margin-top: 2px;
        }

        .instruction-content {
          flex: 1;

          h4 {
            font-size: 16px;
            font-weight: 500;
            color: var(--text-primary);
            margin-bottom: 8px;
          }

          p {
            font-size: 14px;
            color: var(--text-regular);
            line-height: 1.6;
            margin: 0;
          }
        }
      }
    }

    .notices {
      .notice-alert {
        margin-bottom: 15px;

        p {
          margin: 0;
          line-height: 1.6;
        }
      }
    }

    .action-buttons {
      text-align: center;
      margin-top: 40px;
    }
  }
}

@media (max-width: 768px) {
  .certificate-intro-page {
    padding: 20px 0;

    .intro-content {
      .certificate-types {
        grid-template-columns: 1fr;
      }
    }
  }
}
</style>

