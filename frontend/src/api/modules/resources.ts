import request from '../index'
import type { Resource, ApiResponse, PaginationResponse, PaginationParams } from '@/types'

// 获取资源列表
export function getResourcesList(params?: PaginationParams & { category?: string }) {
  return request.get<ApiResponse<PaginationResponse<Resource>>>('/resources/list', { params })
}

// 获取资源详情
export function getResourceDetail(id: number) {
  return request.get<ApiResponse<Resource>>(`/resources/${id}`)
}

// 增加下载次数
export function incrementResourceDownload(id: number) {
  return request.post<ApiResponse<null>>(`/resources/${id}/download`)
}