import type { Category } from '../types/trends'

const TABS: { label: string; value: Category | null }[] = [
  { label: 'All',      value: null },
  { label: 'AI',       value: 'AI' },
  { label: 'Backend',  value: 'BE' },
  { label: 'Security', value: 'SECURITY' },
]

const ACTIVE_COLOR: Record<string, string> = {
  AI:       'bg-signal-ai/10 text-signal-ai border-signal-ai/40',
  BE:       'bg-signal-be/10 text-signal-be border-signal-be/40',
  SECURITY: 'bg-signal-security/10 text-signal-security border-signal-security/40',
  ALL:      'bg-ink-700 text-ink-100 border-ink-600',
}

interface Props {
  active: Category | null
  onChange: (c: Category | null) => void
}

export default function CategoryFilter({ active, onChange }: Props) {
  return (
    <div className="flex gap-2 flex-wrap">
      {TABS.map(tab => {
        const isActive = tab.value === active
        const colorKey = tab.value ?? 'ALL'
        return (
          <button
            key={colorKey}
            onClick={() => onChange(tab.value)}
            className={`
              px-4 py-1.5 rounded-full text-sm font-body font-medium border
              transition-all duration-200
              ${isActive
                ? ACTIVE_COLOR[colorKey]
                : 'bg-transparent text-ink-400 border-ink-700 hover:border-ink-500 hover:text-ink-200'
              }
            `}
          >
            {tab.label}
          </button>
        )
      })}
    </div>
  )
}
