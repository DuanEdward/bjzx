<template>
  <div class="home">
    <!-- Banner轮播 -->
    <section class="banner-section">
      <el-carousel height="500px" :interval="4000" arrow="hover">
        <el-carousel-item v-for="banner in banners" :key="banner.id">
          <a :href="banner.link" target="_blank" v-if="banner.link">
            <img :src="banner.image" :alt="banner.title">
          </a>
          <img v-else :src="banner.image" :alt="banner.title">
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 学会简介 -->
    <section class="society-intro section">
      <div class="container">
        <h2 class="section-title">中心简介</h2>
        <div class="intro-content">
          <div class="intro-text">
            <p>北京中兴建机职业技能鉴定中心是经相关部门批准设立的职业技能鉴定机构，致力于为建筑机械行业提供专业的职业技能鉴定服务。中心秉承"公正、专业、高效"的服务理念，为行业培养和认证技能人才，推动行业健康发展。</p>
          </div>
          <div class="intro-image">
            <!-- <img src="@/assets/images/society-intro.jpg" alt="中心简介" v-if="false"> -->
            <div class="placeholder-image">
              <el-icon size="120"><Picture /></el-icon>
              <p>中心形象图</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 新闻动态 -->
    <section class="news-section section">
      <div class="container">
        <h2 class="section-title">新闻动态</h2>
        <div class="section-content">
          <div class="news-list">
            <div class="news-item" v-for="news in newsList" :key="news.id" @click="viewNews(news.id)">
              <div class="news-image" v-if="news.coverImage">
                <img :src="news.coverImage" :alt="news.title">
              </div>
              <div class="news-content">
                <h3 class="news-title">{{ news.title }}</h3>
                <p class="news-summary">{{ news.summary }}</p>
                <div class="news-meta">
                  <span>{{ news.publishedAt }}</span>
                  <span><el-icon><View /></el-icon> {{ news.views }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="section-more">
            <el-button type="text" @click="$router.push('/news')">查看更多 <el-icon><ArrowRight /></el-icon></el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 快速入口 -->
    <section class="quick-links section bg-gray">
      <div class="container">
        <h2 class="section-title">快速入口</h2>
        <div class="section-content">
          <div class="links-grid">
            <div class="link-card" @click="$router.push('/notice')">
              <el-icon size="60"><Bell /></el-icon>
              <h3>通知公告</h3>
              <p>查看最新通知和公告</p>
            </div>
            <div class="link-card" @click="$router.push('/policy')">
              <el-icon size="60"><Document /></el-icon>
              <h3>政策法规</h3>
              <p>了解相关政策法规</p>
            </div>
            <div class="link-card" @click="$router.push('/industry')">
              <el-icon size="60"><TrendCharts /></el-icon>
              <h3>行业动态</h3>
              <p>掌握行业最新动态</p>
            </div>
            <div class="link-card" @click="$router.push('/study')">
              <el-icon size="60"><Reading /></el-icon>
              <h3>学习考试</h3>
              <p>学习资源和考试信息</p>
            </div>
            <div class="link-card" @click="$router.push('/certificate-intro')">
              <el-icon size="60"><InfoFilled /></el-icon>
              <h3>证书说明</h3>
              <p>了解证书相关信息</p>
            </div>
            <div class="link-card" @click="$router.push('/certificate-query')">
              <el-icon size="60"><Search /></el-icon>
              <h3>证书查询</h3>
              <p>查询证书信息</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Picture, View, ArrowRight, Bell, Document, TrendCharts, Reading, InfoFilled, Search } from '@element-plus/icons-vue'
import { getBanners, getLatestNews } from '@/api/modules/home'
import type { Banner, News } from '@/types'

const router = useRouter()
const banners = ref<Banner[]>([])
const newsList = ref<News[]>([])

// 查看新闻详情
const viewNews = (id: number) => {
  router.push(`/news/${id}`)
}

// 初始化数据
const initData = async () => {
  try {
    // 获取Banner数据
    const bannerRes = await getBanners()
    banners.value = (bannerRes.data as Banner[]) || []

    // 获取最新新闻
    const newsRes = await getLatestNews({ pageSize: 5 })
    newsList.value = (newsRes.data as any)?.list || []
  } catch (error) {
    console.error('获取首页数据失败:', error)
  }
}

onMounted(() => {
  initData()
})
</script>

<style lang="scss" scoped>
.home {
  .section {
    padding: 60px 0;

    &.bg-gray {
      background-color: var(--background-base);
    }

    .section-title {
      text-align: center;
      font-size: 32px;
      font-weight: 500;
      color: var(--text-primary);
      margin-bottom: 40px;
    }

    .section-content {
      max-width: 1200px;
      margin: 0 auto;
    }

    .section-more {
      text-align: center;
      margin-top: 30px;
    }
  }

  .banner-section {
    :deep(.el-carousel__item) {
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }

  .society-intro {
    .intro-content {
      display: flex;
      align-items: center;
      gap: 60px;
    }

    .intro-text {
      flex: 1;

      p {
        font-size: 16px;
        line-height: 1.8;
        color: var(--text-regular);
        margin-bottom: 30px;
      }
    }

    .intro-image {
      flex: 1;

      .placeholder-image {
        background: var(--background-light);
        height: 300px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: var(--text-secondary);
        border-radius: 8px;
      }
    }
  }

  .news-section {
    .news-list {
      display: flex;
      flex-direction: column;
      gap: 20px;
    }

    .news-item {
      display: flex;
      gap: 20px;
      padding: 20px;
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
        width: 200px;
        height: 150px;
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
        justify-content: space-between;

        .news-title {
          font-size: 18px;
          font-weight: 500;
          color: var(--text-primary);
          margin-bottom: 10px;
        }

        .news-summary {
          font-size: 14px;
          color: var(--text-regular);
          line-height: 1.6;
          margin-bottom: 15px;
        }

        .news-meta {
          display: flex;
          gap: 20px;
          font-size: 13px;
          color: var(--text-secondary);
        }
      }
    }
  }

  .quick-links {
    .links-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 30px;
    }

    .link-card {
      text-align: center;
      padding: 40px 20px;
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 30px 0 rgba(0, 0, 0, 0.1);
      }

      .el-icon {
        color: var(--primary-color);
        margin-bottom: 20px;
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
        margin: 0;
      }
    }
  }
}

@media (max-width: 768px) {
  .home {
    .section {
      padding: 40px 0;
    }

    .society-intro {
      .intro-content {
        flex-direction: column;
        gap: 30px;
      }
    }

    .news-section {
      .news-item {
        flex-direction: column;

        .news-image {
          width: 100%;
        }
      }
    }

    .quick-links {
      .links-grid {
        grid-template-columns: repeat(2, 1fr);
      }
    }
  }
}
</style>