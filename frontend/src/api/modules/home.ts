import request from '../index'
import type { Banner, News, Activity, ApiResponse, PaginationResponse, PaginationParams } from '@/types'

// 获取Banner列表
export function getBanners() {
  return request.get<ApiResponse<Banner[]>>('/home/banners')
}

// 获取最新新闻
export function getLatestNews(params?: Partial<PaginationParams>) {
  return request.get<ApiResponse<News[]>>('/home/latest-news', { params })
}

// 获取最新活动
export function getLatestActivities(params?: Partial<PaginationParams>) {
  return request.get<ApiResponse<PaginationResponse<Activity>>>('/home/latest-activities', { params })
}

// 获取学会简介
export function getSocietyInfo() {
  return request.get<ApiResponse<any>>('/home/society-info')
}