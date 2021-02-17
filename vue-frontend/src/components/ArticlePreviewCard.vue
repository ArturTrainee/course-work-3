<template>
  <div class="article-preview">
    <RwvArticleMeta :article="article" :showActions="false" />
    <router-link :to="articleLink" class="preview-link">
      <h6 class="font-weight-bold" v-text="article.title" />
      <p class="description" v-text="article.description" />
      <span>Read more...</span>
    </router-link>
  </div>
</template>

<script>
import RwvArticleMeta from "./ArticleMeta";
import TagList from "./TagList";

export default {
  name: "ArticlePreviewCard",
  components: {
    RwvArticleMeta,
    TagList
  },
  props: {
    article: { type: Object, required: true }
  },
  computed: {
    articleLink() {
      return {
        name: "article",
        params: {
          slug: this.article.slug
        }
      };
    }
  }
};
</script>
<style scoped>
  .article-preview {
    border-top: none;
    line-height: 1;
  }
  .description {
    word-break: break-word;
    max-width: 90%;
  }
</style>
