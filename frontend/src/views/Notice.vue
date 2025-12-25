<template>
  <div class="notice-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">通知公告</h1>
        <p class="page-subtitle">查看学会最新通知和公告信息</p>
      </div>

      <!-- 通知列表 -->
      <div class="notice-list">
        <div class="notice-item" v-for="notice in noticeList" :key="notice.id" @click="viewNotice(notice.id)">
          <div class="notice-image" v-if="notice.coverImage">
            <img :src="notice.coverImage" :alt="notice.title">
          </div>
          <div class="notice-content">
            <div class="notice-header">
              <el-tag v-if="notice.isTop" type="danger" size="small">置顶</el-tag>
              <span class="notice-date">{{ notice.publishedAt }}</span>
            </div>
            <h3 class="notice-title">{{ notice.title }}</h3>
            <p class="notice-summary">{{ notice.summary }}</p>
            <div class="notice-footer">
              <span class="notice-author">{{ notice.author }}</span>
              <span class="notice-views"><el-icon><View /></el-icon> {{ notice.views }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="!loading && noticeList.length === 0" description="暂无通知公告" />

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
import { ElMessage } from 'element-plus'
import { View } from '@element-plus/icons-vue'
import { getNewsList } from '@/api/modules/news'
import type { News, PaginationParams, PaginationResponse } from '@/types'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const noticeList = ref<News[]>([])
const total = ref(0)
const pagination = reactive<PaginationParams>({
  page: 1,
  pageSize: 10
})

// 获取通知公告列表
const fetchNoticeList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      category: 'notice' // 只获取通知公告分类
    }
    const response = await getNewsList(params)
    const pageData = response.data as PaginationResponse<News>
    noticeList.value = pageData?.list || []
    total.value = pageData?.total || 0
  } catch (error) {
    console.error('获取通知公告列表失败:', error)
    ElMessage.error('获取通知公告列表失败')
  } finally {
    loading.value = false
  }
}

// 查看通知详情
const viewNotice = (id: number) => {
  router.push(`/notice/${id}`)
}

// 每页数量变化
const handleSizeChange = () => {
  pagination.page = 1
  fetchNoticeList()
}

// 页码变化
const handleCurrentChange = () => {
  fetchNoticeList()
}

onMounted(() => {
  fetchNoticeList()
})
</script>

<style lang="scss" scoped>
.notice-page {
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

  .notice-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .notice-item {
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

    .notice-image {
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

    .notice-content {
      flex: 1;
      display: flex;
      flex-direction: column;
    }

    .notice-header {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 12px;

      .notice-date {
        font-size: 13px;
        color: var(--text-secondary);
        margin-left: auto;
      }
    }

    .notice-title {
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

    .notice-summary {
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

    .notice-footer {
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
  .notice-page {
    padding: 20px 0;

    .notice-item {
      flex-direction: column;

      .notice-image {
        width: 100%;
        height: 200px;
      }
    }
  }
}
</style>

