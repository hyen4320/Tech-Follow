interface Props {
  current: number
  total: number
  onChange: (page: number) => void
}

export default function Pagination({ current, total, onChange }: Props) {
  if (total <= 1) return null

  const pages: (number | '...')[] = []
  if (total <= 7) {
    for (let i = 0; i < total; i++) pages.push(i)
  } else {
    pages.push(0)
    if (current > 3) pages.push('...')
    for (let i = Math.max(1, current - 1); i <= Math.min(total - 2, current + 1); i++) pages.push(i)
    if (current < total - 4) pages.push('...')
    pages.push(total - 1)
  }

  return (
    <div className="flex items-center justify-center gap-1 mt-12">
      <button
        onClick={() => onChange(current - 1)}
        disabled={current === 0}
        className="w-9 h-9 flex items-center justify-center rounded-lg text-ink-400
          hover:text-ink-100 hover:bg-ink-800 disabled:opacity-30 disabled:cursor-not-allowed
          transition-all duration-150 font-mono"
      >
        ‹
      </button>

      {pages.map((p, i) =>
        p === '...' ? (
          <span key={`dot-${i}`} className="w-9 h-9 flex items-center justify-center text-ink-600 font-mono">
            ···
          </span>
        ) : (
          <button
            key={p}
            onClick={() => onChange(p as number)}
            className={`w-9 h-9 flex items-center justify-center rounded-lg text-sm font-mono
              transition-all duration-150
              ${p === current
                ? 'bg-ink-700 text-ink-100 border border-ink-600'
                : 'text-ink-400 hover:text-ink-100 hover:bg-ink-800'
              }`}
          >
            {(p as number) + 1}
          </button>
        )
      )}

      <button
        onClick={() => onChange(current + 1)}
        disabled={current === total - 1}
        className="w-9 h-9 flex items-center justify-center rounded-lg text-ink-400
          hover:text-ink-100 hover:bg-ink-800 disabled:opacity-30 disabled:cursor-not-allowed
          transition-all duration-150 font-mono"
      >
        ›
      </button>
    </div>
  )
}
