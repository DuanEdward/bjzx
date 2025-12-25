<template>
  <div class="news-detail">
    <div class="container">
      <div class="back-nav">
        <el-button @click="$router.back()" :icon="ArrowLeft">返回</el-button>
      </div>

      <div class="news-content" v-if="newsDetail">
        <h1 class="news-title">{{ newsDetail.title }}</h1>

        <div class="news-meta">
          <span class="meta-item">
            <el-icon><Calendar /></el-icon>
            {{ newsDetail.publishedAt }}
          </span>
          <span class="meta-item">
            <el-icon><User /></el-icon>
            {{ newsDetail.author }}
          </span>
          <span class="meta-item">
            <el-icon><View /></el-icon>
            阅读 {{ newsDetail.views }}
          </span>
          <span class="meta-item">
            <el-tag>{{ newsDetail.categoryName }}</el-tag>
          </span>
        </div>

        <div class="news-cover" v-if="newsDetail.coverImage">
          <img :src="newsDetail.coverImage" :alt="newsDetail.title">
        </div>

        <div class="news-body" v-html="newsDetail.content"></div>
      </div>

      <!-- 加载状态 -->
      <el-skeleton v-else-if="loading" animated>
        <template #template>
          <el-skeleton-item variant="h1" style="width: 60%; margin-bottom: 20px" />
          <el-skeleton-item variant="text" style="width: 40%; margin-bottom: 30px" />
          <el-skeleton-item variant="image" style="width: 100%; height: 400px; margin-bottom: 30px" />
          <el-skeleton-item variant="p" style="width: 100%; margin-bottom: 10px" />
          <el-skeleton-item variant="p" style="width: 100%; margin-bottom: 10px" />
          <el-skeleton-item variant="p" style="width: 80%" />
        </template>
      </el-skeleton>

      <!-- 错误状态 -->
      <el-result v-else icon="error" title="加载失败" sub-title="新闻详情加载失败，请稍后重试">
        <template #extra>
          <el-button type="primary" @click="$router.back()">返回</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Calendar, User, View } from '@element-plus/icons-vue'
import { getNewsDetail, incrementNewsViews } from '@/api/modules/news'
import type { News } from '@/types'

const route = useRoute()
const newsId = Number(route.params.id)
const loading = ref(true)
const newsDetail = ref<News | null>(null)

// 获取新闻详情
const fetchNewsDetail = async () => {
  try {
    loading.value = true
    const response = await getNewsDetail(newsId)
    newsDetail.value = response.data as News

    // 增加浏览量
    incrementNewsViews(newsId).catch(console.error)
  } catch (error) {
    console.error('获取新闻详情失败:', error)
    ElMessage.error('获取新闻详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchNewsDetail()
})
</script>

<style lang="scss" scoped>
.news-detail {
  padding: 40px 0;
  min-height: calc(100vh - 160px);

  .back-nav {
    margin-bottom: 30px;
  }

  .news-content {
    background: #fff;
    padding: 40px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  }

  .news-title {
    font-size: 32px;
    font-weight: 500;
    color: var(--text-primary);
    line-height: 1.4;
    margin-bottom: 20px;
  }

  .news-meta {
    display: flex;
    align-items: center;
    gap: 20px;
    padding-bottom: 20px;
    border-bottom: 1px solid var(--border-light);
    margin-bottom: 30px;

    .meta-item {
      display: flex;
      align-items: center;
      gap: 5px;
      font-size: 14px;
      color: var(--text-secondary);
    }
  }

  .news-cover {
    margin-bottom: 30px;

    img {
      width: 100%;
      max-height: 500px;
      object-fit: cover;
      border-radius: 8px;
    }
  }

  .news-body {
    font-size: 16px;
    line-height: 1.8;
    color: var(--text-regular);

    :deep(img) {
      max-width: 100%;
      height: auto;
      border-radius: 4px;
      margin: 20px 0;
    }

    :deep(p) {
      margin-bottom: 15px;
    }

    :deep(h2) {
      font-size: 24px;
      font-weight: 500;
      color: var(--text-primary);
      margin: 30px 0 15px;
    }

    :deep(h3) {
      font-size: 20px;
      font-weight: 500;
      color: var(--text-primary);
      margin: 25px 0 10px;
    }

    :deep(ul), :deep(ol) {
      padding-left: 30px;
      margin-bottom: 15px;
    }

    :deep(li) {
      margin-bottom: 8px;
    }

    :deep(blockquote) {
      background: var(--background-base);
      border-left: 4px solid var(--primary-color);
      padding: 15px 20px;
      margin: 20px 0;
      border-radius: 4px;
    }

    :deep(code) {
      background: var(--background-base);
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 14px;
    }

    :deep(pre) {
      background: #2d2d2d;
      color: #f8f8f2;
      padding: 20px;
      border-radius: 8px;
      overflow-x: auto;
      margin: 20px 0;
    }

    :deep(table) {
      width: 100%;
      border-collapse: collapse;
      margin: 20px 0;

      th, td {
        border: 1px solid var(--border-base);
        padding: 10px;
        text-align: left;
      }

      th {
        background: var(--background-base);
        font-weight: 500;
      }
    }
  }
}

@media (max-width: 768px) {
  .news-detail {
    padding: 20px 0;

    .news-content {
      padding: 20px;
    }

    .news-title {
      font-size: 24px;
    }

    .news-meta {
      flex-wrap: wrap;
      gap: 10px;
    }
  }
}
</style>