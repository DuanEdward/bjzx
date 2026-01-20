<template>
  <header class="app-header">
    <div class="container">
      <div class="header-content">
        <div class="header-top">
          <div class="header-actions">
            <div class="beijing-time">
              <span class="time-label">北京时间：</span>
              <span class="time-value">{{ beijingTime }}</span>
            </div>
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
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeIndex = computed(() => route.path)

// 实时北京时间
const beijingTime = ref('')

const updateTime = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  beijingTime.value = `${year}年${month}月${day}日 ${hours}:${minutes}:${seconds}`
}

let timeInterval: number | null = null

onMounted(() => {
  updateTime()
  timeInterval = window.setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timeInterval !== null) {
    clearInterval(timeInterval)
  }
})
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
    align-items: center;
    justify-content: flex-end;

    .beijing-time {
      display: flex;
      align-items: center;
      gap: 8px;
      color: var(--text-secondary);
      font-size: 13px;

      .time-label {
        color: var(--text-secondary);
      }

      .time-value {
        color: var(--primary-color);
        font-weight: 500;
        font-family: 'Courier New', monospace;
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
      .beijing-time {
        font-size: 11px;

        .time-value {
          font-size: 11px;
        }
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