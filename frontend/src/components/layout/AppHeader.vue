<template>
  <header class="app-header">
    <div class="container">
      <div class="header-content">
        <div class="header-top">
          <div class="header-actions">
            <el-button type="text" size="small" @click="showFeedbackDialog = true">投诉反馈</el-button>
            <el-button type="text" size="small" @click="showContactDialog = true">联系我们</el-button>
          </div>
        </div>
        <div class="header-main">
          <div class="logo-section">
            <div class="logo">
              <img src="/banner.jpg" alt="Logo" class="logo-image">
              <h1>北京中兴建机职业技能鉴定中心</h1>
            </div>
          </div>
          <nav class="nav-menu">
            <el-menu mode="horizontal" :default-active="activeIndex" :router="true" :collapse="false">
              <el-menu-item index="/">首页</el-menu-item>
              <el-menu-item index="/news">新闻中心</el-menu-item>
              <el-menu-item index="/notice">通知公告</el-menu-item>
              <el-menu-item index="/policy">政策法规</el-menu-item>
              <el-menu-item index="/industry">行业动态</el-menu-item>
              <el-menu-item index="/study">学习考试</el-menu-item>
              <el-menu-item index="/certificate/intro">证书说明</el-menu-item>
              <el-menu-item index="/certificate/query">证书查询</el-menu-item>
            </el-menu>
          </nav>
        </div>
      </div>

      <!-- 投诉反馈对话框 -->
      <el-dialog v-model="showFeedbackDialog" title="投诉反馈" width="500px">
        <el-form :model="feedbackForm" :rules="feedbackRules" ref="feedbackFormRef" label-width="80px">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="feedbackForm.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="feedbackForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="反馈内容" prop="content">
            <el-input
              v-model="feedbackForm.content"
              type="textarea"
              :rows="5"
              placeholder="请输入反馈内容"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showFeedbackDialog = false">取消</el-button>
          <el-button type="primary" @click="submitFeedback" :loading="feedbackLoading">提交</el-button>
        </template>
      </el-dialog>

      <!-- 联系我们对话框 -->
      <el-dialog v-model="showContactDialog" title="联系我们" width="500px">
        <el-form :model="contactForm" :rules="contactRules" ref="contactFormRef" label-width="80px">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="contactForm.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="contactForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="留言内容" prop="message">
            <el-input
              v-model="contactForm.message"
              type="textarea"
              :rows="5"
              placeholder="请输入留言内容"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showContactDialog = false">取消</el-button>
          <el-button type="primary" @click="submitContact" :loading="contactLoading">提交</el-button>
        </template>
      </el-dialog>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import request from '@/api'

const route = useRoute()
const activeIndex = computed(() => route.path)

// 投诉反馈
const showFeedbackDialog = ref(false)
const feedbackFormRef = ref<FormInstance>()
const feedbackLoading = ref(false)
const feedbackForm = ref({
  name: '',
  phone: '',
  content: ''
})

const feedbackRules: FormRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  content: [{ required: true, message: '请输入反馈内容', trigger: 'blur' }]
}

const submitFeedback = async () => {
  if (!feedbackFormRef.value) return
  await feedbackFormRef.value.validate(async (valid) => {
    if (valid) {
      feedbackLoading.value = true
      try {
        await request.post('/feedback', feedbackForm.value)
        ElMessage.success('提交成功，我们会尽快处理')
        showFeedbackDialog.value = false
        feedbackForm.value = { name: '', phone: '', content: '' }
      } catch (error) {
        ElMessage.error('提交失败，请稍后重试')
      } finally {
        feedbackLoading.value = false
      }
    }
  })
}

// 联系我们
const showContactDialog = ref(false)
const contactFormRef = ref<FormInstance>()
const contactLoading = ref(false)
const contactForm = ref({
  name: '',
  phone: '',
  message: ''
})

const contactRules: FormRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  message: [{ required: true, message: '请输入留言内容', trigger: 'blur' }]
}

const submitContact = async () => {
  if (!contactFormRef.value) return
  await contactFormRef.value.validate(async (valid) => {
    if (valid) {
      contactLoading.value = true
      try {
        await request.post('/contact', contactForm.value)
        ElMessage.success('提交成功，我们会尽快联系您')
        showContactDialog.value = false
        contactForm.value = { name: '', phone: '', message: '' }
      } catch (error) {
        ElMessage.error('提交失败，请稍后重试')
      } finally {
        contactLoading.value = false
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.app-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;

  .header-content {
    width: 100%;
  }

  .header-top {
    display: flex;
    justify-content: flex-end;
    padding: 8px 0;
    border-bottom: 1px solid #f0f0f0;
  }

  .header-actions {
    display: flex;
    gap: 8px;

    .el-button {
      color: var(--text-secondary);
      font-size: 12px;
      padding: 4px 8px;

      &:hover {
        color: var(--primary-color);
      }
    }
  }

  .header-main {
    display: flex;
    flex-direction: column;
    padding: 10px 0;
  }

  .logo-section {
    width: 100%;
    margin-bottom: 10px;
  }

  .logo {
    display: flex;
    align-items: center;
    gap: 15px;

    .logo-image {
      height: 60px;
      width: auto;
      object-fit: contain;
      flex-shrink: 0;
    }

    h1 {
      font-size: 24px;
      font-weight: 500;
      color: var(--text-primary);
      margin: 0;
      white-space: normal;
      word-break: break-all;
      line-height: 1.4;
    }
  }

  .nav-menu {
    width: 100%;
    overflow-x: auto;
    overflow-y: hidden;

    :deep(.el-menu) {
      border-bottom: none;
      background: transparent;
      display: flex;
      flex-wrap: nowrap;
      min-width: max-content;

      .el-menu-item {
        font-size: 15px;
        height: 50px;
        line-height: 50px;
        padding: 0 15px;
        white-space: nowrap;
        flex-shrink: 0;

        &:hover {
          background-color: var(--background-base);
        }

        &.is-active {
          color: var(--primary-color);
          border-bottom-color: var(--primary-color);
        }
      }
    }
  }
}

@media (max-width: 1200px) {
  .app-header {
    .logo {
      h1 {
        font-size: 20px;
      }
    }

    .nav-menu {
      :deep(.el-menu) {
        .el-menu-item {
          font-size: 14px;
          padding: 0 12px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .app-header {
    .header-top {
      padding: 5px 0;
    }

    .header-actions {
      .el-button {
        font-size: 11px;
        padding: 2px 6px;
      }
    }

    .header-main {
      padding: 8px 0;
    }

    .logo-section {
      margin-bottom: 8px;
    }

    .logo {
      .logo-image {
        height: 50px;
      }

      h1 {
        font-size: 18px;
      }
    }

    .nav-menu {
      width: 100%;

      :deep(.el-menu) {
        .el-menu-item {
          font-size: 13px;
          height: 45px;
          line-height: 45px;
          padding: 0 10px;
        }
      }
    }
  }
}
</style>