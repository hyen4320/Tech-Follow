export default function SkeletonCard() {
  return (
    <div className="h-[200px] bg-ink-900 border border-ink-700 rounded-2xl p-6 flex flex-col gap-4">
      <div className="flex items-center justify-between">
        <div className="h-3 w-16 rounded-full bg-ink-700 shimmer-box" />
        <div className="h-3 w-12 rounded-full bg-ink-700 shimmer-box" />
      </div>
      <div className="flex-1 flex flex-col gap-2">
        <div className="h-4 w-full rounded-full bg-ink-700 shimmer-box" />
        <div className="h-4 w-4/5 rounded-full bg-ink-700 shimmer-box" />
        <div className="h-4 w-3/5 rounded-full bg-ink-700 shimmer-box" />
      </div>
      <div className="flex items-center justify-between pt-3 border-t border-ink-700">
        <div className="h-3 w-24 rounded-full bg-ink-700 shimmer-box" />
      </div>
    </div>
  )
}
