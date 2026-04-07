import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { fetchTrends } from './api/trends'
import type { Category } from './types/trends'
import TrendCard from './components/TrendCard'
import SkeletonCard from './components/SkeletonCard'
import CategoryFilter from './components/CategoryFilter'
import Pagination from './components/Pagination'

export default function App() {
  const [category, setCategory] = useState<Category | null>(null)
  const [page, setPage]         = useState(0)

  const { data, isLoading, isError } = useQuery({
    queryKey: ['trends', category, page],
    queryFn:  () => fetchTrends(category, page),
    placeholderData: (prev) => prev,
  })

  function handleCategoryChange(c: Category | null) {
    setCategory(c)
    setPage(0)
  }

  return (
    <div className="min-h-screen bg-ink-950 text-ink-100 font-body">

      {/* 헤더 */}
      <header className="sticky top-0 z-10 bg-ink-950/80 backdrop-blur-md border-b border-ink-800">
        <div className="max-w-6xl mx-auto px-6 py-4 flex items-center justify-between">
          <div>
            <h1 className="font-display text-xl text-ink-50 tracking-tight">
              Tech<span className="italic text-ink-400">Follow</span>
            </h1>
          </div>
          {data && (
            <span className="text-xs font-mono text-ink-600">
              {data.totalElements.toLocaleString()} articles
            </span>
          )}
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-6 py-10">

        {/* 필터 */}
        <div className="mb-8 flex items-center justify-between gap-4 flex-wrap">
          <CategoryFilter active={category} onChange={handleCategoryChange} />
        </div>

        {/* 에러 */}
        {isError && (
          <div className="text-center py-24 text-ink-400 font-mono text-sm">
            서버에 연결할 수 없습니다. BE가 실행 중인지 확인해 주세요.
          </div>
        )}

        {/* 카드 그리드 */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          {isLoading
            ? Array.from({ length: 20 }).map((_, i) => <SkeletonCard key={i} />)
            : data?.content.map((card, i) => (
                <TrendCard key={card.id} card={card} index={i} />
              ))
          }
        </div>

        {/* 결과 없음 */}
        {!isLoading && data?.content.length === 0 && (
          <div className="text-center py-24 text-ink-400 font-mono text-sm">
            수집된 기사가 없습니다.
          </div>
        )}

        {/* 페이지네이션 */}
        {data && (
          <Pagination
            current={data.number}
            total={data.totalPages}
            onChange={setPage}
          />
        )}
      </main>
    </div>
  )
}
