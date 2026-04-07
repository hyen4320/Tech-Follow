import type { TrendsCardDto } from '../types/trends'

const CATEGORY_STYLE = {
  AI:       { label: 'AI',       dot: 'bg-signal-ai',       text: 'text-signal-ai' },
  BE:       { label: 'Backend',  dot: 'bg-signal-be',       text: 'text-signal-be' },
  SECURITY: { label: 'Security', dot: 'bg-signal-security', text: 'text-signal-security' },
}

function formatDate(iso: string | null): string {
  if (!iso) return ''
  return new Date(iso).toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' })
}

interface Props {
  card: TrendsCardDto
  index: number
}

export default function TrendCard({ card, index }: Props) {
  const cat = CATEGORY_STYLE[card.category]

  return (
    <a
      href={card.originUrl}
      target="_blank"
      rel="noopener noreferrer"
      className="group block"
      style={{ animationDelay: `${index * 60}ms` }}
    >
      <article className="
        animate-fade-up opacity-0
        h-full flex flex-col gap-4
        bg-ink-900 border border-ink-700
        rounded-2xl p-6
        transition-all duration-300
        hover:border-ink-600 hover:bg-ink-800
        hover:-translate-y-1 hover:shadow-2xl hover:shadow-black/40
      ">
        {/* 상단: 카테고리 + 날짜 */}
        <div className="flex items-center justify-between">
          <span className={`flex items-center gap-1.5 text-xs font-mono font-medium tracking-widest uppercase ${cat.text}`}>
            <span className={`w-1.5 h-1.5 rounded-full ${cat.dot}`} />
            {cat.label}
          </span>
          <span className="text-xs font-mono text-ink-400">
            {formatDate(card.publishedAt ?? card.collectedAt)}
          </span>
        </div>

        {/* 제목 */}
        <h2 className="
          font-display text-ink-100 leading-snug
          text-[1.05rem] flex-1
          group-hover:text-white transition-colors duration-200
          line-clamp-3
        ">
          {card.title}
        </h2>

        {/* 하단: 출처 + 화살표 */}
        <div className="flex items-center justify-between mt-auto pt-3 border-t border-ink-700">
          <span className="text-xs text-ink-400 font-body truncate max-w-[70%]">
            {card.sourceName}
          </span>
          <span className="
            text-ink-600 group-hover:text-ink-200
            transition-all duration-200
            group-hover:translate-x-1
            text-sm
          ">
            →
          </span>
        </div>
      </article>
    </a>
  )
}
