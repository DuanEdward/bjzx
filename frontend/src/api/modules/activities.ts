import request from '../index'
import type { Activity, ApiResponse, PaginationResponse, PaginationParams } from '@/types'

// 获取活动列表
export function getActivitiesList(params?: PaginationParams & {
  status?: string
  startDate?: string
  endDate?: string
}) {
  return request.get<ApiResponse<PaginationResponse<Activity>>>('/activities/list', { params })
}

// 获取活动详情
export function getActivityDetail(id: number) {
  return request.get<ApiResponse<Activity>>(`/activities/${id}`)
}

// 活动报名
export function registerActivity(id: number, data: {
  name: string
  phone: string
  email: string
  company: string
  remark?: string
}) {
  return request.post<ApiResponse<null>>(`/activities/${id}/register`, data)
}