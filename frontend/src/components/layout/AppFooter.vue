<template>
  <footer class="app-footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section">
          <h3>北京中兴建机职业技能鉴定中心</h3>
          <p>Beijing Zhongxing Construction Machinery Vocational Skills Appraisal Center</p>
          <p>致力于职业技能鉴定与人才培养</p>
        </div>
        <div class="footer-section">
          <h4>快速服务</h4>
          <div class="footer-actions">
            <el-button type="text" size="small" @click="showFeedbackDialog = true">投诉反馈</el-button>
            <el-button type="text" size="small" @click="showContactDialog = true">联系我们</el-button>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; {{ currentYear }} 北京中兴建机职业技能鉴定中心 版权所有</p>
        <p><a href="https://beian.miit.gov.cn" target="_blank">京ICP备2025158024号-1</a></p>
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
  </footer>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import request from '@/api'

const currentYear = computed(() => new Date().getFullYear())

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
.app-footer {
  background: #2c3e50;
  color: #ecf0f1;
  padding: 40px 0 20px;

  .footer-content {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 30px;
    margin-bottom: 30px;
  }

  .footer-section {
    h3 {
      font-size: 20px;
      margin-bottom: 15px;
      color: #fff;
    }

    h4 {
      font-size: 16px;
      margin-bottom: 10px;
      color: #fff;
    }

    p {
      margin-bottom: 8px;
      font-size: 14px;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .footer-actions {
      display: flex;
      gap: 15px;

      .el-button {
        color: #bdc3c7;
        font-size: 14px;
        padding: 8px 16px;

        &:hover {
          color: #fff;
        }
      }
    }

    .qrcode {
      text-align: center;

      img {
        width: 100px;
        height: 100px;
        margin-bottom: 10px;
        background: #fff;
        padding: 5px;
        border-radius: 4px;
      }

      p {
        font-size: 12px;
      }
    }
  }

  .footer-bottom {
    text-align: center;
    padding-top: 20px;
    border-top: 1px solid #34495e;

    p {
      font-size: 13px;
      color: #95a5a6;
      margin-bottom: 5px;

      a {
        color: #95a5a6;

        &:hover {
          color: #bdc3c7;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .app-footer {
    padding: 30px 0 15px;

    .footer-content {
      gap: 20px;
    }
  }
}
</style>