import Vue from "vue";
import axios from "axios";
import VueAxios from "vue-axios";
import JwtService from "@/common/jwt.service";

const ApiService = {
  init() {
    Vue.use(VueAxios, axios);
    Vue.axios.defaults.baseURL = process.env.VUE_APP_API_URL;
  },
  setHeader() {
    Vue.axios.defaults.headers.common["Authorization"] = `Token ${JwtService.getToken()}`;
  },
  query(resource, params) {
    return Vue.axios.get(resource, params).catch(error => {
      throw new Error(`[RWV] ApiService ${error}`);
    });
  },
  get(resource) {
    return Vue.axios.get(resource).catch(error => {
      throw new Error(`[RWV] ApiService ${error}`);
    });
  },
  post(resource, params) {
    return Vue.axios.post(resource, params);
  },
  put(resource, params) {
    return Vue.axios.put(resource, params);
  },
  delete(resource) {
    return Vue.axios.delete(resource).catch(error => {
      throw new Error(`[RWV] ApiService ${error}`);
    });
  }
};

export default ApiService;

export const TagsService = {
  get() {
    return ApiService.get("tags");
  }
};

export const ArticlesService = {
  query(type, params, username) {
    if (type === "history") {
      return ApiService.query(`profiles/${username}/history`, { params });
    }
    params.isPublished = (type !== "drafts");
    return ApiService.query("articles" + (type === "feed" ? "/feed" : ""), { params });
  },
  get(slug) {
    return ApiService.get(`articles/${slug}`);
  },
  create(params) {
    return ApiService.post("articles", { article: params });
  },
  createDraft(params) {
    return ApiService.post("articles/draft", { article: params });
  },
  update(slug, params) {
    return ApiService.put(`articles/${slug}`, { article: params });
  },
  updateDraft(slug, params) {
    return ApiService.put(`articles/draft/${slug}`, { article: params });
  },
  destroy(slug) {
    return ApiService.delete(`articles/${slug}`);
  }
};

export const CommentsService = {
  get(slug) {
    if (typeof slug !== "string") {
      throw new Error(
        "[RWV] CommentsService.get() article slug required to fetch comments"
      );
    }
    return ApiService.get(`articles/${slug}/comments`);
  },
  post(slug, payload) {
    return ApiService.post(`articles/${slug}/comments`, {
      comment: { body: payload }
    });
  },
  destroy(slug, commentId) {
    return ApiService.delete(`articles/${slug}/comments/${commentId}`);
  }
};

export const FavoriteService = {
  add(slug) {
    return ApiService.post(`articles/${slug}/favorite`);
  },
  remove(slug) {
    return ApiService.delete(`articles/${slug}/favorite`);
  }
};
