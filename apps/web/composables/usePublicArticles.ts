import type { Article } from '@cnbjti/types'

export function usePublicArticles() {
  return useAsyncData<Article[]>(
    'public-resource-articles',
    () => publicApi<Article[]>('/public/articles'),
    {
      default: () => [],
    },
  )
}
