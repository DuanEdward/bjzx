<template>
  <div class="news-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">新闻中心</h1>
        <p class="page-subtitle">了解学会最新动态和行业资讯</p>
      </div>

      <!-- 新闻分类 -->
      <div class="news-categories">
        <el-tabs v-model="activeCategory" @tab-change="handleCategoryChange">
          <el-tab-pane label="全部" name="all"></el-tab-pane>
          <el-tab-pane label="学会动态" name="society"></el-tab-pane>
          <el-tab-pane label="行业资讯" name="industry"></el-tab-pane>
          <el-tab-pane label="通知公告" name="notice"></el-tab-pane>
        </el-tabs>
      </div>

      <!-- 新闻列表 -->
      <div class="news-list">
        <div class="news-item" v-for="news in newsList" :key="news.id" @click="viewNews(news.id)">
          <div class="news-image" v-if="news.coverImage">
            <img :src="news.coverImage" :alt="news.title">
          </div>
          <div class="news-content">
            <div class="news-header">
              <el-tag v-if="news.isTop" type="danger" size="small">置顶</el-tag>
              <el-tag type="info" size="small">{{ news.categoryName }}</el-tag>
              <span class="news-date">{{ news.publishedAt }}</span>
            </div>
            <h3 class="news-title">{{ news.title }}</h3>
            <p class="news-summary">{{ news.summary }}</p>
            <div class="news-footer">
              <span class="news-author">{{ news.author }}</span>
              <span class="news-views"><el-icon><View /></el-icon> {{ news.views }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="!loading && newsList.length === 0" description="暂无新闻数据" />

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { View } from '@element-plus/icons-vue'
import { getNewsList } from '@/api/modules/news'
import type { News, PaginationParams } from '@/types'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const activeCategory = ref('all')
const newsList = ref<News[]>([])
const total = ref(0)
const pagination = reactive<PaginationParams>({
  page: 1,
  pageSize: 10
})

// 获取新闻列表
const fetchNewsList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      category: activeCategory.value === 'all' ? undefined : activeCategory.value
    }
    const response = await getNewsList(params)
    newsList.value = response.data?.list || []
    total.value = response.data?.total || 0
  } catch (error) {
    console.error('获取新闻列表失败:', error)
    ElMessage.error('获取新闻列表失败')
  } finally {
    loading.value = false
  }
}

// 查看新闻详情
const viewNews = (id: number) => {
  router.push(`/news/${id}`)
}

// 分类切换
const handleCategoryChange = () => {
  pagination.page = 1
  fetchNewsList()
}

// 每页数量变化
const handleSizeChange = () => {
  pagination.page = 1
  fetchNewsList()
}

// 页码变化
const handleCurrentChange = () => {
  fetchNewsList()
}

onMounted(() => {
  fetchNewsList()
})
</script>

<style lang="scss" scoped>
.news-page {
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

  .news-categories {
    margin-bottom: 30px;

    :deep(.el-tabs__header) {
      margin-bottom: 0;
    }
  }

  .news-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .news-item {
    display: flex;
    gap: 20px;
    padding: 24px;
    background: #fff;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.1);
    }

    .news-image {
      width: 240px;
      height: 160px;
      flex-shrink: 0;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 4px;
      }
    }

    .news-content {
      flex: 1;
      display: flex;
      flex-direction: column;
    }

    .news-header {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 12px;

      .news-date {
        font-size: 13px;
        color: var(--text-secondary);
        margin-left: auto;
      }
    }

    .news-title {
      font-size: 20px;
      font-weight: 500;
      color: var(--text-primary);
      line-height: 1.4;
      margin-bottom: 12px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .news-summary {
      font-size: 14px;
      color: var(--text-regular);
      line-height: 1.6;
      margin-bottom: 15px;
      flex: 1;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .news-footer {
      display: flex;
      justify-content: space-between;
      font-size: 13px;
      color: var(--text-secondary);
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 40px;
  }
}

@media (max-width: 768px) {
  .news-page {
    padding: 20px 0;

    .news-item {
      flex-direction: column;

      .news-image {
        width: 100%;
        height: 200px;
      }
    }
  }
}
</style>