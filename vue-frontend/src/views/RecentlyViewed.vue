<template>
  <div class="profile-page">
    <RwvArticleList :items-per-page="5" type="history" />
  </div>
</template>

<script>
import RwvArticleList from "@/components/ArticleList";

export default {
  name: "RwvRecentlyViewed",
  components: {
    RwvArticleList
  },
  computed: {
    viewer() {
      return this.$route.params.username;
    }
  }
};
</script>
