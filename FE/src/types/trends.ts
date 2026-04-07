export type Category = 'AI' | 'BE' | 'SECURITY'

export interface TrendsCardDto {
  id: number
  title: string
  sourceName: string
  originUrl: string
  category: Category
  publishedAt: string | null
  collectedAt: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}
