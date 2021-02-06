<template>
  <div class="profile-page">
    <RwvArticleList :author="author" :items-per-page="5" type="drafts" />
  </div>
</template>

<script>
import RwvArticleList from "@/components/ArticleList";

export default {
  name: "RwvProfileDrafts",
  components: {
    RwvArticleList
  },
  computed: {
    author() {
      return this.$route.params.username;
    }
  }
};
</script>
