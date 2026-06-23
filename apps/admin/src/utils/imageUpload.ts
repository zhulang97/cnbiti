const IMAGE_COMPRESS_MIN_BYTES = 1.5 * 1024 * 1024
const IMAGE_MAX_DIMENSION = 1920
const IMAGE_JPEG_QUALITY = 0.82

const passthroughTypes = new Set(['image/gif', 'image/svg+xml'])

export async function prepareUploadFile(file: File): Promise<File> {
  if (!shouldCompress(file)) {
    return file
  }
  try {
    return await compressImage(file)
  } catch {
    return file
  }
}

function shouldCompress(file: File) {
  return file.size > IMAGE_COMPRESS_MIN_BYTES
    && file.type.startsWith('image/')
    && !passthroughTypes.has(file.type)
}

async function compressImage(file: File) {
  const image = await loadImage(file)
  const scale = Math.min(1, IMAGE_MAX_DIMENSION / Math.max(image.naturalWidth, image.naturalHeight))
  if (scale === 1 && file.size <= IMAGE_COMPRESS_MIN_BYTES * 2) {
    return file
  }

  const canvas = document.createElement('canvas')
  canvas.width = Math.max(1, Math.round(image.naturalWidth * scale))
  canvas.height = Math.max(1, Math.round(image.naturalHeight * scale))
  const context = canvas.getContext('2d')
  if (!context) {
    return file
  }
  context.drawImage(image, 0, 0, canvas.width, canvas.height)

  const blob = await new Promise<Blob | null>((resolve) => {
    canvas.toBlob(resolve, 'image/jpeg', IMAGE_JPEG_QUALITY)
  })
  if (!blob || blob.size >= file.size) {
    return file
  }

  return new File([blob], compressedFilename(file.name), {
    type: 'image/jpeg',
    lastModified: file.lastModified,
  })
}

function loadImage(file: File) {
  return new Promise<HTMLImageElement>((resolve, reject) => {
    const url = URL.createObjectURL(file)
    const image = new Image()
    image.onload = () => {
      URL.revokeObjectURL(url)
      resolve(image)
    }
    image.onerror = () => {
      URL.revokeObjectURL(url)
      reject(new Error('Unable to read image'))
    }
    image.src = url
  })
}

function compressedFilename(filename: string) {
  const base = filename.replace(/\.[^.]+$/, '') || 'image'
  return `${base}.jpg`
}
