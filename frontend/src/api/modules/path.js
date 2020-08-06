import ApiService from '@/api'

const PathService = {
  get(source, target) {
    return ApiService.get(`/paths?source=${source}&target=${target}&type=DISTANCE`)
  }
}

export default PathService
