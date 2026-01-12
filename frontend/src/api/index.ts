import axios, { type AxiosResponse } from 'axios'
import type { ApiResponse } from '@/types'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 可以在这里添加token等
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { data } = response
    // 后端返回的格式是 { code, message, data }
    if (data.code === 200 || data.code === 0) {
      // 直接返回data对象，这样外部可以通过 .data 访问实际数据
      return data as any
    } else {
      console.error('API Error:', data.message)
      return Promise.reject(new Error(data.message || '请求失败'))
    }
  },
  (error) => {
    console.error('Request Error:', error)
    // 网络错误或其他错误
    if (error.response) {
      // 服务器返回了错误状态码
      const { status, data } = error.response
      if (status === 404) {
        console.error('API endpoint not found')
      } else if (status >= 500) {
        console.error('Server error:', status)
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      console.error('No response received')
    } else {
      // 请求配置出错
      console.error('Request setup error:', error.message)
    }
    return Promise.reject(error)
  }
)

// 类型声明：让 TypeScript 知道拦截器返回的是 ApiResponse 而不是 AxiosResponse
declare module 'axios' {
  export interface AxiosInstance {
    get<T = any>(url: string, config?: any): Promise<ApiResponse<T>>
    post<T = any>(url: string, data?: any, config?: any): Promise<ApiResponse<T>>
    put<T = any>(url: string, data?: any, config?: any): Promise<ApiResponse<T>>
    delete<T = any>(url: string, data?: any, config?: any): Promise<ApiResponse<T>>
  }
}

export { request }
export default request