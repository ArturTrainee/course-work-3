<template>
  <div class="home-page">
    <div class="banner">
      <div class="container">
        <h1 class="logo-font">SmartReads</h1>
        <p>A place to share your knowledge.</p>
      </div>
    </div>
    <div class="container page">
      <h5 class="font-weight-bold m-b-1">Trending on SmartReads</h5>
      <div class="container">
        <div class="row">
          <div
            class="col-md-4"
            v-for="(article, index) in trendingArticles.slice(0, 3)"
            :key="article.title + index"
          >
            <h5 class="m-b-0">0{{index + 1}}</h5>
            <ArticlePreviewCard v-bind:article="article" />
          </div>
          <div class="w-100" />
          <div
            class="col-md-4"
            v-for="(article, index) in trendingArticles.slice(3)"
            :key="article.title + index"
          >
            <h5 class="m-b-0">0{{index + 4}}</h5>
            <ArticlePreviewCard v-bind:article="article" />
          </div>
        </div>
      </div>
      <div class="row m-t-1">
        <div class="col-md-9">
          <div class="feed-toggle">
            <ul class="nav nav-pills outline-active">
              <li v-if="isAuthenticated" class="nav-item">
                <router-link
                  :to="{ name: 'home-my-feed' }"
                  class="nav-link"
                  active-class="active"
                >
                  Your Feed
                </router-link>
              </li>
              <li class="nav-item">
                <router-link
                  :to="{ name: 'home' }"
                  exact
                  class="nav-link"
                  active-class="active"
                >
                  Global Feed
                </router-link>
              </li>
              <li class="nav-item" v-if="tag">
                <router-link
                  :to="{ name: 'home-tag', params: { tag } }"
                  class="nav-link"
                  active-class="active"
                >
                  <em class="ion-pound" /> {{ tag }}
                </router-link>
              </li>
            </ul>
          </div>
          <router-view></router-view>
        </div>
        <div class="col-md-3">
          <div class="sidebar">
            <p>Popular Tags</p>
            <div class="tag-list">
              <RwvTag v-for="(tag, index) in tags" :name="tag" :key="index" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import RwvTag from "@/components/VTag";
import ArticlePreviewCard from "@/components/ArticlePreviewCard";
import { FETCH_TAGS, FETCH_TRENDING_ARTICLES } from "@/store/actions.type";

export default {
  name: "home",
  components: {
    RwvTag,
    ArticlePreviewCard
  },
  mounted() {
    this.$store.dispatch(FETCH_TAGS);
    this.$store.dispatch(FETCH_TRENDING_ARTICLES, { limit: 6, offset: 0 });
  },
  computed: {
    ...mapGetters(["isAuthenticated", "tags", "isLoading", "trendingArticles"]),
    tag() {
      return this.$route.params.tag;
    }
  }
};
</script>
