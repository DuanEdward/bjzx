<template>
  <div class="certificate-intro-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">证书说明</h1>
        <p class="page-subtitle">注意事项与申明</p>
      </div>

      <!-- 证书说明内容 -->
      <div class="intro-content">
        <el-card class="intro-card">
          <div class="notes-content" v-html="formattedNotes"></div>
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
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'

const notesContent = ref('')
const formattedNotes = ref('')

// 格式化文本内容，保证观赏性
const formatNotes = (text: string): string => {
  if (!text) return ''
  
  // 将文本按行分割
  const lines = text.split('\n')
  let html = ''
  let inList = false
  
  lines.forEach((line, index) => {
    const trimmedLine = line.trim()
    
    // 空行
    if (!trimmedLine) {
      if (inList) {
        html += '</ul>'
        inList = false
      }
      html += '<br>'
      return
    }
    
    // 标题行（以数字开头，如 "1、"）
    if (/^\d+[、.]/.test(trimmedLine)) {
      if (inList) {
        html += '</ul>'
        inList = false
      }
      html += `<h3 class="notes-title">${trimmedLine}</h3>`
      return
    }
    
    // 列表项（以 "①"、"②" 等开头）
    if (/^[①②③④⑤⑥⑦⑧⑨⑩]/.test(trimmedLine)) {
      if (!inList) {
        html += '<ul class="notes-list">'
        inList = true
      }
      html += `<li>${trimmedLine}</li>`
      return
    }
    
    // 普通段落
    if (inList) {
      html += '</ul>'
      inList = false
    }
    
    // 检查是否是重要段落（包含特定关键词）
    if (trimmedLine.includes('不能替代') || trimmedLine.includes('注意事项') || trimmedLine.includes('申明')) {
      html += `<p class="notes-important">${trimmedLine}</p>`
    } else {
      html += `<p class="notes-paragraph">${trimmedLine}</p>`
    }
  })
  
  if (inList) {
    html += '</ul>'
  }
  
  return html
}

// 加载notes文件内容
onMounted(async () => {
  try {
    const response = await fetch('/docs/notes')
    if (response.ok) {
      notesContent.value = await response.text()
      formattedNotes.value = formatNotes(notesContent.value)
    } else {
      // 如果文件不存在，使用默认内容
      formattedNotes.value = '<p>内容加载中...</p>'
    }
  } catch (error) {
    console.error('加载证书说明内容失败:', error)
    formattedNotes.value = '<p>内容加载失败，请稍后重试</p>'
  }
})
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
      padding: 30px;
    }

    .action-buttons {
      text-align: center;
      margin-top: 40px;
    }
  }
}

// 文本内容样式（使用深度选择器，因为内容是动态HTML）
:deep(.notes-content) {
  font-size: 16px;
  line-height: 2;
  color: var(--text-primary);

  .notes-title {
    font-size: 20px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 30px 0 15px 0;
    padding-bottom: 10px;
    border-bottom: 2px solid var(--primary-color);
  }

  .notes-paragraph {
    margin: 15px 0;
    text-indent: 2em;
    line-height: 2;
  }

  .notes-important {
    margin: 20px 0;
    padding: 15px 20px;
    background: #fff3cd;
    border-left: 4px solid #ffc107;
    border-radius: 4px;
    font-weight: 500;
    line-height: 2;
  }

  .notes-list {
    margin: 15px 0;
    padding-left: 30px;

    li {
      margin: 10px 0;
      line-height: 2;
    }
  }

  br {
    margin: 10px 0;
  }
}

@media (max-width: 768px) {
  .certificate-intro-page {
    padding: 20px 0;

    .intro-content {
      .intro-card {
        padding: 20px;
      }
    }
  }

  :deep(.notes-content) {
    font-size: 14px;

    .notes-title {
      font-size: 18px;
    }
  }
}
</style>

