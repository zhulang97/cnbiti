export default defineEventHandler((event) => {
  const url = getRequestURL(event)
  if (!/^\/(?:en|zh|de|es)(?:\/|$)/i.test(url.pathname)) return

  const targetPath = url.pathname.replace(/^\/(?:en|zh|de|es)(?=\/|$)/i, '') || '/'
  return sendRedirect(event, `${targetPath}${url.search}`, 301)
})
