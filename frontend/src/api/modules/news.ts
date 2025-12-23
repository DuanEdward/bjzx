import request from '../index'
import type { News, ApiResponse, PaginationResponse, PaginationParams } from '@/types'

// 获取新闻列表
export function getNewsList(params?: PaginationParams & { category?: string }) {
  return request.get<ApiResponse<PaginationResponse<News>>>('/news/list', { params })
}

// 获取新闻详情
export function getNewsDetail(id: number) {
  return request.get<ApiResponse<News>>(`/news/${id}`)
}

// 增加新闻浏览量
export function incrementNewsViews(id: number) {
  return request.post<ApiResponse<null>>(`/news/${id}/views`)
}