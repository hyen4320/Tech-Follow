import axios from 'axios'
import type { Category, Page, TrendsCardDto } from '../types/trends'

const api = axios.create({ baseURL: '/api' })

export const fetchTrends = async (
  category: Category | null,
  page: number,
  size = 20,
): Promise<Page<TrendsCardDto>> => {
  const params: Record<string, string | number> = { page, size }
  if (category) params.category = category
  const { data } = await api.get<Page<TrendsCardDto>>('/trends', { params })
  return data
}
